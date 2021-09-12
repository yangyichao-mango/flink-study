package flink.examples.sql._03.source_sink.table.redis.v1.sink;//package flink.examples.sql._03.source_sink.table.redis.v1.sink;
//
//import javax.annotation.Nullable;
//
//import org.apache.flink.api.common.serialization.DeserializationSchema;
//import org.apache.flink.streaming.connectors.redis.RedisSink;
//import org.apache.flink.table.connector.ChangelogMode;
//import org.apache.flink.table.connector.format.DecodingFormat;
//import org.apache.flink.table.connector.sink.DynamicTableSink;
//import org.apache.flink.table.connector.sink.SinkFunctionProvider;
//import org.apache.flink.table.data.RowData;
//import org.apache.flink.table.types.DataType;
//import org.apache.flink.util.Preconditions;
//
//import flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions;
//
//public class RedisDynamicTableSink implements DynamicTableSink {
//
//    /**
//     * Data type to configure the formats.
//     */
//    protected final DataType physicalDataType;
//
//    /**
//     * Optional format for decoding keys from Kafka.
//     */
//    protected final @Nullable
//    DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
//
//    protected final RedisWriteOptions redisWriteOptions;
//
//    public RedisDynamicTableSink(DataType physicalDataType
//            , DecodingFormat<DeserializationSchema<RowData>> decodingFormat
//            , RedisWriteOptions redisWriteOptions) {
//
//        // Format attributes
//        this.physicalDataType =
//                Preconditions.checkNotNull(
//                        physicalDataType, "Physical data type must not be null.");
//        this.decodingFormat = decodingFormat;
//        this.redisWriteOptions = redisWriteOptions;
//    }
//
//
//    @Override
//    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
//        return null;
//    }
//
//    @Override
//    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
//        return SinkFunctionProvider.of(new RedisSink<RowData>(flinkJedisConfigBase, redisMapper));
//    }
//
//    @Override
//    public DynamicTableSink copy() {
//        return new RedisDynamicTableSink(tableSchema, config);
//    }
//
//    @Override
//    public String asSummaryString() {
//        return "REDIS";
//    }
//}
