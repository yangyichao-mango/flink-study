package flink.examples.datastream._03.state._03_rocksdb;

import java.util.List;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.StateTtlConfig.StateVisibility;
import org.apache.flink.api.common.state.StateTtlConfig.UpdateType;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;

/**
 * https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/fault-tolerance/broadcast_state/
 */

public class Rocksdb_OperatorAndKeyedState_StateStorageDIr_Test {


    public static void main(String[] args) throws Exception {

//        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--execution.savepoint.path", ""});
        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        flinkEnv.env()
                .addSource(new UserDefinedSource())
                .keyBy(new KeySelector<Item, Integer>() {
                    @Override
                    public Integer getKey(Item item) throws Exception {
                        return item.name.hashCode();
                    }
                })
                .process(new KeyedProcessFunction<Integer, Item, String>() {

                    // store partial matches, i.e. first elements of the pair waiting for their second element
                    // we keep a list as we may have many first elements waiting
                    private final MapStateDescriptor<String, Item> mapStateDesc =
                            new MapStateDescriptor<>(
                                    "key1",
                                    String.class
                                    , Item.class);

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        mapStateDesc.enableTimeToLive(StateTtlConfig
                                .newBuilder(Time.hours(24))
                                .setUpdateType(UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateVisibility.NeverReturnExpired)
                                .cleanupFullSnapshot()
                                .build());
                    }


                    @Override
                    public void processElement(Item value, Context ctx, Collector<String> out) throws Exception {

                        MapState<String, Item> mapState = getRuntimeContext().getMapState(mapStateDesc);

                        mapState.put(value.name, value);

                        out.collect(value.name);

                    }
                })
                .keyBy(new KeySelector<String, Integer>() {
                    @Override
                    public Integer getKey(String value) throws Exception {
                        return value.hashCode();
                    }
                })
                .process(new KeyedProcessFunction<Integer, String, String>() {

                    // store partial matches, i.e. first elements of the pair waiting for their second element
                    // we keep a list as we may have many first elements waiting
                    private final MapStateDescriptor<String, String> mapStateDesc =
                            new MapStateDescriptor<>(
                                    "key2",
                                    BasicTypeInfo.STRING_TYPE_INFO,
                                    BasicTypeInfo.STRING_TYPE_INFO);

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        mapStateDesc.enableTimeToLive(StateTtlConfig
                                .newBuilder(Time.hours(24))
                                .setUpdateType(UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateVisibility.NeverReturnExpired)
                                .cleanupFullSnapshot()
                                .build());
                    }


                    @Override
                    public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
                        MapState<String, String> mapState = getRuntimeContext().getMapState(mapStateDesc);

                        mapState.put(value, value);
                    }
                })
                .print();


        flinkEnv.env().execute("广播状态测试任务");

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

    private static class UserDefinedSource extends RichParallelSourceFunction<Item>
            implements CheckpointedFunction {

        private final ListStateDescriptor<Item> listStateDescriptor =
                new ListStateDescriptor<Item>("a", Item.class);

        private volatile boolean isCancel = false;

        private transient ListState<Item> l;

        @Override
        public void run(SourceContext<Item> ctx) throws Exception {

            int i = 0;

            while (!this.isCancel) {
                ctx.collect(
                        Item.builder()
                                .name("item" + i)
                                .color(Color.RED)
                                .shape(Shape.CIRCLE)
                                .build()
                );
                i++;

                List<Item> items = (List<Item>) l.get();

                items.add(Item.builder()
                        .name("item")
                        .color(Color.RED)
                        .shape(Shape.CIRCLE)
                        .build());

                Thread.sleep(1);
            }
        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println(1);
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            this.l = context.getOperatorStateStore().getListState(listStateDescriptor);
        }
    }

}
