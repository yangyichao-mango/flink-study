package flink.examples.sql._03.source_sink;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class CreateViewTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.5 用户自定义 SOURCE 案例");

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("state.backend", "rocksdb");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    `name` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._03.source_sink.table.user_defined.UserDefinedSource'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "CREATE VIEW query_view as\n"
                + "SELECT\n"
                + "    *\n"
                + "FROM source_table\n"
                + ";\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    *\n"
                + "FROM query_view;";

        // 临时 VIEW
        String TEMPORARY_VIEW_SQL = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    `name` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._03.source_sink.table.user_defined.UserDefinedSource'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "CREATE TEMPORARY VIEW query_view as\n"
                + "SELECT\n"
                + "    *\n"
                + "FROM source_table\n"
                + ";\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    *\n"
                + "FROM query_view;";

        // 临时 Table
        String TEMPORARY_TABLE_SQL = "CREATE TEMPORARY TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    `name` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._03.source_sink.table.user_defined.UserDefinedSource'\n"
                + ");\n"
                + "\n"
                + "CREATE TEMPORARY TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "CREATE TEMPORARY VIEW query_view as\n"
                + "SELECT\n"
                + "    *\n"
                + "FROM source_table\n"
                + ";\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    *\n"
                + "FROM query_view;";

        Arrays.stream(TEMPORARY_TABLE_SQL.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
