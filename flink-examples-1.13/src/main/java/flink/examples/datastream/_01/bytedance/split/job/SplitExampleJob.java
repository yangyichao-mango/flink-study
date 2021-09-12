package flink.examples.datastream._01.bytedance.split.job;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import flink.examples.datastream._01.bytedance.split.kafka.KafkaProducerCenter;
import flink.examples.datastream._01.bytedance.split.model.ClientLogSink;
import flink.examples.datastream._01.bytedance.split.model.ClientLogSource;
import flink.examples.datastream._01.bytedance.split.model.DynamicProducerRule;
import flink.examples.datastream._01.bytedance.split.zkconfigcenter.ZkBasedConfigCenter;

/**
 * zk：https://www.jianshu.com/p/5491d16e6abd
 * kafka：https://www.jianshu.com/p/dd2578d47ff6
 */
public class SplitExampleJob {

    public static void main(String[] args) throws Exception {

        ParameterTool parameters = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 其他参数设置
        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
        env.getConfig().setGlobalJobParameters(parameters);
        env.setMaxParallelism(2);

        // ck 设置
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        env.enableCheckpointing(30 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3L);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        env.addSource(new UserDefinedSource())
                .process(new ProcessFunction<ClientLogSource, ClientLogSink>() {

                    private ZkBasedConfigCenter zkBasedConfigCenter;

                    private KafkaProducerCenter kafkaProducerCenter;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        this.zkBasedConfigCenter = ZkBasedConfigCenter.getInstance();
                        this.kafkaProducerCenter = KafkaProducerCenter.getInstance();

                    }

                    @Override
                    public void processElement(ClientLogSource clientLogSource, Context context, Collector<ClientLogSink> collector)
                            throws Exception {

                        this.zkBasedConfigCenter.getMap().forEach(new BiConsumer<Long, DynamicProducerRule>() {
                            @Override
                            public void accept(Long id, DynamicProducerRule dynamicProducerRule) {

                                if (dynamicProducerRule.eval(clientLogSource)) {
                                    kafkaProducerCenter.send(dynamicProducerRule.getTargetTopic(), clientLogSource.toString());
                                }

                            }
                        });
                    }

                    @Override
                    public void close() throws Exception {
                        super.close();
                        this.zkBasedConfigCenter.close();
                        this.kafkaProducerCenter.close();
                    }
                });

        env.execute();
    }

    private static class UserDefinedSource implements SourceFunction<ClientLogSource> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<ClientLogSource> sourceContext) throws Exception {

            while (!this.isCancel) {
                sourceContext.collect(
                        ClientLogSource
                                .builder()
                                .id(RandomUtils.nextInt(0, 10))
                                .price(RandomUtils.nextInt(0, 100))
                                .timestamp(System.currentTimeMillis())
                                .date(new Date().toString())
                                .build()
                );

                Thread.sleep(1000L);
            }

        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }
    }

}
