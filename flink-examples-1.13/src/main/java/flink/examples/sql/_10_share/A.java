package flink.examples.sql._10_share;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class A {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        env.setParallelism(1);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode().build();

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        DataStream<Row> source = env.addSource(new UserDefinedSource());

        Table sourceTable = tEnv.fromDataStream(source, "stat_date,\n"
                + "  order_id,\n"
                + "  buyer_id,\n"
                + "  seller_id,\n"
                + "  buy_amount,\n"
                + "  div_pay_amt");

        tEnv.createTemporaryView("dwd_tb_trd_ord_ent_di_m", sourceTable);

        String sql = "CREATE TABLE dimTable (\n"
                + "    name STRING,\n"
                + "    name1 STRING,\n"
                + "    score BIGINT"
                + ") WITH (\n"
                + "  'connector' = 'redis',\n"
                + "  'hostname' = '127.0.0.1',\n"
                + "  'port' = '6379',\n"
                + "  'format' = 'json',\n"
                + "  'lookup.cache.max-rows' = '500',\n"
                + "  'lookup.cache.ttl' = '3600',\n"
                + "  'lookup.max-retries' = '1'\n"
                + ")";

        String joinSql = "SELECT o.f0, o.f1, c.name, c.name1, c.score\n"
                + "FROM leftTable AS o\n"
                + "LEFT JOIN dimTable FOR SYSTEM_TIME AS OF o.proctime AS c\n"
                + "ON o.f0 = c.name";

        TableResult dimTable = tEnv.executeSql(sql);

        Table t = tEnv.sqlQuery(joinSql);

        //        Table t = tEnv.sqlQuery("select * from leftTable");

        tEnv.toAppendStream(t, Row.class).print();

        env.execute();
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
