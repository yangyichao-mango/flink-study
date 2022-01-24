package flink.examples.sql._03.source_sink.abilities.source.before;

import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DeserializationFormatFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.utils.TableSchemaUtils;


public class Before_Abilities_TableSourceFactory implements DynamicTableSourceFactory {

    // define all options statically
    public static final ConfigOption<String> CLASS_NAME = ConfigOptions.key("class.name")
            .stringType()
            .noDefaultValue();

    @Override
    public String factoryIdentifier() {
        return "before_supports_reading_metadata_user_defined"; // used for matching to `connector = '...'`
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(CLASS_NAME);
        options.add(FactoryUtil.FORMAT); // use pre-defined option for format
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        return options;
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        // either implement your custom validation logic here ...
        // or use the provided helper utility
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
        final DecodingFormat<DeserializationSchema<RowData>> decodingFormat = helper.discoverDecodingFormat(
                DeserializationFormatFactory.class,
                FactoryUtil.FORMAT);

        // validate all options
        helper.validate();

        // get the validated options
        final ReadableConfig options = helper.getOptions();
        final String className = options.get(CLASS_NAME);

        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType =
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();

        final DataType sourceRowDataType =
                context.getCatalogTable().getResolvedSchema().toSourceRowDataType();

        final DataType sinkRowDataType =
                context.getCatalogTable().getResolvedSchema().toSinkRowDataType();

        final Schema schema =
                context.getCatalogTable().getUnresolvedSchema();

        TableSchema physicalSchema =
                TableSchemaUtils.getPhysicalSchema(context.getCatalogTable().getSchema());


        TableSchema tableSchema = context.getCatalogTable().getSchema();

        // create and return dynamic table source
        return new Before_Abilities_TableSource(className
                , decodingFormat
                , sourceRowDataType
                , producedDataType
                , physicalSchema
                , tableSchema);
    }
}
