package flink.examples.sql._03.source_sink.ddl;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.descriptors.CustomConnectorDescriptor;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import flink.examples.sql._05.format.formats.protobuf.descriptors.Protobuf;


public class TableApiDDLTest {

    // https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/dev/table/sql/queries/overview/

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

//        flinkEnv.getStreamTableEnvironment().getConfig().getConfiguration().setString("table.exec.emit.early-fire.enabled", "true");
//        flinkEnv.getStreamTableEnvironment().getConfig().getConfiguration().setString("table.exec.emit.early-fire.delay", "60 s");

        String sql = "CREATE TABLE redis_sink_table (\n"
                + "    key STRING,\n"
                + "    `value` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'redis',\n"
                + "  'hostname' = '127.0.0.1',\n"
                + "  'port' = '6379',\n"
                + "  'write.mode' = 'string'\n"
                + ")";

        // create and register a TableSink
        final Schema schema = new Schema()
                .field("key", DataTypes.STRING())
                .field("value", DataTypes.STRING());

        flinkEnv.getStreamTableEnvironment()
                .connect(
                        new CustomConnectorDescriptor("redis", 1, true)
                        .property("hostname", "127.0.0.1")
                        .property("port", "6379")
                        .property("write.mode", "string")
                )
                .withFormat(new Protobuf())
                .withSchema(schema)
                .createTemporaryTable("redis_sink_table");


        DataStream<Row> r = flinkEnv.getStreamExecutionEnvironment().addSource(new UserDefinedSource());

        Table sourceTable = flinkEnv.getStreamTableEnvironment().fromDataStream(r, org.apache.flink.table.api.Schema.newBuilder()
                .columnByExpression("proctime", "PROCTIME()")
                .build());

        flinkEnv.getStreamTableEnvironment()
                .createTemporaryView("leftTable", sourceTable);

        String insertSql = "INSERT INTO redis_sink_table\n"
                + "SELECT o.f0, o.f1\n"
                + "FROM leftTable AS o\n";

        flinkEnv.getStreamTableEnvironment().executeSql(sql);

        flinkEnv.getStreamTableEnvironment().executeSql(insertSql);

    }

    private static class UserDefinedSource implements SourceFunction<Row>, ResultTypeQueryable<Row> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<Row> sourceContext) throws Exception {

            while (!this.isCancel) {

                sourceContext.collect(Row.of("a", "b", 1L));

                Thread.sleep(10L);
            }

        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }

        @Override
        public TypeInformation<Row> getProducedType() {
            return new RowTypeInfo(TypeInformation.of(String.class), TypeInformation.of(String.class),
                    TypeInformation.of(Long.class));
        }
    }

}
