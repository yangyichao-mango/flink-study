package flink.examples.datastream._06_test._01_event_proctime;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;


public class OneJobWIthTimerTest {

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
                .process(new KeyedProcessFunction<Long, SourceModel, SinkModel>() {

                    private int i = 0;

                    @Override
                    public void processElement(SourceModel value, Context ctx, Collector<SinkModel> out)
                            throws Exception {
                        if (i == 0) {
                            i++;
                            System.out.println(1);
                            ctx.timerService().registerEventTimeTimer(value.time + 1000);
                            ctx.timerService().registerProcessingTimeTimer(value.time + 5000);
                        } else {
                            System.out.println(2);
                        }
                    }

                    @Override
                    public void onTimer(long timestamp, OnTimerContext ctx, Collector<SinkModel> out) throws Exception {

                        System.out.println(ctx.timeDomain());

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
