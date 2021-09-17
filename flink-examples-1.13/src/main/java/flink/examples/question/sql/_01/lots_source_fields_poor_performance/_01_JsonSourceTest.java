package flink.examples.question.sql._01.lots_source_fields_poor_performance;

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


public class _01_JsonSourceTest {

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

        tEnv.getConfig().getConfiguration().setString("pipeline.name", "1.13.1 TUMBLE WINDOW 案例");

        tEnv.getConfig().getConfiguration().setString("state.backend", "rocksdb");


        String originalSql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    server_timestamp BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'class.name' = 'flink.examples.question.sql._01.lots_source_fields_poor_performance._01_JsonSourceTest$UserDefineSource1',\n"
                + "  'format' = 'json'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    rn BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select user_id,\n"
                + "       name,\n"
                + "       rn\n"
                + "from (\n"
                + "      SELECT\n"
                + "          user_id,\n"
                + "          name,\n"
                + "          row_number() over(partition by user_id order by server_timestamp) as rn\n"
                + "      FROM source_table\n"
                + ")\n"
                + "where rn = 1";


        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    user_id1 BIGINT,\n"
                + "    name1 STRING,\n"
                + "    user_id2 BIGINT,\n"
                + "    name2 STRING,\n"
                + "    user_id3 BIGINT,\n"
                + "    name3 STRING,\n"
                + "    user_id4 BIGINT,\n"
                + "    name4 STRING,\n"
                + "    user_id5 BIGINT,\n"
                + "    name5 STRING,\n"
                + "    user_id6 BIGINT,\n"
                + "    name6 STRING,\n"
                + "    user_id7 BIGINT,\n"
                + "    name7 STRING,\n"
                + "    user_id8 BIGINT,\n"
                + "    name8 STRING,\n"
                + "    user_id9 BIGINT,\n"
                + "    name9 STRING,\n"
                + "    server_timestamp BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10',\n"
                + "  'fields.name.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.server_timestamp.min' = '1',\n"
                + "  'fields.server_timestamp.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    rn BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select user_id,\n"
                + "       name,\n"
                + "       rn\n"
                + "from (\n"
                + "      SELECT\n"
                + "          user_id,\n"
                + "          name,\n"
                + "          row_number() over(partition by user_id order by server_timestamp) as rn\n"
                + "      FROM source_table\n"
                + ")\n"
                + "where rn = 1";

        Arrays.stream(originalSql.split(";"))
                .forEach(tEnv::executeSql);
    }

    public static class UserDefineSource1 extends RichSourceFunction<RowData> {

        private DeserializationSchema<RowData> dser;

        private volatile boolean isCancel;

        public UserDefineSource1(DeserializationSchema<RowData> dser) {
            this.dser = dser;
        }

        @Override
        public void run(SourceContext<RowData> ctx) throws Exception {
            while (!this.isCancel) {
                ctx.collect(this.dser.deserialize(
                        JacksonUtils.bean2Json(ImmutableMap.of("user_id", 1111L
                                , "name", "antigeneral"
                                , "server_timestamp", System.currentTimeMillis())
                        ).getBytes()
                ));
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }

}
