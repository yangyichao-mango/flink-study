package flink.examples.sql._07.query._08_datastream_trans;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        DataStream<Row> r = flinkEnv.env().addSource(new UserDefinedSource());

        // 数据源是 DataStream API
        Table sourceTable = flinkEnv.streamTEnv().fromDataStream(r
                , Schema
                        .newBuilder()
                        .column("f0", "string")
                        .column("f1", "string")
                        .column("f2", "bigint")
                        .columnByExpression("proctime", "PROCTIME()")
                        .build());

        flinkEnv.streamTEnv().createTemporaryView("source_table", sourceTable);

        String selectDistinctSql = "select distinct f0 from source_table";

        Table resultTable = flinkEnv.streamTEnv().sqlQuery(selectDistinctSql);

        flinkEnv.streamTEnv().toRetractStream(resultTable, Row.class).print();

        String groupBySql = "select f0 from source_table group by f0";

        Table resultTable1 = flinkEnv.streamTEnv().sqlQuery(groupBySql);

        flinkEnv.streamTEnv().toRetractStream(resultTable1, Row.class).print();

        flinkEnv.env().execute();
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
