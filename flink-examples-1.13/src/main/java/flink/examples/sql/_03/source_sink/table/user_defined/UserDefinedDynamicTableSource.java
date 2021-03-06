package flink.examples.sql._03.source_sink.table.user_defined;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.connector.source.abilities.SupportsFilterPushDown;
import org.apache.flink.table.connector.source.abilities.SupportsLimitPushDown;
import org.apache.flink.table.connector.source.abilities.SupportsPartitionPushDown;
import org.apache.flink.table.connector.source.abilities.SupportsProjectionPushDown;
import org.apache.flink.table.connector.source.abilities.SupportsReadingMetadata;
import org.apache.flink.table.connector.source.abilities.SupportsSourceWatermark;
import org.apache.flink.table.connector.source.abilities.SupportsWatermarkPushDown;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.expressions.ResolvedExpression;
import org.apache.flink.table.types.DataType;

import com.google.common.collect.Lists;

import lombok.SneakyThrows;


public class UserDefinedDynamicTableSource implements ScanTableSource
        , SupportsFilterPushDown // 过滤条件下推
        , SupportsLimitPushDown // limit 条件下推
        , SupportsPartitionPushDown //
        , SupportsProjectionPushDown // select 下推
        , SupportsReadingMetadata // 元数据
        , SupportsWatermarkPushDown
        , SupportsSourceWatermark {

    private final String className;
    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
    private final DataType producedDataType;

    public UserDefinedDynamicTableSource(
            String className,
            DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
            DataType producedDataType) {
        this.className = className;
        this.decodingFormat = decodingFormat;
        this.producedDataType = producedDataType;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        // in our example the format decides about the changelog mode
        // but it could also be the source itself
        return decodingFormat.getChangelogMode();
    }

    @SneakyThrows
    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {

        // create runtime classes that are shipped to the cluster

        final DeserializationSchema<RowData> deserializer = decodingFormat.createRuntimeDecoder(
                runtimeProviderContext,
                producedDataType);

        Map<String, DataType> readableMetadata = decodingFormat.listReadableMetadata();

        Class<?> clazz = this.getClass().getClassLoader().loadClass(className);

        RichSourceFunction<RowData> r = (RichSourceFunction<RowData>) clazz.getConstructors()[0].newInstance(deserializer);

        return SourceFunctionProvider.of(r, false);
    }

    @Override
    public DynamicTableSource copy() {
        return new UserDefinedDynamicTableSource(className, decodingFormat, producedDataType);
    }

    @Override
    public String asSummaryString() {
        return "Socket Table Source";
    }

    @Override
    public Result applyFilters(List<ResolvedExpression> filters) {
        return Result.of(Lists.newLinkedList(), filters);
    }

    @Override
    public void applyLimit(long limit) {
        System.out.println(1);
    }

    @Override
    public Optional<List<Map<String, String>>> listPartitions() {
        return Optional.empty();
    }

    @Override
    public void applyPartitions(List<Map<String, String>> remainingPartitions) {
        System.out.println(1);
    }

    @Override
    public boolean supportsNestedProjection() {
        return false;
    }

    @Override
    public void applyProjection(int[][] projectedFields) {
        System.out.println(1);
    }

    @Override
    public Map<String, DataType> listReadableMetadata() {
        return new HashMap<String, DataType>() {{
            put("flink_read_timestamp", DataTypes.BIGINT());
        }};
    }

    @Override
    public void applyReadableMetadata(List<String> metadataKeys, DataType producedDataType) {
        System.out.println(1);
    }

    @Override
    public void applyWatermark(WatermarkStrategy<RowData> watermarkStrategy) {
        System.out.println(1);
    }

    @Override
    public void applySourceWatermark() {
        System.out.println(1);
    }
}
