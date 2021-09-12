package flink.examples.sql._03.source_sink.table.redis.v1;

import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.HOSTNAME;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.PORT;

import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DeserializationFormatFactory;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;

import flink.examples.sql._03.source_sink.table.redis.options.RedisLookupOptions;
import flink.examples.sql._03.source_sink.table.redis.options.RedisOptions;
import flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions;
import flink.examples.sql._03.source_sink.table.redis.v1.source.RedisDynamicTableSource;

//import flink.examples.sql._03.source_sink.table.redis.v1.sink.RedisDynamicTableSink;


public class RedisDynamicTableFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {

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

        final RedisWriteOptions redisWriteOptions = RedisOptions.getRedisWriteOptions(options);

        TableSchema schema = context.getCatalogTable().getSchema();

//        return new RedisDynamicTableSink(
//                schema.toPhysicalRowDataType()
//                , decodingFormat
//                , redisWriteOptions);

        return null;
    }

    @Override
    public String factoryIdentifier() {
        return "redis";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(HOSTNAME);
        options.add(PORT);
        options.add(FactoryUtil.FORMAT); // use pre-defined option for format
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        //        options.add(COMMAND);
        //        options.add(KEY_COLUMN);
        //        options.add(VALUE_COLUMN);
        //        options.add(FIELD_COLUMN);
        //        options.add(TTL);
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

        final RedisLookupOptions redisLookupOptions = RedisOptions.getRedisLookupOptions(options);

        TableSchema schema = context.getCatalogTable().getSchema();

        return new RedisDynamicTableSource(
                schema.toPhysicalRowDataType()
                , decodingFormat
                , redisLookupOptions);
    }
}