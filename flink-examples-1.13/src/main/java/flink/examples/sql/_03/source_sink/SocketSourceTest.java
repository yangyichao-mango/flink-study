package flink.examples.sql._03.source_sink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


public class SocketSourceTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode().build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        TableResult tr = tEnv.executeSql(
                "CREATE TABLE UserScores (name STRING, score INT)\n"
                        + "WITH (\n"
                        + "  'connector' = 'socket',\n"
                        + "  'hostname' = 'localhost',\n"
                        + "  'port' = '9999',\n"
                        + "  'byte-delimiter' = '10',\n"
                        + "  'format' = 'changelog-csv',\n"
                        + "  'changelog-csv.column-delimiter' = '|'\n"
                        + ")"
        );

//        TableResult tr = tEnv.executeSql(
//                "CREATE TABLE Orders (\n"
//                        + "    order_number BIGINT,\n"
//                        + "    price        DECIMAL(32,2),\n"
//                        + "    buyer        ROW<first_name STRING, last_name STRING>,\n"
//                        + "    order_time   TIMESTAMP(3)\n"
//                        + ") WITH (\n"
//                        + "  'connector' = 'datagen',\n"
//                        + "  'number-of-rows' = '10',\n"
//                        + "  'rows-per-second' = '1'\n"
//                        + ")"
//        );

//        Table t = tEnv.sqlQuery("SELECT * FROM Orders");
        Table t = tEnv.sqlQuery("SELECT name, SUM(score) FROM UserScores GROUP BY name");

        tEnv.toRetractStream(t, Row.class).print();

        env.execute("测试");
    }

}
