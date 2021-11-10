package flink.examples.sql._03.source_sink;

import org.apache.flink.configuration.Configuration;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


/**
 * zk：https://www.jianshu.com/p/5491d16e6abd
 * /usr/local/Cellar/zookeeper/3.4.13/bin/zkServer start
 *
 * kafka：https://www.jianshu.com/p/dd2578d47ff6
 * /usr/local/Cellar/kafka/2.2.1/bin/kafka-server-start /usr/local/Cellar/kafka/2.2.1/libexec/config/server.properties &
 *
 * 创建 topic：kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tuzisir
 * 查看 topic：kafka-topics --list --zookeeper localhost:2181
 * 向 topic 发消息：kafka-console-producer --broker-list localhost:9092 --topic tuzisir
 * 从 topic 消费消息：kafka-console-consumer --bootstrap-server localhost:9092 --topic tuzisir --from-beginning
 */
public class UpsertKafkaSinkProtobufFormatSupportTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        Configuration configuration = flinkEnv.streamTEnv().getConfig().getConfiguration();
        // set low-level key-value options

        configuration.setString("table.exec.mini-batch.enabled", "true"); // enable mini-batch optimization
        configuration.setString("table.exec.mini-batch.allow-latency", "5 s"); // use 5 seconds to buffer input records
        configuration.setString("table.exec.mini-batch.size", "5000"); // the maximum number of records can be buffered by each aggregate operator task
        configuration.setString("pipeline.name", "GROUP AGG MINI BATCH 案例"); // the maximum number of records can be buffered by each aggregate operator task


        String sourceSql = "CREATE TABLE source_table (\n"
                + "    order_id STRING,\n"
                + "    price BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.order_id.length' = '1',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '1000000'\n"
                + ")";

        String sinkSql = "CREATE TABLE sink_table (\n"
                + "    order_id STRING,\n"
                + "    count_result BIGINT,\n"
                + "    sum_result BIGINT,\n"
                + "    avg_result DOUBLE,\n"
                + "    min_result BIGINT,\n"
                + "    max_result BIGINT,\n"
                + "    PRIMARY KEY (`order_id`) NOT ENFORCED\n"
                + ") WITH (\n"
                + "  'connector' = 'upsert-kafka',\n"
                + "  'topic' = 'tuzisir',\n"
                + "  'properties.bootstrap.servers' = 'localhost:9092',\n"
                + "  'key.format' = 'json',\n"
                + "  'value.format' = 'protobuf',\n"
                + "  'value.protobuf.class-name' = 'flink.examples.sql._04.format.formats.protobuf.Test'\n"
                + ")";

        String selectWhereSql = "insert into sink_table\n"
                + "select order_id,\n"
                + "       count(*) as count_result,\n"
                + "       sum(price) as sum_result,\n"
                + "       avg(price) as avg_result,\n"
                + "       min(price) as min_result,\n"
                + "       max(price) as max_result\n"
                + "from source_table\n"
                + "group by order_id";

        flinkEnv.streamTEnv().executeSql(sourceSql);
        flinkEnv.streamTEnv().executeSql(sinkSql);
        flinkEnv.streamTEnv().executeSql(selectWhereSql);
    }
}