package flink.examples.sql._07.query._04_window_agg._02_cumulate_window;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;


public class CumulateWindowTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setParallelism(10);

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

        String sourceSql = "CREATE TABLE source_table (\n"
                + "    dim STRING,\n"
                + "    user_id BIGINT,\n"
                + "    price BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10000',\n"
                + "  'fields.dim.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '100000'\n"
                + ")";

        String sinkSql = "CREATE TABLE sink_table (\n"
                + "    dim STRING,\n"
                + "    pv BIGINT,\n"
                + "    sum_price BIGINT,\n"
                + "    max_price BIGINT,\n"
                + "    min_price BIGINT,\n"
                + "    uv BIGINT,\n"
                + "    window_start bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ")";

        String selectWhereSql = "insert into sink_table\n"
                + "select dim,\n"
                + "       sum(bucket_pv) as pv,\n"
                + "       sum(bucket_sum_price) as sum_price,\n"
                + "       max(bucket_max_price) as max_price,\n"
                + "       min(bucket_min_price) as min_price,\n"
                + "       sum(bucket_uv) as uv,\n"
                + "       max(window_start) as window_start\n"
                + "from (\n"
                + "     SELECT dim,\n"
                + "             UNIX_TIMESTAMP(CAST(window_start AS STRING)) * 1000 as window_start, \n"
                + "            window_end, \n"
                + "            count(*) as bucket_pv,\n"
                + "            sum(price) as bucket_sum_price,\n"
                + "            max(price) as bucket_max_price,\n"
                + "            min(price) as bucket_min_price,\n"
                + "            count(distinct user_id) as bucket_uv\n"
                + "     FROM TABLE(TUMBLE(\n"
                + "               TABLE source_table\n"
                + "               , DESCRIPTOR(row_time)\n"
                + "               , INTERVAL '60' SECOND))\n"
                + "     GROUP BY window_start, \n"
                + "              window_end,\n"
                + "              dim,\n"
                + "              mod(user_id, 1024)\n"
                + ")\n"
                + "group by dim,\n"
                + "         window_start";

        tEnv.getConfig().getConfiguration().setString("pipeline.name", "1.13.2 WINDOW TVF TUMBLE WINDOW 案例");

        tEnv.executeSql(sourceSql);
        tEnv.executeSql(sinkSql);
        tEnv.executeSql(selectWhereSql);

        /**
         * 两阶段聚合
         * 本地 agg：{@link org.apache.flink.table.runtime.operators.aggregate.window.LocalSlicingWindowAggOperator}
         *                   -> {@link org.apache.flink.table.runtime.operators.aggregate.window.combines.LocalAggCombiner}
         *
         * key agg；{@link org.apache.flink.table.runtime.operators.window.slicing.SlicingWindowOperator}
         *    -> {@link org.apache.flink.table.runtime.operators.aggregate.window.processors.SliceUnsharedWindowAggProcessor}
         *                   -> {@link org.apache.flink.table.runtime.operators.aggregate.window.combines.GlobalAggCombiner}
         */
    }

}
