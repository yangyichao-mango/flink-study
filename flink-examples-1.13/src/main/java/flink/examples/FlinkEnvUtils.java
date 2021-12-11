package flink.examples;

import java.io.IOException;
import java.util.Optional;
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
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.module.CoreModule;

import flink.examples.sql._08.batch._03_hive_udf.HiveModuleV2;
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
        env.enableCheckpointing(180 * 1000L, CheckpointingMode.EXACTLY_ONCE);
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

        FlinkEnv flinkEnv = FlinkEnv
                .builder()
                .streamExecutionEnvironment(env)
                .streamTableEnvironment(tEnv)
                .build();

        initHiveEnv(flinkEnv, parameterTool);

        return flinkEnv;
    }

    /**
     * hadoop 启动：/usr/local/Cellar/hadoop/3.2.1/sbin/start-all.sh
     * http://localhost:9870/
     * http://localhost:8088/cluster
     *
     * hive 启动：$HIVE_HOME/bin/hive --service metastore &
     * hive cli：$HIVE_HOME/bin/hive
     */
    private static void initHiveEnv(FlinkEnv flinkEnv, ParameterTool parameterTool) {
        String defaultDatabase = "default";
        String hiveConfDir = "/usr/local/Cellar/hive/3.1.2/libexec/conf";

        boolean enableHiveCatalog = parameterTool.getBoolean("enable.hive.catalog", false);

        if (enableHiveCatalog) {
            HiveCatalog hive = new HiveCatalog("default", defaultDatabase, hiveConfDir);

            Optional.ofNullable(flinkEnv.streamTEnv())
                    .ifPresent(s -> s.registerCatalog("default", hive));

            Optional.ofNullable(flinkEnv.batchTEnv())
                    .ifPresent(s -> s.registerCatalog("default", hive));

            // set the HiveCatalog as the current catalog of the session

            Optional.ofNullable(flinkEnv.streamTEnv())
                    .ifPresent(s -> s.useCatalog("default"));

            Optional.ofNullable(flinkEnv.batchTEnv())
                    .ifPresent(s -> s.useCatalog("default"));
        }

        boolean enableHiveDialect = parameterTool.getBoolean("enable.hive.dialect", false);

        if (enableHiveDialect) {

            Optional.ofNullable(flinkEnv.streamTEnv())
                    .ifPresent(s -> s.getConfig().setSqlDialect(SqlDialect.HIVE));

            Optional.ofNullable(flinkEnv.batchTEnv())
                    .ifPresent(s -> s.getConfig().setSqlDialect(SqlDialect.HIVE));
        }

        boolean enableHiveModuleV2 = parameterTool.getBoolean("enable.hive.module.v2", true);

        if (enableHiveModuleV2) {
            String version = "3.1.2";

            HiveModuleV2 hiveModuleV2 = new HiveModuleV2(version);

            final boolean enableHiveModuleLoadFirst = parameterTool.getBoolean("enable.hive.module.load-first", true);

            Optional.ofNullable(flinkEnv.streamTEnv())
                    .ifPresent(s -> {
                        if (enableHiveModuleLoadFirst) {
                            s.unloadModule("core");
                            s.loadModule("default", hiveModuleV2);
                            s.loadModule("core", CoreModule.INSTANCE);
                        } else {
                            s.loadModule("default", hiveModuleV2);
                        }
                    });

            Optional.ofNullable(flinkEnv.batchTEnv())
                    .ifPresent(s -> {
                        if (enableHiveModuleLoadFirst) {
                            s.unloadModule("core");
                            s.loadModule("default", hiveModuleV2);
                            s.loadModule("core", CoreModule.INSTANCE);
                        } else {
                            s.loadModule("default", hiveModuleV2);
                        }
                    });

            flinkEnv.setHiveModuleV2(hiveModuleV2);
        }
    }


    public static FlinkEnv getBatchTableEnv(String[] args) throws IOException {

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
        env.getCheckpointConfig()
                .enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inBatchMode()
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);

        FlinkEnv flinkEnv = FlinkEnv
                .builder()
                .streamExecutionEnvironment(env)
                .tableEnvironment(tEnv)
                .build();


        initHiveEnv(flinkEnv, parameterTool);

        return flinkEnv;
    }

    @Builder
    @Data
    public static class FlinkEnv {
        private StreamExecutionEnvironment streamExecutionEnvironment;
        private StreamTableEnvironment streamTableEnvironment;
        private TableEnvironment tableEnvironment;
        private HiveModuleV2 hiveModuleV2;

        public StreamTableEnvironment streamTEnv() {
            return this.streamTableEnvironment;
        }

        public TableEnvironment batchTEnv() {
            return this.tableEnvironment;
        }

        public StreamExecutionEnvironment env() {
            return this.streamExecutionEnvironment;
        }

        public HiveModuleV2 hiveModuleV2() {
            return this.hiveModuleV2;
        }
    }

}
