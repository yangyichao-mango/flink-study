package flink.examples.datastream._09_join._02_connect;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class _01_Connect_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        flinkEnv.env()
                .addSource(new SourceFunction<Object>() {
                    @Override
                    public void run(SourceContext<Object> ctx) throws Exception {

                    }

                    @Override
                    public void cancel() {

                    }
                })
                .keyBy(new KeySelector<Object, Object>() {
                    @Override
                    public Object getKey(Object value) throws Exception {
                        return null;
                    }
                })
                .connect(flinkEnv.env().addSource(new SourceFunction<Object>() {
                    @Override
                    public void run(SourceContext<Object> ctx) throws Exception {

                    }

                    @Override
                    public void cancel() {

                    }
                }).keyBy(new KeySelector<Object, Object>() {
                    @Override
                    public Object getKey(Object value) throws Exception {
                        return null;
                    }
                }))
                .process(new KeyedCoProcessFunction<Object, Object, Object, Object>() {

                    private transient MapState<String, String> mapState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        this.mapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, String>("a", String.class, String.class));
                    }

                    @Override
                    public void processElement1(Object value, Context ctx, Collector<Object> out) throws Exception {

                    }

                    @Override
                    public void processElement2(Object value, Context ctx, Collector<Object> out) throws Exception {

                    }
                })
                .print();
    }

}
