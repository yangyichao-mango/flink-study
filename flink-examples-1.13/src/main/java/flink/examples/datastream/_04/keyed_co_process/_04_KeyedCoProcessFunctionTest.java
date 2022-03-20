package flink.examples.datastream._04.keyed_co_process;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.StateTtlConfig.StateVisibility;
import org.apache.flink.api.common.state.StateTtlConfig.UpdateType;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;

import com.twitter.chill.protobuf.ProtobufSerializer;

import flink.examples.JacksonUtils;
import flink.examples.datastream._04.keyed_co_process.protobuf.Source;
import flink.examples.sql._05.format.formats.protobuf.Test;

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
        env.getCheckpointConfig()
                .enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        env.registerTypeWithKryoSerializer(Source.class, ProtobufSerializer.class);
        env.registerTypeWithKryoSerializer(Test.class, ProtobufSerializer.class);


        KeyedStream<Source, Integer> source1 = env
                .addSource(new UserDefineSource1())
                .uid("source1")
                .keyBy(new KeySelector<Source, Integer>() {
                    @Override
                    public Integer getKey(Source value) throws Exception {
                        return value.getName().hashCode() % 1024;
                    }
                });

        KeyedStream<Source, Integer> source2 = env
                .addSource(new UserDefineSource2())
                .uid("source2")
                .keyBy(new KeySelector<Source, Integer>() {
                    @Override
                    public Integer getKey(Source value) throws Exception {
                        return value.getName().hashCode() % 1024;
                    }
                });

        source1.connect(source2)
                .process(new KeyedCoProcessFunction<Integer, Source, Source, Test>() {

                    private transient MapState<String, Source>
                            source1State;

                    private transient MapState<String, Source>
                            source2State;

                    private StateTtlConfig getStateTtlConfig() {
                        return StateTtlConfig
                                .newBuilder(Time.hours(1))
                                .setUpdateType(UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateVisibility.NeverReturnExpired)
                                .cleanupIncrementally(3, true)
                                .build();
                    }

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        MapStateDescriptor<String, Source>
                                source1StateDescriptor =
                                new MapStateDescriptor<String, Source>(
                                        "source1State"
                                        , TypeInformation.of(String.class)
                                        , TypeInformation.of(Source.class));

                        source1StateDescriptor
                                .enableTimeToLive(getStateTtlConfig());

                        this.source1State =
                                getRuntimeContext().getMapState(source1StateDescriptor);

                        MapStateDescriptor<String, Source>
                                source2StateDescriptor =
                                new MapStateDescriptor<String, Source>(
                                        "source2State"
                                        , TypeInformation.of(String.class)
                                        , TypeInformation.of(Source.class));

                        source2StateDescriptor
                                .enableTimeToLive(getStateTtlConfig());

                        this.source2State =
                                getRuntimeContext().getMapState(source2StateDescriptor);
                    }

                    @Override
                    public void processElement1(Source value, Context ctx, Collector<Test> out) throws Exception {
                        ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 10000);
                        this.source1State.put(value.getName(), value);
                    }

                    @Override
                    public void processElement2(Source value, Context ctx, Collector<Test> out) throws Exception {
                        ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 10000);
                        this.source2State.put(value.getName(), value);
                    }

                    @Override
                    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Test> out) throws Exception {

                        for (Entry<String, Source> e : this.source1State.entries()) {
                            this.source1State.remove(e.getKey());
                            out.collect(Test
                                    .newBuilder()
                                    .setName(e.getValue().getName())
                                    .build());
                        }

                        for (Entry<String, Source> e : this.source2State.entries()) {
                            this.source1State.remove(e.getKey());
                            out.collect(Test
                                    .newBuilder()
                                    .setName(e.getValue().getName())
                                    .build());
                        }

//                        this.source1State.iterator()
//                                .forEachRemaining(a -> {
//                                    out.collect(Test
//                                            .newBuilder()
//                                            .setName(a.getValue().getName())
//                                            .build()
//                                    );
//                                    try {
//                                        this.source1State.remove(a.getKey());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                });
//
//                        this.source2State.iterator()
//                                .forEachRemaining(a -> {
//                                    out.collect(Test
//                                            .newBuilder()
//                                            .setName(a.getValue().getName())
//                                            .build()
//                                    );
//                                    try {
//                                        this.source2State.remove(a.getKey());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                });
                    }
                })
                .uid("process")
                .disableChaining()
                .addSink(new SinkFunction<Test>() {
                    @Override
                    public void invoke(Test value, Context context) throws Exception {
                        System.out.println(JacksonUtils.bean2Json(value));
                    }
                })
                .uid("sink");

        env.execute("KeyedCoProcessFunction 测试");
    }


    private static class UserDefineSource1 extends RichSourceFunction<Source> {

        private volatile boolean isCancel = false;

        @Override
        public void run(SourceContext<Source> ctx) throws Exception {

            int i = 0;

            while (!this.isCancel) {
                ctx.collect(
                        Source.newBuilder()
                                .setName("antigenral-from-source-" + i)
                                .build()
                );
                i++;

                if (i == 20) {
                    i = 0;
                }
                Thread.sleep(100);
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }

    private static class UserDefineSource2 extends RichSourceFunction<Source> {

        private volatile boolean isCancel = false;

        @Override
        public void run(SourceContext<Source> ctx) throws Exception {

            int i = 0;

            while (!this.isCancel) {
                ctx.collect(
                        Source.getDefaultInstance()
                );
                i++;

                if (i == 20) {
                    i = 0;
                }
                Thread.sleep(100);
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }


}
