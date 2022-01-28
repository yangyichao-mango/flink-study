package flink.examples.sql._02.timezone;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class TimeZoneTest3 {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        String exampleSql = "CREATE TABLE source_table (\n"
                + "    id BIGINT,\n"
                + "    money BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp_LTZ(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.id.min' = '1',\n"
                + "  'fields.id.max' = '100000',\n"
                + "  'fields.money.min' = '1',\n"
                + "  'fields.money.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    window_end_timestamp bigint,\n"
                + "    window_start_timestamp bigint,\n"
                + "    window_end timestamp(3),\n"
                + "    window_start timestamp(3),\n"
                + "    sum_money BIGINT,\n"
                + "    count_distinct_id BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "SELECT UNIX_TIMESTAMP(CAST(window_end AS STRING)) * 1000 as window_end_timestamp, \n"
                + "      UNIX_TIMESTAMP(CAST(window_start AS STRING)) * 1000 as window_start_timestamp, \n"
                + "      window_end, \n"
                + "      window_start, \n"
                + "      sum(money) as sum_money,\n"
                + "      count(distinct id) as count_distinct_id\n"
                + "FROM TABLE(CUMULATE(\n"
                + "         TABLE source_table\n"
                + "         , DESCRIPTOR(row_time)\n"
                + "         , INTERVAL '1' SECOND\n"
                + "         , INTERVAL '1' DAY))\n"
                + "GROUP BY window_start, \n"
                + "        window_end";

        for (String innerSql : exampleSql.split(";")) {
            flinkEnv.streamTEnv().executeSql(innerSql);
        }
    }
}
