package flink.examples.sql._07.query._13_window_topn;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class WindowTopN_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        String sql = "CREATE TABLE source_table (\n"
                + "    name BIGINT NOT NULL,\n"
                + "    search_cnt BIGINT NOT NULL,\n"
                + "    key BIGINT NOT NULL,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10',\n"
                + "  'fields.name.min' = '1',\n"
                + "  'fields.name.max' = '10',\n"
                + "  'fields.key.min' = '1',\n"
                + "  'fields.key.max' = '2',\n"
                + "  'fields.search_cnt.min' = '1000',\n"
                + "  'fields.search_cnt.max' = '10000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    key BIGINT,\n"
                + "    name BIGINT,\n"
                + "    search_cnt BIGINT,\n"
                + "    window_start TIMESTAMP(3),\n"
                + "    window_end TIMESTAMP(3)\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT key, name, search_cnt, window_start, window_end\n"
                + "FROM (\n"
                + "   SELECT key, name, search_cnt, window_start, window_end, \n"
                + "     ROW_NUMBER() OVER (PARTITION BY window_start, window_end, key\n"
                + "       ORDER BY search_cnt desc) AS rownum\n"
                + "   FROM (\n"
                + "      SELECT window_start, window_end, key, name, max(search_cnt) as search_cnt\n"
                + "      FROM TABLE(TUMBLE(TABLE source_table, DESCRIPTOR(row_time), INTERVAL '1' MINUTES))\n"
                + "      GROUP BY window_start, window_end, key, name\n"
                + "   )\n"
                + ")\n"
                + "WHERE rownum <= 100\n";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }


}