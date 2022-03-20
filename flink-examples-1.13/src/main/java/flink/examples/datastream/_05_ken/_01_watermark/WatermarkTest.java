package flink.examples.datastream._05_ken._01_watermark;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;


public class WatermarkTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(8);

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
                                            .build()
                            );
                        }
                    }

                    @Override
                    public void cancel() {
                        this.isCancel = true;
                    }
                })
                .filter(new FilterFunction<SourceModel>() {
                    @Override
                    public boolean filter(SourceModel value) throws Exception {
                        return value.getPage().equals("Shopping-Cart");
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SourceModel>(Time.minutes(1)) {
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
                .window(TumblingEventTimeWindows.of(Time.minutes(1)))
                .process(new ProcessWindowFunction<SourceModel, SinkModel, Long, TimeWindow>() {
                    @Override
                    public void process(Long aLong, Context context, Iterable<SourceModel> elements,
                            Collector<SinkModel> out) throws Exception {

                        long windowStart = context.window().getStart();

                        Set<Long> s = new HashSet<>();

                        elements.forEach(new Consumer<SourceModel>() {
                            @Override
                            public void accept(SourceModel sourceModel) {
                                s.add(sourceModel.userId);
                            }
                        });

                        out.collect(
                                SinkModel
                                        .builder()
                                        .uv(s.size())
                                        .time(windowStart)
                                        .build()
                        );
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
    private static class SinkModel {
        private long uv;
        private long time;
    }

}
