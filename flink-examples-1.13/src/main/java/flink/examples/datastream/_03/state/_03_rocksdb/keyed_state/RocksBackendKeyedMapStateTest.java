package flink.examples.datastream._03.state._03_rocksdb.keyed_state;

import java.util.LinkedList;
import java.util.List;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.StateTtlConfig.TtlTimeCharacteristic;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;

/**
 * https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/fault-tolerance/broadcast_state/
 */

public class RocksBackendKeyedMapStateTest {


    public static void main(String[] args) throws Exception {
        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        flinkEnv.env()
                .addSource(new ParallelSourceFunction<Item>() {

                    private volatile boolean isCancel = false;

                    @Override
                    public void run(SourceContext<Item> ctx) throws Exception {

                        int i = 0;

                        while (!this.isCancel) {
                            ctx.collect(
                                    Item.builder()
                                            .name("item")
                                            .color(Color.RED)
                                            .shape(Shape.CIRCLE)
                                            .build()
                            );
                            i++;
                            Thread.sleep(1000);
                        }
                    }

                    @Override
                    public void cancel() {
                        this.isCancel = true;
                    }
                })
                .keyBy(new KeySelector<Item, Integer>() {
                    @Override
                    public Integer getKey(Item item) throws Exception {
                        return item.color.ordinal();
                    }
                })
                .process(new KeyedProcessFunction<Integer, Item, String>() {

                    // store partial matches, i.e. first elements of the pair waiting for their second element
                    // we keep a list as we may have many first elements waiting
                    private final MapStateDescriptor<String, List<Item>> mapStateDesc =
                            new MapStateDescriptor<>(
                                    "a",
                                    BasicTypeInfo.STRING_TYPE_INFO,
                                    new ListTypeInfo<>(Item.class));

                    // store partial matches, i.e. first elements of the pair waiting for their second element
                    // we keep a list as we may have many first elements waiting
                    private final MapStateDescriptor<String, List<Item>> mapStateDescb =
                            new MapStateDescriptor<>(
                                    "b",
                                    BasicTypeInfo.STRING_TYPE_INFO,
                                    new ListTypeInfo<>(Item.class));

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        mapStateDesc.enableTimeToLive(StateTtlConfig
                                .newBuilder(Time.milliseconds(1))
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .cleanupInRocksdbCompactFilter(10)
                                .build());

                        StateTtlConfig
                                // 1.ttl ??????
                                .newBuilder(Time.milliseconds(1))

                                // 2.????????????
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                                // ?????????????????????
                                .updateTtlOnCreateAndWrite()
                                // ?????????????????????
                                .updateTtlOnReadAndWrite()

                                // 3.??????????????????????????????
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                // ?????????????????????????????????
                                .returnExpiredIfNotCleanedUp()
                                // ????????????????????????
                                .neverReturnExpired()

                                // 4.?????????????????????
                                .setTtlTimeCharacteristic(TtlTimeCharacteristic.ProcessingTime)
                                .useProcessingTime()

                                // 5.????????????
                                // ??? cp ??? sp ???????????????????????????
                                .cleanupFullSnapshot()
                                // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                                // ???????????????????????? HeapStateBeckend???Rocksdb ??????????????????
                                // ????????? 1 ??? state????????? 1000 ???????????????
                                .cleanupIncrementally(1000, true)
                                // Rocksdb ??????????????? rocksdb ??? compaction ????????????????????????
                                // ??? compaction ????????? 3 ??? entry????????????????????????????????????????????????????????????
                                .cleanupInRocksdbCompactFilter(3)
                                // ?????? cleanup
                                .disableCleanupInBackground()
                                .build();

                    }


                    @Override
                    public void processElement(Item value, Context ctx, Collector<String> out) throws Exception {

                        MapState<String, List<Item>> mapState = getRuntimeContext().getMapState(mapStateDesc);

                        List<Item> l = mapState.get(value.name);

                        Object o = mapState.get("??????");

                        if (null == l) {
                            l = new LinkedList<>();
                        }

                        l.add(value);

                        mapState.put(value.name, l);



                    }
                })
                .print();


        flinkEnv.env().execute("????????????????????????");

    }

    @Builder
    @Data
    private static class Rule {
        private String name;
        private Shape first;
        private Shape second;
    }

    @Builder
    @Data
    private static class Item {
        private String name;
        private Shape shape;
        private Color color;

    }


    private enum Shape {
        CIRCLE,
        SQUARE
        ;
    }

    private enum Color {
        RED,
        BLUE,
        BLACK,
        ;
    }

}
