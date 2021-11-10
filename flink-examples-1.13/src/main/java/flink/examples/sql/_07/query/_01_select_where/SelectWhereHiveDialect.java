package flink.examples.sql._07.query._01_select_where;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

/**
 * hadoop 启动：/usr/local/Cellar/hadoop/3.2.1/sbin/start-all.sh
 * http://localhost:9870/
 * http://localhost:8088/cluster
 *
 * hive 启动：$HIVE_HOME/bin/hive --service metastore &
 * hive cli：$HIVE_HOME/bin/hive
 */
public class SelectWhereHiveDialect {


    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        // TODO 没有 hive catalog 会 fallback 回 default parser
        flinkEnv.streamTEnv().getConfig().setSqlDialect(SqlDialect.HIVE);

        DataStream<Row> r = flinkEnv.env().addSource(new UserDefinedSource());

        flinkEnv.streamTEnv().createTemporaryView("source_table", r);

        String selectWhereSql = "select * from source_table";

        Table resultTable = flinkEnv.streamTEnv().sqlQuery(selectWhereSql);

        flinkEnv.streamTEnv().toRetractStream(resultTable, Row.class).print();

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
            return new RowTypeInfo(new TypeInformation[]{
                    TypeInformation.of(String.class)
                    , TypeInformation.of(String.class)
                    , TypeInformation.of(Long.class)
            }, new String[] {"a", "b", "c"});
        }
    }

}
