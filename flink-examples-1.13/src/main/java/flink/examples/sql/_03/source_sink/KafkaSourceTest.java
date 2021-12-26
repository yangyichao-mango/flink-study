package flink.examples.sql._03.source_sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


public class KafkaSourceTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        env.setParallelism(1);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        tEnv.executeSql(
                "CREATE TABLE KafkaSourceTable (\n"
                        + "  `f0` STRING,\n"
                        + "  `f1` STRING\n"
                        + ") WITH (\n"
                        + "  'connector' = 'kafka',\n"
                        + "  'topic' = 'topic',\n"
                        + "  'properties.bootstrap.servers' = 'localhost:9092',\n"
                        + "  'properties.group.id' = 'testGroup',\n"
                        + "  'format' = 'json'\n"
                        + ")"
        );

        Table t = tEnv.sqlQuery("SELECT * FROM KafkaSourceTable");

        tEnv.toAppendStream(t, Row.class).print();

        env.execute();
    }
}
