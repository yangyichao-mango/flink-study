package flink.examples.sql._03.source_sink.abilities.source;

import java.util.HashMap;
import java.util.LinkedList;
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
import org.apache.flink.table.types.FieldsDataType;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.types.Row;

import com.google.common.collect.Lists;

import lombok.SneakyThrows;


public class Abilities_TableSource implements ScanTableSource
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
    private long limit;

    public Abilities_TableSource(
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

        FieldsDataType deser = (FieldsDataType) producedDataType;

        List<DataType> dataTypes = new LinkedList<>(deser.getChildren());

        dataTypes.add(DataTypes.BIGINT());

        List<RowType.RowField> logicalTypes = new LinkedList<>(((RowType) deser.getLogicalType()).getFields());

        logicalTypes.add(new RowType.RowField("flink_read_timestamp", new BigIntType()));

        RowType rowType = new RowType(logicalTypes);

        final DeserializationSchema<RowData> deserializer = decodingFormat.createRuntimeDecoder(
                runtimeProviderContext,
                new FieldsDataType(rowType, Row.class, dataTypes));

        Class<?> clazz = this.getClass().getClassLoader().loadClass(className);

        RichSourceFunction<RowData> r;

        if (0 == limit) {
            r = (RichSourceFunction<RowData>) clazz.getConstructors()[0].newInstance(deserializer);
        } else {
            r = (RichSourceFunction<RowData>) clazz.getConstructors()[1].newInstance(deserializer, this.limit);
        }

        return SourceFunctionProvider.of(r, false);
    }

    @Override
    public DynamicTableSource copy() {
        return new Abilities_TableSource(className, decodingFormat, producedDataType);
    }

    @Override
    public String asSummaryString() {
        return "Socket Table Source";
    }

    @Override
    public Result applyFilters(List<ResolvedExpression> filters) {
        // 不上推任何过滤条件
        return Result.of(Lists.newLinkedList(), filters);
        // 将所有的过滤条件都上推到 source
//        return Result.of(filters, Lists.newLinkedList());
    }

    @Override
    public void applyLimit(long limit) {
        this.limit = limit;
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
