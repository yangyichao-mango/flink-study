package flink.examples.sql._07.query._01_select_where;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.types.Row;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class SelectWhereTest2 {


    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        String sourceSql = "CREATE TABLE source_table (\n"
                + "    order_number BIGINT,\n"
                + "    price        DECIMAL(32,2)\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10',\n"
                + "  'fields.order_number.min' = '10',\n"
                + "  'fields.order_number.max' = '11'\n"
                + ")";

        String sinkSql = "CREATE TABLE sink_table (\n"
                + "    order_number BIGINT,\n"
                + "    price        DECIMAL(32,2)\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ")";

        String selectWhereSql = "insert into sink_table\n"
                + "select * from source_table\n"
                + "where order_number = 10";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "ETL 案例");

        flinkEnv.streamTEnv().executeSql(sourceSql);
        flinkEnv.streamTEnv().executeSql(sinkSql);
        flinkEnv.streamTEnv().executeSql(selectWhereSql);
    }


    private static class UserDefinedSource implements SourceFunction<Row>, ResultTypeQueryable<Row> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<Row> sourceContext) throws Exception {

            int i = 0;

            while (!this.isCancel) {

                sourceContext.collect(Row.of("a" + i, "b", 1L));

                Thread.sleep(10L);
                i++;
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
