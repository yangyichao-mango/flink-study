package flink.examples.sql._03.source_sink.table.redis.v2.sink;

import javax.annotation.Nullable;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.utils.DataTypeUtils;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Preconditions;

import flink.examples.sql._03.source_sink.table.redis.mapper.SetRedisMapper;
import flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions;

/**
 * https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/table/sourcessinks/
 *
 * https://www.alibabacloud.com/help/zh/faq-detail/118038.htm?spm=a2c63.q38357.a3.16.48fa711fo1gVUd
 */
public class RedisDynamicTableSink implements DynamicTableSink {

    /**
     * Data type to configure the formats.
     */
    protected final DataType physicalDataType;

    protected final RedisWriteOptions redisWriteOptions;

    public RedisDynamicTableSink(
            DataType physicalDataType
            , RedisWriteOptions redisWriteOptions) {

        // Format attributes
        this.physicalDataType =
                Preconditions.checkNotNull(
                        physicalDataType, "Physical data type must not be null.");
        this.redisWriteOptions = redisWriteOptions;
    }

    private @Nullable
    SerializationSchema<RowData> createSerialization(
            Context context,
            @Nullable EncodingFormat<SerializationSchema<RowData>> format,
            int[] projection) {
        if (format == null) {
            return null;
        }
        DataType physicalFormatDataType =
                DataTypeUtils.projectRow(this.physicalDataType, projection);
        return format.createRuntimeEncoder(context, physicalFormatDataType);
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        // UPSERT mode
        ChangelogMode.Builder builder = ChangelogMode.newBuilder();
        for (RowKind kind : requestedMode.getContainedKinds()) {
            if (kind != RowKind.UPDATE_BEFORE) {
                builder.addContainedKind(kind);
            }
        }
        return builder.build();
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        FlinkJedisConfigBase flinkJedisConfigBase = new FlinkJedisPoolConfig.Builder()
                .setHost(this.redisWriteOptions.getHostname())
                .setPort(this.redisWriteOptions.getPort())
                .build();

        RedisMapper<RowData> redisMapper = null;

        switch (this.redisWriteOptions.getWriteMode()) {
            case "string":
                redisMapper = new SetRedisMapper();
                break;
            default:
                throw new RuntimeException("其他类型 write mode 请自定义实现");
        }

        return SinkFunctionProvider.of(new RedisSink<>(
                flinkJedisConfigBase
                , redisMapper));
    }

    @Override
    public DynamicTableSink copy() {
        return null;
    }

    @Override
    public String asSummaryString() {
        return "redis";
    }
}
