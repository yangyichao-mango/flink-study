package flink.examples.sql._07.query._04_window_agg._02_cumulate_window;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class CumulateWindowTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE TABLE source_table (\n"
                + "    dim STRING,\n"
                + "    user_id BIGINT,\n"
                + "    price BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.dim.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    dim STRING,\n"
                + "    pv BIGINT,\n"
                + "    sum_price BIGINT,\n"
                + "    max_price BIGINT,\n"
                + "    min_price BIGINT,\n"
                + "    uv BIGINT,\n"
                + "    window_end bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "select dim,\n"
                + "       sum(bucket_pv) as pv,\n"
                + "       sum(bucket_sum_price) as sum_price,\n"
                + "       max(bucket_max_price) as max_price,\n"
                + "       min(bucket_min_price) as min_price,\n"
                + "       sum(bucket_uv) as uv,\n"
                + "       max(window_end) as window_end\n"
                + "from (\n"
                + "     SELECT dim,\n"
                + "            UNIX_TIMESTAMP(CAST(window_end AS STRING)) * 1000 as window_end, \n"
                + "            window_start, \n"
                + "            count(*) as bucket_pv,\n"
                + "            sum(price) as bucket_sum_price,\n"
                + "            max(price) as bucket_max_price,\n"
                + "            min(price) as bucket_min_price,\n"
                + "            count(distinct user_id) as bucket_uv\n"
                + "     FROM TABLE(CUMULATE(\n"
                + "               TABLE source_table\n"
                + "               , DESCRIPTOR(row_time)\n"
                + "               , INTERVAL '60' SECOND\n"
                + "               , INTERVAL '1' DAY))\n"
                + "     GROUP BY window_start, \n"
                + "              window_end,\n"
                + "              dim,\n"
                + "              mod(user_id, 1024)\n"
                + ")\n"
                + "group by dim,\n"
                + "         window_end;";

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
                + "    window_end bigint,\n"
                + "    window_start timestamp(3),\n"
                + "    sum_money BIGINT,\n"
                + "    count_distinct_id BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "SELECT UNIX_TIMESTAMP(CAST(window_end AS STRING)) * 1000 as window_end, \n"
                + "      window_start, \n"
                + "      sum(money) as sum_money,\n"
                + "      count(distinct id) as count_distinct_id\n"
                + "FROM TABLE(CUMULATE(\n"
                + "         TABLE source_table\n"
                + "         , DESCRIPTOR(row_time)\n"
                + "         , INTERVAL '60' SECOND\n"
                + "         , INTERVAL '1' DAY))\n"
                + "GROUP BY window_start, \n"
                + "        window_end";

        for (String innerSql : exampleSql.split(";")) {
            flinkEnv.streamTEnv().executeSql(innerSql);
        }
    }

}
