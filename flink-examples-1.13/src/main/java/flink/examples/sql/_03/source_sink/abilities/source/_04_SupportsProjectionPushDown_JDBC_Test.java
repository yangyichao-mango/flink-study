package flink.examples.sql._03.source_sink.abilities.source;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class _04_SupportsProjectionPushDown_JDBC_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.5 用户自定义 SOURCE 案例");

        String sql = "CREATE TABLE source_table_1 (\n"
                + "    id DECIMAL(20, 0),\n"
                + "    name STRING,\n"
                + "    owner STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'jdbc',\n"
                + "  'url' = 'jdbc:mysql://localhost:3306/user_profile',\n"
                + "  'username' = 'root',\n"
                + "  'password' = 'root123456',\n"
                + "  'table-name' = 'user_test'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table_2 (\n"
                + "    id DECIMAL(20, 0),\n"
                + "    name STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table_2\n"
                + "SELECT\n"
                + "    id\n"
                + "    , name\n"
                + "FROM source_table_1\n";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
