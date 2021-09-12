package flink.examples.sql._03.source_sink.table.redis.options;

import static flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions.WRITE_MODE;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions.WRITE_TTL;
import static org.apache.flink.table.types.logical.utils.LogicalTypeChecks.hasRoot;

import java.time.Duration;
import java.util.stream.IntStream;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.table.types.logical.utils.LogicalTypeChecks;
import org.apache.flink.util.Preconditions;


public class RedisOptions {

    public static final ConfigOption<Integer> TIMEOUT = ConfigOptions
            .key("timeout")
            .intType()
            .defaultValue(2000)
            .withDescription("Optional timeout for connect to redis");

    public static final ConfigOption<Integer> MAXIDLE = ConfigOptions
            .key("maxIdle")
            .intType()
            .defaultValue(2)
            .withDescription("Optional maxIdle for connect to redis");

    public static final ConfigOption<Integer> MINIDLE = ConfigOptions
            .key("minIdle")
            .intType()
            .defaultValue(1)
            .withDescription("Optional minIdle for connect to redis");

    public static final ConfigOption<String> PASSWORD = ConfigOptions
            .key("password")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional password for connect to redis");

    public static final ConfigOption<Integer> PORT = ConfigOptions
            .key("port")
            .intType()
            .defaultValue(6379)
            .withDescription("Optional port for connect to redis");

    public static final ConfigOption<String> HOSTNAME = ConfigOptions
            .key("hostname")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional host for connect to redis");

    public static final ConfigOption<String> CLUSTERNODES = ConfigOptions
            .key("cluster-nodes")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional nodes for connect to redis cluster");

    public static final ConfigOption<Integer> DATABASE = ConfigOptions
            .key("database")
            .intType()
            .defaultValue(0)
            .withDescription("Optional database for connect to redis");


    public static final ConfigOption<String> COMMAND = ConfigOptions
            .key("command")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional command for connect to redis");

    public static final ConfigOption<String> REDISMODE = ConfigOptions
            .key("redis-mode")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional redis-mode for connect to redis");

    public static final ConfigOption<String> REDIS_MASTER_NAME = ConfigOptions
            .key("master.name")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional master.name for connect to redis sentinels");

    public static final ConfigOption<String> SENTINELS_INFO = ConfigOptions
            .key("sentinels.info")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional sentinels.info for connect to redis sentinels");

    public static final ConfigOption<String> SENTINELS_PASSWORD = ConfigOptions
            .key("sentinels.password")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional sentinels.password for connect to redis sentinels");

    public static final ConfigOption<String> KEY_COLUMN = ConfigOptions
            .key("key-column")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional key-column for insert to redis");

    public static final ConfigOption<String> VALUE_COLUMN = ConfigOptions
            .key("value-column")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional value_column for insert to redis");


    public static final ConfigOption<String> FIELD_COLUMN = ConfigOptions
            .key("field-column")
            .stringType()
            .noDefaultValue()
            .withDescription("Optional field_column for insert to redis");


    public static final ConfigOption<Boolean> PUT_IF_ABSENT = ConfigOptions
            .key("put-if-absent")
            .booleanType()
            .defaultValue(false)
            .withDescription("Optional put_if_absent for insert to redis");

    public static final ConfigOption<Boolean> LOOKUP_ASYNC =
            ConfigOptions.key("lookup.async")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("whether to set async lookup.");

    public static final ConfigOption<Long> LOOKUP_CACHE_MAX_ROWS =
            ConfigOptions.key("lookup.cache.max-rows")
                    .longType()
                    .defaultValue(-1L)
                    .withDescription(
                            "the max number of rows of lookup cache, over this value, the oldest rows will "
                                    + "be eliminated. \"cache.max-rows\" and \"cache.ttl\" options must all be "
                                    + "specified if any of them is "
                                    + "specified. Cache is not enabled as default.");

    public static final ConfigOption<Duration> LOOKUP_CACHE_TTL =
            ConfigOptions.key("lookup.cache.ttl")
                    .durationType()
                    .defaultValue(Duration.ofSeconds(0))
                    .withDescription("the cache time to live.");

    public static final ConfigOption<Integer> LOOKUP_MAX_RETRIES =
            ConfigOptions.key("lookup.max-retries")
                    .intType()
                    .defaultValue(3)
                    .withDescription("the max retry times if lookup database failed.");

    public static RedisLookupOptions getRedisLookupOptions(ReadableConfig tableOptions) {
        return (RedisLookupOptions) RedisLookupOptions
                .builder()
                .setLookupAsync(tableOptions.get(LOOKUP_ASYNC))
                .setMaxRetryTimes(tableOptions.get(LOOKUP_MAX_RETRIES))
                .setCacheExpireMs(tableOptions.get(LOOKUP_CACHE_TTL).toMillis())
                .setCacheMaxSize(tableOptions.get(LOOKUP_CACHE_MAX_ROWS))
                .setHostname(tableOptions.get(HOSTNAME))
                .setPort(tableOptions.get(PORT))
                .build();
    }

    public static RedisWriteOptions getRedisWriteOptions(ReadableConfig tableOptions) {
        return (RedisWriteOptions) RedisWriteOptions
                .builder()
                .setWriteTtl(tableOptions.get(WRITE_TTL))
                .setWriteMode(tableOptions.get(WRITE_MODE))
                .setHostname(tableOptions.get(HOSTNAME))
                .setPort(tableOptions.get(PORT))
                .build();
    }

    /**
     * Creates an array of indices that determine which physical fields of the table schema to
     * include in the value format.
     *
     * <p>See {@link #VALUE_FORMAT}, {@link #VALUE_FIELDS_INCLUDE}, and {@link #KEY_FIELDS_PREFIX}
     * for more information.
     */
    public static int[] createValueFormatProjection(
            DataType physicalDataType) {
        final LogicalType physicalType = physicalDataType.getLogicalType();
        Preconditions.checkArgument(
                hasRoot(physicalType, LogicalTypeRoot.ROW), "Row data type expected.");
        final int physicalFieldCount = LogicalTypeChecks.getFieldCount(physicalType);
        final IntStream physicalFields = IntStream.range(0, physicalFieldCount);

        return physicalFields.toArray();
    }

}
