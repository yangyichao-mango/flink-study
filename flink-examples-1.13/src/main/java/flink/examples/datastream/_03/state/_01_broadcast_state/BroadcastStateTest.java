package flink.examples.datastream._03.state._01_broadcast_state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.Builder;
import lombok.Data;

/**
 * https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/fault-tolerance/broadcast_state/
 */

public class BroadcastStateTest {


    public static void main(String[] args) throws Exception {
        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        // a map descriptor to store the name of the rule (string) and the rule itself.
        MapStateDescriptor<String, Rule> ruleStateDescriptor = new MapStateDescriptor<>(
                "RulesBroadcastState",
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(new TypeHint<Rule>() {
                }));

        // broadcast the rules and create the broadcast state
        BroadcastStream<Rule> ruleBroadcastStream = flinkEnv.env()
                .addSource(new SourceFunction<Rule>() {

                    private volatile boolean isCancel = false;

                    @Override
                    public void run(SourceContext<Rule> ctx) throws Exception {

                        int i = 0;

                        while (!this.isCancel) {
                            ctx.collect(
                                    Rule.builder()
                                            .name("rule" + i)
                                            .first(Shape.CIRCLE)
                                            .second(Shape.SQUARE)
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
                .setParallelism(1)
                .broadcast(ruleStateDescriptor);

        flinkEnv.env()
                .addSource(new ParallelSourceFunction<Item>() {

                    private volatile boolean isCancel = false;

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
                            Thread.sleep(1000);
                        }
                    }

                    @Override
                    public void cancel() {
                        this.isCancel = true;
                    }
                })
                .keyBy(new KeySelector<Item, Color>() {
                    @Override
                    public Color getKey(Item item) throws Exception {
                        return item.color;
                    }
                })
                .connect(ruleBroadcastStream)
                .process(new KeyedBroadcastProcessFunction<Color, Item, Rule, String>() {

                    // store partial matches, i.e. first elements of the pair waiting for their second element
                    // we keep a list as we may have many first elements waiting
                    private final MapStateDescriptor<String, List<Item>> mapStateDesc =
                            new MapStateDescriptor<>(
                                    "items",
                                    BasicTypeInfo.STRING_TYPE_INFO,
                                    new ListTypeInfo<>(Item.class));

                    // identical to our ruleStateDescriptor above
                    private final MapStateDescriptor<String, Rule> ruleStateDescriptor =
                            new MapStateDescriptor<>(
                                    "RulesBroadcastState",
                                    BasicTypeInfo.STRING_TYPE_INFO,
                                    TypeInformation.of(new TypeHint<Rule>() {
                                    }));

                    @Override
                    public void processBroadcastElement(Rule value,
                            Context ctx,
                            Collector<String> out) throws Exception {
                        ctx.getBroadcastState(ruleStateDescriptor).put(value.name, value);
                    }

                    @Override
                    public void processElement(Item value,
                            ReadOnlyContext ctx,
                            Collector<String> out) throws Exception {

                        final MapState<String, List<Item>> state = getRuntimeContext().getMapState(mapStateDesc);
                        final Shape shape = value.getShape();

                        for (Map.Entry<String, Rule> entry
                                : ctx.getBroadcastState(ruleStateDescriptor).immutableEntries()) {
                            final String ruleName = entry.getKey();
                            final Rule rule = entry.getValue();

                            List<Item> stored = state.get(ruleName);
                            if (stored == null) {
                                stored = new ArrayList<>();
                            }

                            if (shape == rule.second && !stored.isEmpty()) {
                                for (Item i : stored) {
                                    out.collect("MATCH: " + i + " - " + value);
                                }
                                stored.clear();
                            }

                            // there is no else{} to cover if rule.first == rule.second
                            if (shape.equals(rule.first)) {
                                stored.add(value);
                            }

                            if (stored.isEmpty()) {
                                state.remove(ruleName);
                            } else {
                                state.put(ruleName, stored);
                            }
                        }
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
