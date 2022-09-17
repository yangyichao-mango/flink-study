package flink.examples.datastream._08_late_record;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;


public class LatenessTest {

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
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SourceModel>(Time.minutes(1)) {
                    @Override
                    public long extractTimestamp(SourceModel element) {
                        return element.getTime();
                    }
                })
                .flatMap(new FlatMapFunction<SourceModel, MiddleModel>() {

                    private Collector<MiddleModel> out1;

                    @Override
                    public void flatMap(SourceModel value, Collector<MiddleModel> out) throws Exception {
                        for (int i = 0; i < 3; i++) {

                            if (out1 == null) {
                                this.out1 = out;
                            }

                            out.collect(
                                    MiddleModel
                                            .builder()
                                            .uv(1L)
                                            .time(System.currentTimeMillis())
                                            .build()
                            );
                        }
                    }
                })
                .keyBy(new KeySelector<MiddleModel, Integer>() {
                    @Override
                    public Integer getKey(MiddleModel value) throws Exception {
                        return 0;
                    }
                })
                .timeWindow(Time.seconds(10))
                .process(new ProcessWindowFunction<MiddleModel, SinkModel, Integer, TimeWindow>() {
                    @Override
                    public void process(Integer integer, Context context, Iterable<MiddleModel> elements,
                            Collector<SinkModel> out) throws Exception {
                        System.out.println(1L);
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
