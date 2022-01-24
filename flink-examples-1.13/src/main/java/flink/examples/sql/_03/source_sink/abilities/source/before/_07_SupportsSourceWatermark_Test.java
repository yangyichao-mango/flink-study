package flink.examples.sql._03.source_sink.abilities.source.before;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class _07_SupportsSourceWatermark_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.5 用户自定义 SOURCE 案例");

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("state.backend", "rocksdb");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    flink_read_timestamp BIGINT METADATA VIRTUAL,\n"
                + "    row_time AS TO_TIMESTAMP_LTZ(flink_read_timestamp, 3),\n"
                + "    WATERMARK FOR row_time AS SOURCE_WATERMARK()\n"
                + ") WITH (\n"
                + "  'connector' = 'before_supports_reading_metadata_user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._03.source_sink.abilities.source.before.Before_Abilities_SourceFunction'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    window_end bigint,\n"
                + "    window_start timestamp(3),\n"
                + "    count_distinct_id BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "SELECT UNIX_TIMESTAMP(CAST(window_end AS STRING)) * 1000 as window_end, \n"
                + "      window_start, \n"
                + "      count(distinct user_id) as count_distinct_id\n"
                + "FROM TABLE(CUMULATE(\n"
                + "         TABLE source_table\n"
                + "         , DESCRIPTOR(row_time)\n"
                + "         , INTERVAL '10' SECOND\n"
                + "         , INTERVAL '1' DAY))\n"
                + "GROUP BY window_start, \n"
                + "        window_end";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
