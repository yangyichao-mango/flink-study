package flink.examples.datastream._04.keyed_co_process;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import lombok.Builder;
import lombok.Data;

public class _04_KeyedCoProcessFunctionTest {

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


    }

    @Data
    @Builder
    private static class CommonModel {
        private String userId;
    }


    private static class UserDefineSource1 extends RichSourceFunction<CommonModel> {

        private volatile boolean isCancel = true;

        @Override
        public void run(SourceContext<CommonModel> ctx) throws Exception {
            while (!this.isCancel) {
                ctx.collect(
                        CommonModel
                                .builder()
                                .build()
                );
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }

    private static class UserDefineSource2 extends RichSourceFunction<CommonModel> {

        private volatile boolean isCancel = true;

        @Override
        public void run(SourceContext<CommonModel> ctx) throws Exception {
            while (!this.isCancel) {
                ctx.collect(
                        CommonModel
                                .builder()
                                .build()
                );
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }


}
