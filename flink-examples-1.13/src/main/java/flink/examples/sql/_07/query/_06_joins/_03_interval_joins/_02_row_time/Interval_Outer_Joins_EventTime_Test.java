package flink.examples.sql._07.query._06_joins._03_interval_joins._02_row_time;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.data.RowData;

import com.google.common.collect.ImmutableMap;

import flink.examples.JacksonUtils;


public class Interval_Outer_Joins_EventTime_Test {

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
        env.getCheckpointConfig()
                .enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode().build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        tEnv.getConfig().getConfiguration().setString("pipeline.name", "1.13.2 Interval Outer Join 事件时间案例");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    row_time BIGINT,\n"
                + "    ts AS TO_TIMESTAMP_LTZ(row_time, 3),\n"
                + "    WATERMARK FOR ts AS ts\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._07.query._06_joins._03_interval_joins._01_outer_join"
                + ".Interval_Outer_Joins_EventTime_Test$UserDefinedSource1'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE dim_table (\n"
                + "  user_id BIGINT,\n"
                + "  platform STRING,\n"
                + "  row_time BIGINT,\n"
                + "  ts AS TO_TIMESTAMP_LTZ(row_time, 3),\n"
                + "  WATERMARK FOR ts AS ts\n"
                + ")\n"
                + "WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._07.query._06_joins._03_interval_joins._01_outer_join"
                + ".Interval_Outer_Joins_EventTime_Test$UserDefinedSource2'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    platform STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    s.user_id as user_id,\n"
                + "    s.name as name,\n"
                + "    d.platform as platform\n"
                + "FROM source_table as s\n"
                + "FULL JOIN dim_table as d\n"
                + "ON s.user_id = d.user_id\n"
                + "AND s.ts BETWEEN d.ts AND d.ts + INTERVAL '30' SECOND;";

        /**
         * join 算子：{@link org.apache.flink.table.runtime.operators.join.KeyedCoProcessOperatorWithWatermarkDelay}
         *                 -> {@link org.apache.flink.table.runtime.operators.join.interval.RowTimeIntervalJoin}
         *                       -> {@link org.apache.flink.table.runtime.operators.join.interval.IntervalJoinFunction}
         */

        Arrays.stream(sql.split(";"))
                .forEach(tEnv::executeSql);
    }

    public static class UserDefinedSource1 extends RichSourceFunction<RowData> {

        private DeserializationSchema<RowData> dser;

        private volatile boolean isCancel;

        public UserDefinedSource1(DeserializationSchema<RowData> dser) {
            this.dser = dser;
        }

        @Override
        public void run(SourceContext<RowData> ctx) throws Exception {

            int i = 0;

            while (!this.isCancel) {
                ctx.collect(this.dser.deserialize(
                        JacksonUtils.bean2Json(ImmutableMap
                                .of("user_id", i
                                        , "name", "antigeneral" + i
                                        , "row_time", new Timestamp(System.currentTimeMillis())))
                                .getBytes()
                ));
                i++;
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }

    public static class UserDefinedSource2 extends RichSourceFunction<RowData> {

        private DeserializationSchema<RowData> dser;

        private volatile boolean isCancel;

        public UserDefinedSource2(DeserializationSchema<RowData> dser) {
            this.dser = dser;
        }

        @Override
        public void run(SourceContext<RowData> ctx) throws Exception {

            int i = 10;

            while (!this.isCancel) {
                ctx.collect(this.dser.deserialize(
                        JacksonUtils.bean2Json(ImmutableMap
                                .of("user_id", i
                                        , "platform", "platform" + i
                                        , "row_time", new Timestamp(System.currentTimeMillis())))
                                .getBytes()
                ));
                i++;
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }

}
