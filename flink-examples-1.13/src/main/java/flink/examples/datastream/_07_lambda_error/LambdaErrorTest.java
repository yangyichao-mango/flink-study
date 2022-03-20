package flink.examples.datastream._07_lambda_error;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;


public class LambdaErrorTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        flinkEnv.env()
                .addSource(new SourceFunction<SourceModel>() {

                    private volatile boolean isCancel = false;

                    private SinkModel s;

                    @Override
                    public void run(SourceContext<SourceModel> ctx) throws Exception {
                        while (!isCancel) {
                            // xxx 日志上报逻辑
                            ctx.collect(
                                    SourceModel
                                            .builder()
                                            .page("Shopping-Cart")
                                            .userId(1)
                                            .time(System.currentTimeMillis())
                                            .build()
                            );
                            Thread.sleep(100);
                        }
                    }

                    @Override
                    public void cancel() {
                        this.isCancel = true;
                    }
                })
                .print();

        flinkEnv.env().execute();
    }

    @Data
    @Builder
    private static class SourceModel {
        private long userId;
        private String page;
        private long time;
    }

    @Data
    @Builder
    private static class MiddleModel {
        private long uv;
        private long time;
    }

    @Data
    @Builder
    private static class SinkModel {
        private long uv;
        private long time;
    }

}
