package flink.examples.sql._03.source_sink.abilities.source;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class _02_SupportsLimitPushDown_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.2 用户自定义 SOURCE 案例");

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("state.backend", "rocksdb");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    flink_read_timestamp BIGINT METADATA VIRTUAL,\n"
                + "    `name` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'supports_reading_metadata_user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._03.source_sink.abilities.source._01_SupportsReadingMetadata_SourceFunction'\n"
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
                + "    *\n"
                + "FROM source_table\n"
                + "LIMIT 100";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
