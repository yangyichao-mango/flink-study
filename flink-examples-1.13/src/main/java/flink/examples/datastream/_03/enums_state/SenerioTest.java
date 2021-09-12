package flink.examples.datastream._03.enums_state;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SenerioTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        Tuple2<DimNameEnum, String> k = Tuple2.of(DimNameEnum.sex, "男");

        System.out.println(k.toString());

        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        env.addSource(new SourceFunction<SourceModel>() {

            private volatile boolean isCancel = false;

            @Override
            public void run(SourceContext<SourceModel> ctx) throws Exception {

            }

            @Override
            public void cancel() {
                this.isCancel = true;
            }
        })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SourceModel>(Time.minutes(1L)) {
                    @Override
                    public long extractTimestamp(SourceModel element) {
                        return element.getTimestamp();
                    }
                })
                .keyBy(new KeySelector<SourceModel, Long>() {
                    @Override
                    public Long getKey(SourceModel value) throws Exception {
                        return value.getUserId() % 1000;
                    }
                })
                .timeWindow(Time.minutes(1))
                .aggregate(
                        new AggregateFunction<SourceModel, Map<Tuple2<DimNameEnum, String>, Long>, Map<Tuple2<DimNameEnum, String>, Long>>() {

                            @Override
                            public Map<Tuple2<DimNameEnum, String>, Long> createAccumulator() {
                                return new HashMap<>();
                            }

                            @Override
                            public Map<Tuple2<DimNameEnum, String>, Long> add(SourceModel value,
                                    Map<Tuple2<DimNameEnum, String>, Long> accumulator) {

                                Lists.newArrayList(Tuple2.of(DimNameEnum.province, value.getProvince())
                                        , Tuple2.of(DimNameEnum.age, value.getAge())
                                        , Tuple2.of(DimNameEnum.sex, value.getSex()))
                                        .forEach(t -> {
                                            Long l = accumulator.get(t);

                                            if (null == l) {
                                                accumulator.put(t, 1L);
                                            } else {
                                                accumulator.put(t, l + 1);
                                            }
                                        });

                                return accumulator;
                            }

                            @Override
                            public Map<Tuple2<DimNameEnum, String>, Long> getResult(
                                    Map<Tuple2<DimNameEnum, String>, Long> accumulator) {
                                return accumulator;
                            }

                            @Override
                            public Map<Tuple2<DimNameEnum, String>, Long> merge(
                                    Map<Tuple2<DimNameEnum, String>, Long> a,
                                    Map<Tuple2<DimNameEnum, String>, Long> b) {
                                return null;
                            }
                        },
                        new ProcessWindowFunction<Map<Tuple2<DimNameEnum, String>, Long>, SinkModel, Long, TimeWindow>() {

                            private transient ValueState<Map<Tuple2<DimNameEnum, String>, Long>> todayPv;

                            @Override
                            public void open(Configuration parameters) throws Exception {
                                super.open(parameters);
                                this.todayPv = getRuntimeContext().getState(new ValueStateDescriptor<Map<Tuple2<DimNameEnum, String>, Long>>(
                                        "todayPv", TypeInformation.of(
                                        new TypeHint<Map<Tuple2<DimNameEnum, String>, Long>>() {
                                        })));
                            }

                            @Override
                            public void process(Long aLong, Context context,
                                    Iterable<Map<Tuple2<DimNameEnum, String>, Long>> elements, Collector<SinkModel> out)
                                    throws Exception {
                                // 将 elements 数据 merge 到 todayPv 中
                                // 然后 out#collect 出去即可

                                this.todayPv.value()
                                        .forEach(new BiConsumer<Tuple2<DimNameEnum, String>, Long>() {
                                            @Override
                                            public void accept(Tuple2<DimNameEnum, String> k,
                                                    Long v) {
                                                log.info("key 值：{}，value 值：{}", k.toString(), v);
                                            }
                                        });
                            }
                        });

        env.execute();
    }

    @Data
    @Builder
    private static class SourceModel {
        private long userId;
        private String province;
        private String age;
        private String sex;
        private long timestamp;
    }


    @Data
    @Builder
    private static class SinkModel {
        private String dimName;
        private String dimValue;
        private long timestamp;
    }

    enum DimNameEnum {
        province,
        age,
        sex,
        ;
    }

}
