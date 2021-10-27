package flink.examples.datastream._03.state._04_filesystem.operator_state;

import java.util.List;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
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

public class FsStateBackendOperatorListStateTest {


    public static void main(String[] args) throws Exception {
        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--state.backend", "filesystem"});

        flinkEnv.env().setParallelism(1);

        flinkEnv.env()
                .addSource(new UserDefinedSource())
                .keyBy(new KeySelector<Item, Integer>() {
                    @Override
                    public Integer getKey(Item item) throws Exception {
                        return item.color.ordinal();
                    }
                })
                .process(new KeyedProcessFunction<Integer, Item, String>() {

                    @Override
                    public void processElement(Item value, Context ctx, Collector<String> out) throws Exception {

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
        SQUARE;
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
                                .name("item")
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

                l.update(items);


                Thread.sleep(1000);
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
