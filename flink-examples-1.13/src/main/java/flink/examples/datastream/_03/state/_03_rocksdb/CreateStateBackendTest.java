//package flink.examples.datastream._03.state._03_rocksdb;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.state.StateTtlConfig;
//import org.apache.flink.api.common.state.StateTtlConfig.TtlTimeCharacteristic;
//import org.apache.flink.api.common.time.Time;
//import org.apache.flink.api.java.functions.KeySelector;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
//import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
//import org.apache.flink.util.Collector;
//
//import flink.examples.FlinkEnvUtils;
//import flink.examples.FlinkEnvUtils.FlinkEnv;
//import lombok.Builder;
//import lombok.Data;
//
///**
// * https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/fault-tolerance/broadcast_state/
// */
//
//public class CreateStateBackendTest {
//
//
//    public static void main(String[] args) throws Exception {
//        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);
//
//        flinkEnv.env().setParallelism(1);
//
//        flinkEnv.env()
//                .addSource(new ParallelSourceFunction<Item>() {
//
//                    private volatile boolean isCancel = false;
//
//                    @Override
//                    public void run(SourceContext<Item> ctx) throws Exception {
//
//                        int i = 0;
//
//                        while (!this.isCancel) {
//                            ctx.collect(
//                                    Item.builder()
//                                            .name("item")
//                                            .color(Color.RED)
//                                            .shape(Shape.CIRCLE)
//                                            .build()
//                            );
//                            i++;
//                            Thread.sleep(1000);
//                        }
//                    }
//
//                    @Override
//                    public void cancel() {
//                        this.isCancel = true;
//                    }
//                })
//                .keyBy(new KeySelector<Item, Integer>() {
//                    @Override
//                    public Integer getKey(Item item) throws Exception {
//                        return item.color.ordinal();
//                    }
//                })
//                .process(new KeyedProcessFunction<Integer, Item, String>() {
//
//                    // store partial matches, i.e. first elements of the pair waiting for their second element
//                    // we keep a list as we may have many first elements waiting
//                    private MapStateDescriptor<String, String> mapStateDescriptor =
//                            new MapStateDescriptor<>("map state name", String.class, String.class);
//
//                    private transient MapState<String, String> mapState;
//
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//                        super.open(parameters);
//
//                        StateTtlConfig stateTtlConfig = StateTtlConfig
//                                // 1.ttl 时长
//                                .newBuilder(Time.milliseconds(1))
//
//                                // 2.更新类型
//                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
//                                // 创建和写入更新
//                                .updateTtlOnCreateAndWrite()
//                                // 读取和写入更新
//                                .updateTtlOnReadAndWrite()
//
//                                // 3.过期状态的访问可见性
//                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
//                                // 如果还没有被删除就返回
//                                .returnExpiredIfNotCleanedUp()
//                                // 过期的永远不返回
//                                .neverReturnExpired()
//
//                                // 4.过期的时间语义
//                                .setTtlTimeCharacteristic(TtlTimeCharacteristic.ProcessingTime)
//                                .useProcessingTime()
//
//                                // 5.清除策略
//                                // 做 CK 时把所有状态删除掉
//                                .cleanupFullSnapshot()
//                                // 增量删除，只有有状态记录访问时，才会做删除；并且他会加大任务处理延迟。
//                                // 增量删除仅仅支持 HeapStateBeckend，Rocksdb 不支持！！！
//                                // 每访问 1 此 state，遍历 1000 条进行删除
//                                .cleanupIncrementally(1000, true)
//                                // Rocksdb 状态后端在 rocksdb 做 compaction 时清除过期状态。
//                                // 做 compaction 时每隔 3 个 entry，重新更新一下时间戳（用于判断是否过期）
//                                .cleanupInRocksdbCompactFilter(3)
//                                // 禁用 cleanup
//                                .disableCleanupInBackground()
//                                .build();
//
//                        this.mapStateDescriptor.enableTimeToLive(stateTtlConfig);
//                        this.mapState = this.getRuntimeContext().getMapState(mapStateDescriptor);
//                    }
//
//
//                    @Override
//                    public void processElement(Item value, Context ctx, Collector<String> out) throws Exception {
//
//                        MapState<String, List<Item>> mapState = getRuntimeContext().getMapState(mapStateDesc);
//
//                        List<Item> l = mapState.get(value.name);
//
//                        Object o = mapState.get("测试");
//
//                        if (null == l) {
//                            l = new LinkedList<>();
//                        }
//
//                        l.add(value);
//
//                        mapState.put(value.name, l);
//
//
//
//                    }
//                })
//                .print();
//
//
//        flinkEnv.env().execute("广播状态测试任务");
//
//    }
//
//    @Builder
//    @Data
//    private static class Rule {
//        private String name;
//        private Shape first;
//        private Shape second;
//    }
//
//    @Builder
//    @Data
//    private static class Item {
//        private String name;
//        private Shape shape;
//        private Color color;
//
//    }
//
//
//    private enum Shape {
//        CIRCLE,
//        SQUARE
//        ;
//    }
//
//    private enum Color {
//        RED,
//        BLUE,
//        BLACK,
//        ;
//    }
//
//}
