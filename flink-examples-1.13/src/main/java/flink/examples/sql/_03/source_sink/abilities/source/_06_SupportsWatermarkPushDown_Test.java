package flink.examples.sql._03.source_sink.abilities.source;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class _06_SupportsWatermarkPushDown_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.5 用户自定义 SOURCE 案例");

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("state.backend", "rocksdb");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    flink_read_timestamp BIGINT METADATA VIRTUAL,\n"
                + "    time_ltz AS TO_TIMESTAMP_LTZ(flink_read_timestamp, 3),\n"
                + "    `name` STRING,\n"
                + "    WATERMARK FOR time_ltz AS time_ltz - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'supports_reading_metadata_user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._03.source_sink.abilities.source.Abilities_SourceFunction'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    flink_read_timestamp BIGINT,\n"
                + "    name STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    user_id,\n"
                + "    flink_read_timestamp,\n"
                + "    name\n"
                + "FROM source_table";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
