package flink.examples.sql._05.format.formats.protobuf.rowdata;

import static flink.examples.sql._05.format.formats.protobuf.rowdata.ProtobufOptions.PROTOBUF_CLASS_NAME;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DeserializationFormatFactory;
import org.apache.flink.table.factories.DynamicTableFactory.Context;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.SerializationFormatFactory;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.RowType;

import com.google.protobuf.GeneratedMessageV3;


public class ProtobufFormatFactory implements DeserializationFormatFactory, SerializationFormatFactory {

    public static final String IDENTIFIER = "protobuf";


    @Override
    public DecodingFormat<DeserializationSchema<RowData>> createDecodingFormat(Context context,
            ReadableConfig formatOptions) {

        FactoryUtil.validateFactoryOptions(this, formatOptions);

        final String className = formatOptions.get(PROTOBUF_CLASS_NAME);

        try {
            Class<GeneratedMessageV3> protobufV3 =
                    (Class<GeneratedMessageV3>) this.getClass().getClassLoader().loadClass(className);

            return new DecodingFormat<DeserializationSchema<RowData>>() {
                @Override
                public DeserializationSchema<RowData> createRuntimeDecoder(DynamicTableSource.Context context,
                        DataType physicalDataType) {
                    final RowType rowType = (RowType) physicalDataType.getLogicalType();

                    return new ProtobufRowDataDeserializationSchema(
                            protobufV3
                            , true
                            , rowType);
                }

                @Override
                public ChangelogMode getChangelogMode() {
                    return ChangelogMode.insertOnly();
                }
            };
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EncodingFormat<SerializationSchema<RowData>> createEncodingFormat(Context context,
            ReadableConfig formatOptions) {
        return null;
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {

        Set<ConfigOption<?>> optionalOptions = new HashSet<>();

        optionalOptions.add(PROTOBUF_CLASS_NAME);

        return optionalOptions;
    }
}
