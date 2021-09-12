# 1.kafka 初始化

cd /kafka-bin-目录

# 启动 kafka server
./kafka-server-start /usr/local/etc/kafka/server.properties &

# 创建 3 个 topic
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tuzisir

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tuzisir1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tuzisir2

# 启动一个 console consumer

kafka-console-consumer --bootstrap-server localhost:9092 --topic tuzisir --from-beginning

# 2.zk 初始化

cd /zk-bin-目录

zkServer start

zkCli -server 127.0.0.1:2181

# zkCli 中需要执行的命令
create /kafka-config {"1":{"condition":"1==1","targetTopic":"tuzisir1"},"2":{"condition":"1!=1","targetTopic":"tuzisir2"}}

get /kafka-config
