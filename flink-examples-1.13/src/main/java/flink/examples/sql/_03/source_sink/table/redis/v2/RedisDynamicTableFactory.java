package flink.examples.sql._03.source_sink.table.redis.v2;

import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.HOSTNAME;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.LOOKUP_CACHE_MAX_ROWS;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.LOOKUP_CACHE_TTL;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.LOOKUP_MAX_RETRIES;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisOptions.PORT;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions.BATCH_SIZE;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions.IS_BATCH_MODE;
import static flink.examples.sql._03.source_sink.table.redis.options.RedisWriteOptions.WRITE_MODE;

import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
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
import flink.examples.sql._03.source_sink.table.redis.v2.sink.RedisDynamicTableSink;
import flink.examples.sql._03.source_sink.table.redis.v2.source.RedisDynamicTableSource;


public class RedisDynamicTableFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    @Override
    public String factoryIdentifier() {
        return "redis";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(HOSTNAME);
        options.add(PORT);
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(FactoryUtil.FORMAT); // use pre-defined option for format
        options.add(LOOKUP_CACHE_MAX_ROWS);
        options.add(LOOKUP_CACHE_TTL);
        options.add(LOOKUP_MAX_RETRIES);
        options.add(WRITE_MODE);
        options.add(IS_BATCH_MODE);
        options.add(BATCH_SIZE);
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

        Configuration c = (Configuration) context.getConfiguration();

        boolean isDimBatchMode = c.getBoolean("is.dim.batch.mode", false);

        return new RedisDynamicTableSource(
                schema.toPhysicalRowDataType()
                , decodingFormat
                , redisLookupOptions
                , isDimBatchMode);
    }

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {

        // either implement your custom validation logic here ...
        // or use the provided helper utility
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
//        final EncodingFormat<SerializationSchema<RowData>> encodingFormat = helper.discoverEncodingFormat(
//                SerializationFormatFactory.class,
//                FactoryUtil.FORMAT);

        // validate all options
        helper.validate();

        // get the validated options
        final ReadableConfig options = helper.getOptions();

        final RedisWriteOptions redisWriteOptions = RedisOptions.getRedisWriteOptions(options);

        TableSchema schema = context.getCatalogTable().getSchema();

        return new RedisDynamicTableSink(schema.toPhysicalRowDataType()
                , redisWriteOptions);
    }
}