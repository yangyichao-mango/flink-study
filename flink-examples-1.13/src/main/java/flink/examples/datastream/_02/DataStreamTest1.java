//package flink.examples.datastream._02;
//
//import java.io.IOException;
//import java.util.Properties;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//
//import org.apache.commons.lang3.RandomUtils;
//import org.apache.flink.api.common.restartstrategy.RestartStrategies;
//import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
//import org.apache.flink.api.common.serialization.DeserializationSchema;
//import org.apache.flink.api.java.functions.KeySelector;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.api.java.utils.ParameterTool;
//import org.apache.flink.streaming.api.CheckpointingMode;
//import org.apache.flink.streaming.api.TimeCharacteristic;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.CheckpointConfig;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.source.SourceFunction;
//import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
//import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
//import org.apache.flink.streaming.api.windowing.time.Time;
//import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
//import org.apache.flink.util.Collector;
//
//import lombok.Builder;
//import lombok.Data;
//
//
//public class DataStreamTest1 {
//
//    public static void main(String[] args) throws Exception {
//
//        ParameterTool parameters = ParameterTool.fromArgs(args);
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//
//        // 其他参数设置
//        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
//                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
//        env.getConfig().setGlobalJobParameters(parameters);
//        env.setMaxParallelism(2);
//
//        // ck 设置
//        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
//        env.enableCheckpointing(30 * 1000L, CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3L);
//        env.getCheckpointConfig()
//                .enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//
//        env.setParallelism(1);
//
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//        properties.setProperty("group.id", "test");
//
//        DeserializationSchema<Tuple2<String, String>> d = new AbstractDeserializationSchema<Tuple2<String, String>>() {
//
//            @Override
//            public Tuple2<String, String> deserialize(byte[] message) throws IOException {
//                return null;
//            }
//        };
//
//        DataStream<Tuple2<String, String>> stream = env
//                .addSource(new FlinkKafkaConsumer<>("topic", d, properties));
//
//        DataStream<MidModel> eventTimeResult =
//                env
//                        .addSource(new UserDefinedSource())
//                        .map()
//                        .flatMap()
//                        .process()
//                        .keyBy()
//                        .sum()
//
//
//        DataStream<SinkModel> processingTimeResult = eventTimeResult
//                .keyBy(new KeySelector<MidModel, Integer>() {
//                    @Override
//                    public Integer getKey(MidModel midModel) throws Exception {
//                        return midModel.getId();
//                    }
//                })
//                // ！！！处理时间窗口
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(1L)))
//                .process(new ProcessWindowFunction<MidModel, SinkModel, Integer, TimeWindow>() {
//                    @Override
//                    public void process(Integer integer, Context context, Iterable<MidModel> iterable,
//                            Collector<SinkModel> collector) throws Exception {
//
//                        iterable.forEach(new Consumer<MidModel>() {
//                            @Override
//                            public void accept(MidModel midModel) {
//                                collector.collect(
//                                        SinkModel
//                                                .builder()
//                                                .id(midModel.getId())
//                                                .price(midModel.getPrice())
//                                                .timestamp(midModel.getTimestamp())
//                                                .build()
//                                );
//                            }
//                        });
//
//                    }
//                })
//                .uid("process-process-time");
//
//        processingTimeResult.print();
//
//        env.execute();
//    }
//
//    @Data
//    @Builder
//    private static class SourceModel {
//        private int id;
//        private int price;
//        private long timestamp;
//    }
//
//    @Data
//    @Builder
//    private static class MidModel {
//        private int id;
//        private int price;
//        private long timestamp;
//    }
//
//    @Data
//    @Builder
//    private static class SinkModel {
//        private int id;
//        private int price;
//        private long timestamp;
//    }
//
//    private static class UserDefinedSource implements SourceFunction<SourceModel> {
//
//        private volatile boolean isCancel;
//
//        @Override
//        public void run(SourceContext<SourceModel> sourceContext) throws Exception {
//
//            while (!this.isCancel) {
//                sourceContext.collect(
//                        SourceModel
//                                .builder()
//                                .id(RandomUtils.nextInt(0, 10))
//                                .price(RandomUtils.nextInt(0, 100))
//                                .timestamp(System.currentTimeMillis())
//                                .build()
//                );
//
//                Thread.sleep(10L);
//            }
//
//        }
//
//        @Override
//        public void cancel() {
//            this.isCancel = true;
//        }
//    }
//
//}
