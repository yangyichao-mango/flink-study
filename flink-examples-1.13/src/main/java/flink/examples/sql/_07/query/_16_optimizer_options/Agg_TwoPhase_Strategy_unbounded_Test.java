package flink.examples.sql._07.query._16_optimizer_options;

import org.apache.flink.table.api.TableResult;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Agg_TwoPhase_Strategy_unbounded_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--enable.hive.module.v2", "false"});

        flinkEnv.env().setParallelism(1);

        flinkEnv.streamTEnv()
                .getConfig()
                .getConfiguration()
                .setString("table.optimizer.agg-phase-strategy", "TWO_PHASE");

        flinkEnv.streamTEnv()
                .getConfig()
                .getConfiguration()
                .setString("table.exec.mini-batch.enabled", "true");

        flinkEnv.streamTEnv()
                .getConfig()
                .getConfiguration()
                .setString("table.exec.mini-batch.allow-latency", "60 s");

        flinkEnv.streamTEnv()
                .getConfig()
                .getConfiguration()
                .setString("table.exec.mini-batch.size", "1000000000");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT COMMENT '用户 id',\n"
                + "    name STRING COMMENT '用户姓名',\n"
                + "    server_timestamp BIGINT COMMENT '用户访问时间戳',\n"
                + "    proctime AS PROCTIME()\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.name.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '10',\n"
                + "  'fields.server_timestamp.min' = '1',\n"
                + "  'fields.server_timestamp.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    cnt BIGINT,\n"
                + "    server_timestamp BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    user_id,\n"
                + "    count(1) as cnt,\n"
                + "    max(cast(server_timestamp as bigint)) as server_timestamp\n"
                + "FROM source_table\n"
                + "GROUP BY\n"
                + "    user_id";

        for (String innerSql : sql.split(";")) {
            TableResult tableResult = flinkEnv.streamTEnv().executeSql(innerSql);

            tableResult.print();
        }
    }

}
