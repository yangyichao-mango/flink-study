package flink.examples.datastream._10_agg;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;


public class AggTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        flinkEnv.env()
                .addSource(new SourceFunction<SourceModel>() {

                    private volatile boolean isCancel = false;

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
                            Thread.sleep(1000);
                        }
                    }

                    @Override
                    public void cancel() {
                        this.isCancel = true;
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SourceModel>(Time.seconds(1)) {
                    @Override
                    public long extractTimestamp(SourceModel element) {
                        return element.getTime();
                    }
                })
                .keyBy(new KeySelector<SourceModel, Long>() {
                    @Override
                    public Long getKey(SourceModel value) throws Exception {
                        return 0L;
                    }
                })
                .timeWindow(Time.seconds(3))
                .aggregate(new AggregateFunction<SourceModel, SourceModel, SourceModel>() {
                    @Override
                    public SourceModel createAccumulator() {
                        return SourceModel.builder().build();
                    }

                    @Override
                    public SourceModel add(SourceModel sourceModel, SourceModel sourceModel2) {
                        return sourceModel;
                    }

                    @Override
                    public SourceModel getResult(SourceModel sourceModel) {
                        return sourceModel;
                    }

                    @Override
                    public SourceModel merge(SourceModel sourceModel, SourceModel acc1) {
                        return null;
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
