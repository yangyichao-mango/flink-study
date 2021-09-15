package flink.examples.question.sql._01.lots_source_fields_poor_performance;

import static net.mguenther.kafka.junit.ObserveKeyValues.on;
import static net.mguenther.kafka.junit.SendValues.to;

import lombok.SneakyThrows;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig;

public class EmbeddedKafka {

    public static void main(String[] args) {
        EmbeddedKafkaCluster kafkaCluster =
                EmbeddedKafkaCluster.provisionWith(EmbeddedKafkaClusterConfig.defaultClusterConfig());
        kafkaCluster.start();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    kafkaCluster.send(to("test-topic", "a", "b", "c"));
                    Thread.sleep(1000);
                }
            }
        }).start();


        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    kafkaCluster.observe(on("test-topic", 3))
                            .forEach(a -> System.out.println(a.getValue()));
                }
            }
        }).start();


    }

}
