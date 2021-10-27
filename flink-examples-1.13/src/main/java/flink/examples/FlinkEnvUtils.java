package flink.examples;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.PredefinedOptions;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import lombok.Builder;
import lombok.Data;

public class FlinkEnvUtils {

    private static final boolean ENABLE_INCREMENTAL_CHECKPOINT = true;
    private static final int NUMBER_OF_TRANSFER_THREADS = 3;

    /**
     * 设置状态后端为 RocksDBStateBackend
     *
     * @param env env
     */
    public static void setRocksDBStateBackend(StreamExecutionEnvironment env) throws IOException {
        setCheckpointConfig(env);

        RocksDBStateBackend rocksDBStateBackend = new RocksDBStateBackend(
                "file:///Users/flink/checkpoints", ENABLE_INCREMENTAL_CHECKPOINT);
        rocksDBStateBackend.setNumberOfTransferThreads(NUMBER_OF_TRANSFER_THREADS);
        rocksDBStateBackend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH_MEM);
        env.setStateBackend((StateBackend) rocksDBStateBackend);
    }


    /**
     * 设置状态后端为 FsStateBackend
     *
     * @param env env
     */
    public static void setFsStateBackend(StreamExecutionEnvironment env) throws IOException {
        setCheckpointConfig(env);
        FsStateBackend fsStateBackend = new FsStateBackend("file:///Users/flink/checkpoints");
        env.setStateBackend((StateBackend) fsStateBackend);
    }


    /**
     * 设置状态后端为 MemoryStateBackend
     *
     * @param env env
     */
    public static void setMemoryStateBackend(StreamExecutionEnvironment env) throws IOException {
        setCheckpointConfig(env);
        env.setStateBackend((StateBackend) new MemoryStateBackend());
    }

    /**
     * Checkpoint 参数相关配置，but 不设置 StateBackend，即：读取 flink-conf.yaml 文件的配置
     *
     * @param env env
     */
    public static void setCheckpointConfig(StreamExecutionEnvironment env) throws IOException {
        env.getCheckpointConfig().setCheckpointTimeout(TimeUnit.MINUTES.toMillis(3));
        // ck 设置
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        env.enableCheckpointing(10 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3L);

        Configuration configuration = new Configuration();
        configuration.setString("state.checkpoints.num-retained", "3");

        env.configure(configuration, Thread.currentThread().getContextClassLoader());

        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }

    public static FlinkEnv getStreamTableEnv(String[] args) throws IOException {

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        Configuration configuration = Configuration.fromMap(parameterTool.toMap());


        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

        String stateBackend = parameterTool.get("state.backend", "rocksdb");

        if ("rocksdb".equals(stateBackend)) {

            setRocksDBStateBackend(env);
        } else if ("filesystem".equals(stateBackend)) {
            setFsStateBackend(env);
        } else if ("jobmanager".equals(stateBackend)) {
            setMemoryStateBackend(env);
        }


        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
        env.getConfig().setGlobalJobParameters(parameterTool);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        return FlinkEnv
                .builder()
                .streamExecutionEnvironment(env)
                .streamTableEnvironment(tEnv)
                .build();
    }

    @Builder
    @Data
    public static class FlinkEnv {
        private StreamExecutionEnvironment streamExecutionEnvironment;
        private StreamTableEnvironment streamTableEnvironment;

        public StreamTableEnvironment tableEnv() {
            return this.streamTableEnvironment;
        }

        public StreamExecutionEnvironment env() {
            return this.streamExecutionEnvironment;
        }
    }

}
