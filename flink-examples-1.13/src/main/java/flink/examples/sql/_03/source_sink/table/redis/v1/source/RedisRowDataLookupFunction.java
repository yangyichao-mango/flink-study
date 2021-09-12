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

package flink.examples.sql._03.source_sink.table.redis.v1.source;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.shaded.guava18.com.google.common.cache.Cache;
import org.apache.flink.shaded.guava18.com.google.common.cache.CacheBuilder;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.TableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import flink.examples.sql._03.source_sink.table.redis.options.RedisLookupOptions;
import redis.clients.jedis.Jedis;

/**
 * The RedisRowDataLookupFunction is a standard user-defined table function, it can be used in
 * tableAPI and also useful for temporal table join plan in SQL. It looks up the result as {@link
 * RowData}.
 */
@Internal
public class RedisRowDataLookupFunction extends TableFunction<RowData> {

    private static final Logger LOG = LoggerFactory.getLogger(RedisRowDataLookupFunction.class);
    private static final long serialVersionUID = 1L;

    private transient Jedis jedis;

    private final String hostname;
    private final int port;

    private final long cacheMaxSize;
    private final long cacheExpireMs;
    private final int maxRetryTimes;
    private transient Cache<Object, RowData> cache;
    private final SerializationSchema<Object[]> keySerializationSchema;
    private final DeserializationSchema<RowData> valueDeserializationSchema;

    private transient Consumer<Object[]> evaler;

    public RedisRowDataLookupFunction(
            RedisLookupOptions lookupOptions
            , DeserializationSchema<RowData> valueDeserializationSchema) {
        this.hostname = lookupOptions.getHostname();
        this.port = lookupOptions.getPort();
        this.cacheMaxSize = lookupOptions.getCacheMaxSize();
        this.cacheExpireMs = lookupOptions.getCacheExpireMs();
        this.maxRetryTimes = lookupOptions.getMaxRetryTimes();
        this.valueDeserializationSchema = valueDeserializationSchema;
        this.keySerializationSchema = elements -> Joiner.on(":").join(elements).getBytes();
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
        this.jedis = new Jedis(this.hostname, this.port);

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
                    byte[] key = this.keySerializationSchema.serialize(in);
                    byte[] result = this.jedis.get(key);
                    if (null != result && result.length > 0) {

                        RowData rowData = null;
                        try {
                            rowData = this.valueDeserializationSchema.deserialize(result);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // parse and collect
                        collect(rowData);
                        cache.put(key, rowData);
                    }
                }
            };

        } else {
            this.evaler = in -> {
                // fetch result
                byte[] key = this.keySerializationSchema.serialize(in);
                byte[] result = this.jedis.get(key);

                if (null != result && result.length > 0) {

                    RowData rowData = null;
                    try {
                        rowData = this.valueDeserializationSchema.deserialize(result);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // parse and collect
                    collect(rowData);
                }
            };
        }

        LOG.info("end open.");
    }

    @Override
    public void close() {
        LOG.info("start close ...");
        if (null != jedis) {
            this.jedis.close();
            this.jedis = null;
        }
        LOG.info("end close.");
    }
}
