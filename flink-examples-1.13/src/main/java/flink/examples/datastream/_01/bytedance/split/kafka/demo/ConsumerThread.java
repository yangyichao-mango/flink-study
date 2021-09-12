package flink.examples.datastream._01.bytedance.split.kafka.demo;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


public class ConsumerThread implements Runnable {

    private Consumer<String, String> consumer;

    public ConsumerThread(Application application) {
        Properties props = new Properties();
        props.put("bootstrap.servers", application.getBrokerUrl());
        props.put("group.id", application.getConsumerGrp());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put("auto.offset.reset", "earliest");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(application.getTopicName()));
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        int noMessageToFetch = 1;
        while (noMessageToFetch < 3) {
            System.out.println(threadName + "poll start..");
            final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            System.out.println(threadName + "records polled : " + consumerRecords.count());
            if (consumerRecords.count() == 0) {
                noMessageToFetch++;
                continue;
            }
            for (ConsumerRecord<String, String> record : consumerRecords) {
                System.out.printf(threadName + "offset = %d, key = %s, value = %s, partition =%d%n",
                        record.offset(), record.key(), record.value(), record.partition());
            }
            consumer.commitAsync();
        }
    }

}
