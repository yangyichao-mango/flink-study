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
import org.apache.flink.table.api.TableColumn.MetadataColumn;
import org.apache.flink.table.api.TableSchema;
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
import org.apache.flink.table.utils.TableSchemaUtils;

import com.google.common.collect.Lists;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    private final DataType sourceRowDataType;
    private final DataType producedDataType;
    private TableSchema physicalSchema;
    private TableSchema tableSchema;
    private long limit = -1;
    private WatermarkStrategy<RowData> watermarkStrategy;
    private boolean enableSourceWatermark;
    private List<ResolvedExpression> filters;

    public Abilities_TableSource(
            String className,
            DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
            DataType sourceRowDataType,
            DataType producedDataType,
            TableSchema physicalSchema,
            TableSchema tableSchema) {
        this.className = className;
        this.decodingFormat = decodingFormat;
        this.sourceRowDataType = sourceRowDataType;
        this.producedDataType = producedDataType;
        this.physicalSchema = physicalSchema;
        this.tableSchema = tableSchema;
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
                getSchemaWithMetadata(this.tableSchema).toRowDataType());

        Class<?> clazz = this.getClass().getClassLoader().loadClass(className);

        RichSourceFunction<RowData> r;

        if (limit > 0) {
            r = (RichSourceFunction<RowData>) clazz.getConstructor(DeserializationSchema.class, long.class).newInstance(deserializer, this.limit);
        } else if (enableSourceWatermark) {
            r = (RichSourceFunction<RowData>) clazz.getConstructor(DeserializationSchema.class, boolean.class).newInstance(deserializer, this.enableSourceWatermark);
        } else {
            r = (RichSourceFunction<RowData>) clazz.getConstructor(DeserializationSchema.class).newInstance(deserializer);
        }

        return SourceFunctionProvider.of(r, false);
    }

    @Override
    public DynamicTableSource copy() {
        return new Abilities_TableSource(className, decodingFormat, sourceRowDataType, producedDataType, physicalSchema, tableSchema);
    }

    @Override
    public String asSummaryString() {
        return "Socket Table Source";
    }

    @Override
    public Result applyFilters(List<ResolvedExpression> filters) {

        this.filters = new LinkedList<>(filters);

        // 不上推任何过滤条件
//        return Result.of(Lists.newLinkedList(), filters);
        // 将所有的过滤条件都上推到 source
        return Result.of(filters, Lists.newLinkedList());
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
        this.tableSchema = projectSchemaWithMetadata(this.tableSchema, projectedFields);
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
        log.info("Successfully applyWatermark");

        this.watermarkStrategy = watermarkStrategy;
    }

    @Override
    public void applySourceWatermark() {
        log.info("Successfully applySourceWatermark");

        this.enableSourceWatermark = true;
    }

    public static TableSchema projectSchemaWithMetadata(TableSchema tableSchema, int[][] projectedFields) {

        TableSchema.Builder builder = new TableSchema.Builder();
        TableSchema physicalProjectedSchema = TableSchemaUtils.projectSchema(TableSchemaUtils.getPhysicalSchema(tableSchema), projectedFields);

        physicalProjectedSchema
                .getTableColumns()
                .forEach(
                        tableColumn -> {
                            if (tableColumn.isPhysical()) {
                                builder.field(tableColumn.getName(), tableColumn.getType());
                            }
                        });

        tableSchema
                .getTableColumns()
                .forEach(
                        tableColumn -> {
                            if (tableColumn instanceof MetadataColumn) {
                                builder.field(tableColumn.getName(), tableColumn.getType());
                            }
                        });
        return builder.build();
    }

    public static TableSchema getSchemaWithMetadata(TableSchema tableSchema) {

        TableSchema.Builder builder = new TableSchema.Builder();

        tableSchema
                .getTableColumns()
                .forEach(
                        tableColumn -> {
                            if (tableColumn.isPhysical()) {
                                builder.field(tableColumn.getName(), tableColumn.getType());
                            } else if (tableColumn instanceof MetadataColumn) {
                                builder.field(tableColumn.getName(), tableColumn.getType());
                            }
                        });
        return builder.build();
    }
}
