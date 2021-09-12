package flink.examples.datastream._01.bytedance.split.kafka;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import flink.examples.datastream._01.bytedance.split.zkconfigcenter.ZkBasedConfigCenter;


public class KafkaProducerCenter {

    private final ConcurrentMap<String, Producer<String, String>> producerConcurrentMap
            = new ConcurrentHashMap<>();

    private KafkaProducerCenter() {
        ZkBasedConfigCenter.getInstance()
                .getMap()
                .values()
                .forEach(d -> getProducer(d.getTargetTopic()));
    }

    private static class Factory {
        private static final KafkaProducerCenter INSTANCE = new KafkaProducerCenter();
    }

    public static KafkaProducerCenter getInstance() {
        return Factory.INSTANCE;
    }

    private Producer<String, String> getProducer(String topicName) {

        Producer<String, String> producer = producerConcurrentMap.get(topicName);

        if (null != producer) {
            return producer;
        }

        return producerConcurrentMap.computeIfAbsent(topicName, new Function<String, Producer<String, String>>() {
            @Override
            public Producer<String, String> apply(String topicName) {
                Properties props = new Properties();
                props.put("bootstrap.servers", "localhost:9092");
                props.put("acks", "all");
                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                return new KafkaProducer<>(props);
            }
        });

    }

    public void send(String topicName, String message) {

        final ProducerRecord<String, String> record = new ProducerRecord<>(topicName,
                "", message);
        try {
            RecordMetadata metadata = getProducer(topicName).send(record).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        this.producerConcurrentMap.forEach(new BiConsumer<String, Producer<String, String>>() {
            @Override
            public void accept(String s, Producer<String, String> stringStringProducer) {
                stringStringProducer.close();
            }
        });
    }

}
