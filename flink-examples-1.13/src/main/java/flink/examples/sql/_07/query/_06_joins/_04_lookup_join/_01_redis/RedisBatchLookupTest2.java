package flink.examples.sql._07.query._06_joins._04_lookup_join._01_redis;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

/**
 * redis 安装：https://blog.csdn.net/realize_dream/article/details/106227622
 * redis java client：https://www.cnblogs.com/chenyanbin/p/12088796.html
 */
public class RedisBatchLookupTest2 {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils
                .getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        flinkEnv.streamTEnv().getConfig()
                .getConfiguration()
                .setBoolean("is.dim.batch.mode", true);

        String exampleSql = "CREATE TABLE show_log (\n"
                + "    log_id BIGINT,\n"
                + "    `timestamp` as cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    user_id STRING,\n"
                + "    proctime AS PROCTIME()\n"
                + ")\n"
                + "WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10000000',\n"
                + "  'fields.user_id.length' = '1',\n"
                + "  'fields.log_id.min' = '1',\n"
                + "  'fields.log_id.max' = '10'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE user_profile (\n"
                + "    user_id STRING,\n"
                + "    age STRING,\n"
                + "    sex STRING\n"
                + "    ) WITH (\n"
                + "  'connector' = 'redis',\n"
                + "  'hostname' = '127.0.0.1',\n"
                + "  'port' = '6379',\n"
                + "  'format' = 'json',\n"
                + "  'lookup.cache.max-rows' = '500',\n"
                + "  'lookup.cache.ttl' = '3600',\n"
                + "  'lookup.max-retries' = '1'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    log_id BIGINT,\n"
                + "    `timestamp` TIMESTAMP(3),\n"
                + "    user_id STRING,\n"
                + "    proctime TIMESTAMP(3),\n"
                + "    age STRING,\n"
                + "    sex STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT \n"
                + "    s.log_id as log_id\n"
                + "    , s.`timestamp` as `timestamp`\n"
                + "    , s.user_id as user_id\n"
                + "    , s.proctime as proctime\n"
                + "    , u.sex as sex\n"
                + "    , u.age as age\n"
                + "FROM show_log AS s\n"
                + "LEFT JOIN user_profile FOR SYSTEM_TIME AS OF s.proctime AS u\n"
                + "ON s.user_id = u.user_id";

        Arrays.stream(exampleSql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);

    }
}
