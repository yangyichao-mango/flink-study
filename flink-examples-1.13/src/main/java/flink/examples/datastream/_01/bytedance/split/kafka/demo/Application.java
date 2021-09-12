package flink.examples.datastream._01.bytedance.split.kafka.demo;


public class Application {

    private String topicName = "tuzisir";
    private String consumerGrp = "consumerGrp";
    private String brokerUrl = "localhost:9092";

    public static void main(String[] args) throws InterruptedException {


        System.out.println(1);

        Application application = new Application();
        new Thread(new ProducerThread(application), "Producer : ").start();
        new Thread(new ConsumerThread(application), "Consumer1 : ").start();

        //for multiple consumers in same group, start new consumer threads
        //new Thread(new ConsumerThread(application), "Consumer2 : ").start();
    }

    public String getTopicName() {
        return topicName;
    }

    public String getConsumerGrp() {
        return consumerGrp;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

}
