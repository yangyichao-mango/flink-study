package flink.examples.runtime._04.statebackend;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.PredefinedOptions;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;


public class CancelAndRestoreWithCheckpointTest {

    private static final boolean ENABLE_INCREMENTAL_CHECKPOINT = true;
    private static final int NUMBER_OF_TRANSFER_THREADS = 3;

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();

        configuration.setString("execution.savepoint.path", "file:///Users/flink/checkpoints/ce2e1969c5088bf27daf35d4907659fd/chk-5");

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        // ck 设置
        env.getCheckpointConfig().setCheckpointTimeout(TimeUnit.MINUTES.toMillis(3));
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        env.enableCheckpointing(30 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3L);

        env.configure(configuration, Thread.currentThread().getContextClassLoader());

        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        // 状态后端设置
        // 设置存储文件位置为 file:///Users/flink/checkpoints
        RocksDBStateBackend rocksDBStateBackend = new RocksDBStateBackend(
                "file:///Users/flink/checkpoints", ENABLE_INCREMENTAL_CHECKPOINT);
        rocksDBStateBackend.setNumberOfTransferThreads(NUMBER_OF_TRANSFER_THREADS);
        rocksDBStateBackend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH_MEM);
        env.setStateBackend((StateBackend) rocksDBStateBackend);

        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setParallelism(10);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        tEnv.getConfig().getConfiguration().setString("table.exec.emit.early-fire.enabled", "true");
        tEnv.getConfig().getConfiguration().setString("table.exec.emit.early-fire.delay", "60 s");

        String sql = "CREATE TABLE source_table (\n"
                + "    dim BIGINT,\n"
                + "    user_id BIGINT,\n"
                + "    price BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.dim.min' = '1',\n"
                + "  'fields.dim.max' = '2',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    dim BIGINT,\n"
                + "    pv BIGINT,\n"
                + "    sum_price BIGINT,\n"
                + "    max_price BIGINT,\n"
                + "    min_price BIGINT,\n"
                + "    uv BIGINT,\n"
                + "    window_start bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "select dim,\n"
                + "       sum(bucket_pv) as pv,\n"
                + "       sum(bucket_sum_price) as sum_price,\n"
                + "       max(bucket_max_price) as max_price,\n"
                + "       min(bucket_min_price) as min_price,\n"
                + "       sum(bucket_uv) as uv,\n"
                + "       max(window_start) as window_start\n"
                + "from (\n"
                + "     select dim,\n"
                + "            count(*) as bucket_pv,\n"
                + "            sum(price) as bucket_sum_price,\n"
                + "            max(price) as bucket_max_price,\n"
                + "            min(price) as bucket_min_price,\n"
                + "            count(distinct user_id) as bucket_uv,\n"
                + "            UNIX_TIMESTAMP(CAST(tumble_start(row_time, interval '1' DAY) AS STRING)) * 1000 as window_start\n"
                + "     from source_table\n"
                + "     group by\n"
                + "            mod(user_id, 1024),\n"
                + "            dim,\n"
                + "            tumble(row_time, interval '1' DAY)\n"
                + ")\n"
                + "group by dim,\n"
                + "         window_start";

        tEnv.getConfig().getConfiguration().setString("pipeline.name", "1.13.5 WINDOW TVF TUMBLE WINDOW EARLY FIRE 案例");

        Arrays.stream(sql.split(";"))
                .forEach(tEnv::executeSql);
    }

}
