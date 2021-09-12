package flink.examples.sql._07.query._03_group_agg;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;


public class _03_GroupingSetsEqualsGroupAggUnionAllGroupAggTest2 {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setParallelism(1);

        // ck 设置
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        env.enableCheckpointing(30 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3L);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode().build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        String sinkSql = "CREATE TABLE sink_table (\n"
                + "    supplier_id STRING,\n"
                + "    product_id STRING,\n"
                + "    total BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ")";

        String selectWhereSql = "insert into sink_table\n"
                + "SELECT\n"
                + "     supplier_id,\n"
                + "     product_id,\n"
                + "     COUNT(*) AS total\n"
                + "FROM (VALUES\n"
                + "     ('supplier1', 'product1', 4),\n"
                + "     ('supplier1', 'product2', 3),\n"
                + "     ('supplier2', 'product3', 3),\n"
                + "     ('supplier2', 'product4', 4))\n"
                + "AS Products(supplier_id, product_id, rating)\n"
                + "GROUP BY supplier_id, product_id\n"
                + "UNION ALL\n"
                + "SELECT\n"
                + "     supplier_id,\n"
                + "     cast(null as string) as product_id,\n"
                + "     COUNT(*) AS total\n"
                + "FROM (VALUES\n"
                + "     ('supplier1', 'product1', 4),\n"
                + "     ('supplier1', 'product2', 3),\n"
                + "     ('supplier2', 'product3', 3),\n"
                + "     ('supplier2', 'product4', 4))\n"
                + "AS Products(supplier_id, product_id, rating)\n"
                + "GROUP BY supplier_id\n"
                + "UNION ALL\n"
                + "SELECT\n"
                + "     cast(null as string) AS supplier_id,\n"
                + "     cast(null as string) AS product_id,\n"
                + "     COUNT(*) AS total\n"
                + "FROM (VALUES\n"
                + "     ('supplier1', 'product1', 4),\n"
                + "     ('supplier1', 'product2', 3),\n"
                + "     ('supplier2', 'product3', 3),\n"
                + "     ('supplier2', 'product4', 4))\n"
                + "AS Products(supplier_id, product_id, rating)";

        tEnv.getConfig().getConfiguration().setString("pipeline.name", "GROUPING SETS 等同于 GROUP AGG UNION ALL 案例");

        tEnv.executeSql(sinkSql);
        tEnv.executeSql(selectWhereSql);
    }

}
