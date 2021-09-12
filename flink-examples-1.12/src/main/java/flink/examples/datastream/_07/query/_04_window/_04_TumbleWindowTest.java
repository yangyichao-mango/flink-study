package flink.examples.datastream._07.query._04_window;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class _04_TumbleWindowTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.addSource(new UserDefinedSource())
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Tuple4<String, String, Integer, Long>>(Time.seconds(0)) {
                    @Override
                    public long extractTimestamp(Tuple4<String, String, Integer, Long> element) {
                        return element.f3;
                    }
                })
                .keyBy(new KeySelector<Tuple4<String, String, Integer, Long>, String>() {
                    @Override
                    public String getKey(Tuple4<String, String, Integer, Long> row) throws Exception {
                        return row.f0;
                    }
                })
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .sum(2)
                .print();

        env.execute("1.12.1 DataStream TUMBLE WINDOW 案例");
    }

    private static class UserDefinedSource implements SourceFunction<Tuple4<String, String, Integer, Long>> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<Tuple4<String, String, Integer, Long>> sourceContext) throws Exception {

            while (!this.isCancel) {

                sourceContext.collect(Tuple4.of("a", "b", 1, System.currentTimeMillis()));

                Thread.sleep(10L);
            }

        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }
}