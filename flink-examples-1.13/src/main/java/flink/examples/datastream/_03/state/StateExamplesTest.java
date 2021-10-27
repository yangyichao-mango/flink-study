package flink.examples.datastream._03.state;

import java.util.LinkedList;
import java.util.List;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
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

public class StateExamplesTest {


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
                                    "itemsMap",
                                    BasicTypeInfo.STRING_TYPE_INFO,
                                    new ListTypeInfo<>(Item.class));

                    private final ListStateDescriptor<Item> listStateDesc =
                            new ListStateDescriptor<>(
                                    "itemsList",
                                    Item.class);

                    private final ValueStateDescriptor<Item> valueStateDesc =
                            new ValueStateDescriptor<>(
                                    "itemsValue"
                                    , Item.class);

                    private final ReducingStateDescriptor<String> reducingStateDesc =
                            new ReducingStateDescriptor<>(
                                    "itemsReducing"
                                    , new ReduceFunction<String>() {
                                @Override
                                public String reduce(String value1, String value2) throws Exception {
                                    return value1 + value2;
                                }
                            }, String.class);

                    private final AggregatingStateDescriptor<Item, String, String> aggregatingStateDesc =
                            new AggregatingStateDescriptor<Item, String, String>("itemsAgg",
                                    new AggregateFunction<Item, String, String>() {
                                        @Override
                                        public String createAccumulator() {
                                            return "";
                                        }

                                        @Override
                                        public String add(Item value, String accumulator) {
                                            return accumulator + value.name;
                                        }

                                        @Override
                                        public String getResult(String accumulator) {
                                            return accumulator;
                                        }

                                        @Override
                                        public String merge(String a, String b) {
                                            return null;
                                        }
                                    }, String.class);

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        mapStateDesc.enableTimeToLive(StateTtlConfig
                                .newBuilder(Time.milliseconds(1))
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .cleanupInRocksdbCompactFilter(10)
                                .build());

                    }


                    @Override
                    public void processElement(Item value, Context ctx, Collector<String> out) throws Exception {

                        MapState<String, List<Item>> mapState = getRuntimeContext().getMapState(mapStateDesc);

                        List<Item> l = mapState.get(value.name);

                        if (null == l) {
                            l = new LinkedList<>();
                        }

                        l.add(value);

                        mapState.put(value.name, l);

                        ListState<Item> listState = getRuntimeContext().getListState(listStateDesc);

                        listState.add(value);

                        Object o = listState.get();

                        ValueState<Item> valueState = getRuntimeContext().getState(valueStateDesc);

                        valueState.update(value);

                        Item i = valueState.value();

                        AggregatingState<Item, String> aggregatingState = getRuntimeContext().getAggregatingState(aggregatingStateDesc);

                        aggregatingState.add(value);

                        String aggResult = aggregatingState.get();

                        ReducingState<String> reducingState = getRuntimeContext().getReducingState(reducingStateDesc);

                        reducingState.add(value.name);

                        String reducingResult = reducingState.get();

                        System.out.println(1);

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

}
