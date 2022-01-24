package flink.examples.sql._03.source_sink.abilities.source.before;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.api.TableColumn.MetadataColumn;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Before_Abilities_TableSource implements ScanTableSource {

    private final String className;
    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
    private final DataType sourceRowDataType;
    private final DataType producedDataType;
    private TableSchema physicalSchema;
    private TableSchema tableSchema;
    private long limit = -1;
    private WatermarkStrategy<RowData> watermarkStrategy;
    boolean enableSourceWatermark;

    public Before_Abilities_TableSource(
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
        return new Before_Abilities_TableSource(className, decodingFormat, sourceRowDataType, producedDataType, physicalSchema, tableSchema);
    }

    @Override
    public String asSummaryString() {
        return "Socket Table Source";
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
