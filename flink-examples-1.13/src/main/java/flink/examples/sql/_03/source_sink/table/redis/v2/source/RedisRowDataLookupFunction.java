/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flink.examples.sql._03.source_sink.table.redis.v2.source;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.flink.annotation.Internal;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.shaded.guava18.com.google.common.cache.Cache;
import org.apache.flink.shaded.guava18.com.google.common.cache.CacheBuilder;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.TableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flink.examples.sql._03.source_sink.table.redis.container.RedisCommandsContainer;
import flink.examples.sql._03.source_sink.table.redis.container.RedisCommandsContainerBuilder;
import flink.examples.sql._03.source_sink.table.redis.mapper.LookupRedisMapper;
import flink.examples.sql._03.source_sink.table.redis.mapper.RedisCommand;
import flink.examples.sql._03.source_sink.table.redis.mapper.RedisCommandDescription;
import flink.examples.sql._03.source_sink.table.redis.options.RedisLookupOptions;

/**
 * The RedisRowDataLookupFunction is a standard user-defined table function, it can be used in
 * tableAPI and also useful for temporal table join plan in SQL. It looks up the result as {@link
 * RowData}.
 */
@Internal
public class RedisRowDataLookupFunction extends TableFunction<RowData> {

    private static final Logger LOG = LoggerFactory.getLogger(
            RedisRowDataLookupFunction.class);
    private static final long serialVersionUID = 1L;

    private String additionalKey;
    private LookupRedisMapper lookupRedisMapper;
    private RedisCommand redisCommand;

    protected final RedisLookupOptions redisLookupOptions;

    private FlinkJedisConfigBase flinkJedisConfigBase;
    private RedisCommandsContainer redisCommandsContainer;

    private final long cacheMaxSize;
    private final long cacheExpireMs;
    private final int maxRetryTimes;

    private transient Cache<Object, RowData> cache;

    private transient Consumer<Object[]> evaler;

    public RedisRowDataLookupFunction(
            FlinkJedisConfigBase flinkJedisConfigBase
            , LookupRedisMapper lookupRedisMapper,
            RedisLookupOptions redisLookupOptions) {

        this.flinkJedisConfigBase = flinkJedisConfigBase;

        this.lookupRedisMapper = lookupRedisMapper;
        this.redisLookupOptions = redisLookupOptions;
        RedisCommandDescription redisCommandDescription = lookupRedisMapper.getCommandDescription();
        this.redisCommand = redisCommandDescription.getRedisCommand();
        this.additionalKey = redisCommandDescription.getAdditionalKey();

        cacheMaxSize = this.redisLookupOptions.getCacheMaxSize();
        cacheExpireMs = this.redisLookupOptions.getCacheExpireMs();
        maxRetryTimes = this.redisLookupOptions.getMaxRetryTimes();
    }

    /**
     * The invoke entry point of lookup function.
     *
     * @param objects the lookup key. Currently only support single rowkey.
     */
    public void eval(Object... objects) throws IOException {

        for (int retry = 0; retry <= maxRetryTimes; retry++) {
            try {
                // fetch result
                this.evaler.accept(objects);
                break;
            } catch (Exception e) {
                LOG.error(String.format("HBase lookup error, retry times = %d", retry), e);
                if (retry >= maxRetryTimes) {
                    throw new RuntimeException("Execution of HBase lookup failed.", e);
                }
                try {
                    Thread.sleep(1000 * retry);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }


    @Override
    public void open(FunctionContext context) {
        LOG.info("start open ...");

        try {
            this.redisCommandsContainer =
                    RedisCommandsContainerBuilder
                            .build(this.flinkJedisConfigBase);
            this.redisCommandsContainer.open();
        } catch (Exception e) {
            LOG.error("Redis has not been properly initialized: ", e);
            throw new RuntimeException(e);
        }

        this.cache = cacheMaxSize <= 0 || cacheExpireMs <= 0 ? null : CacheBuilder.newBuilder()
                .recordStats()
                .expireAfterWrite(cacheExpireMs, TimeUnit.MILLISECONDS)
                .maximumSize(cacheMaxSize)
                .build();

        if (cache != null) {
            context.getMetricGroup()
                    .gauge("lookupCacheHitRate", (Gauge<Double>) () -> cache.stats().hitRate());


            this.evaler = in -> {
                RowData cacheRowData = cache.getIfPresent(in);
                if (cacheRowData != null) {
                    collect(cacheRowData);
                } else {
                    // fetch result
                    byte[] key = lookupRedisMapper.serialize(in);

                    byte[] value = null;

                    switch (redisCommand) {
                        case GET:
                            value = this.redisCommandsContainer.get(key);
                            break;
                        case HGET:
                            value = this.redisCommandsContainer.hget(key, this.additionalKey.getBytes());
                            break;
                        default:
                            throw new IllegalArgumentException("Cannot process such data type: " + redisCommand);
                    }

                    RowData rowData = this.lookupRedisMapper.deserialize(value);

                    collect(rowData);

                    cache.put(key, rowData);
                }
            };

        } else {
            this.evaler = in -> {
                // fetch result
                byte[] key = lookupRedisMapper.serialize(in);

                byte[] value = null;

                switch (redisCommand) {
                    case GET:
                        value = this.redisCommandsContainer.get(key);
                        break;
                    case HGET:
                        value = this.redisCommandsContainer.hget(key, this.additionalKey.getBytes());
                        break;
                    default:
                        throw new IllegalArgumentException("Cannot process such data type: " + redisCommand);
                }

                RowData rowData = this.lookupRedisMapper.deserialize(value);

                collect(rowData);
            };
        }

        LOG.info("end open.");
    }

    @Override
    public void close() {
        LOG.info("start close ...");
        if (redisCommandsContainer != null) {
            try {
                redisCommandsContainer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.info("end close.");
    }
}
