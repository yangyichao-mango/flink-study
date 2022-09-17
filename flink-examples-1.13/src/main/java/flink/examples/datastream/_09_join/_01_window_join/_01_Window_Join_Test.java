//package flink.examples.datastream._09_join._01_window_join;
//
//import org.apache.flink.api.common.functions.FlatJoinFunction;
//import org.apache.flink.api.common.functions.JoinFunction;
//import org.apache.flink.api.java.functions.KeySelector;
//import org.apache.flink.streaming.api.functions.source.SourceFunction;
//import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
//import org.apache.flink.streaming.api.windowing.time.Time;
//import org.apache.flink.util.Collector;
//
//import flink.examples.FlinkEnvUtils;
//import flink.examples.FlinkEnvUtils.FlinkEnv;
//
//
//public class _01_Window_Join_Test {
//
//    public static void main(String[] args) throws Exception {
//
//        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);
//
//        flinkEnv.env().setParallelism(1);
//
//        flinkEnv.env()
//                .addSource(new SourceFunction<Object>() {
//                    @Override
//                    public void run(SourceContext<Object> ctx) throws Exception {
//
//                    }
//
//                    @Override
//                    public void cancel() {
//
//                    }
//                })
//                .join(flinkEnv.env().addSource(new SourceFunction<Object>() {
//                    @Override
//                    public void run(SourceContext<Object> ctx) throws Exception {
//
//                    }
//
//                    @Override
//                    public void cancel() {
//
//                    }
//                }))
//                .where(new KeySelector<Object, Object>() {
//                    @Override
//                    public Object getKey(Object value) throws Exception {
//                        return null;
//                    }
//                })
//                .equalTo(new KeySelector<Object, Object>() {
//                    @Override
//                    public Object getKey(Object value) throws Exception {
//                        return null;
//                    }
//                })
//                .window(TumblingEventTimeWindows.of(Time.seconds(60)))
//                .apply(new FlatJoinFunction<Object, Object, Object>() {
//                    @Override
//                    public void join(Object first, Object second, Collector<Object> out) throws Exception {
//
//                    }
//                })
//                .apply(new JoinFunction<Object, Object, Object>() {
//                    @Override
//                    public Object join(Object first, Object second) throws Exception {
//                        return null;
//                    }
//                });
//    }
//
//}
