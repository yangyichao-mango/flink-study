package flink.examples.sql._07.query._15_exec_options;

import org.apache.flink.table.api.TableResult;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Idle_Timeout_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--enable.hive.module.v2", "false"});

        flinkEnv.env().setParallelism(1);

        flinkEnv.streamTEnv()
                .getConfig()
                .getConfiguration()
                .setString("table.exec.source.idle-timeout", "180 s");

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT COMMENT '用户 id',\n"
                + "    name STRING COMMENT '用户姓名',\n"
                + "    server_timestamp BIGINT COMMENT '用户访问时间戳',\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time\n"
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
                + "    server_timestamp BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select user_id,\n"
                + "       max(cast(server_timestamp as bigint)) as server_timestamp\n"
                + "from (\n"
                + "      SELECT\n"
                + "          user_id,\n"
                + "          name,\n"
                + "          server_timestamp,\n"
                + "          row_number() over(partition by user_id order by row_time desc) as rn\n"
                + "      FROM source_table\n"
                + ")\n"
                + "where rn = 1\n"
                + "group by user_id";

        for (String innerSql : sql.split(";")) {
            TableResult tableResult = flinkEnv.streamTEnv().executeSql(innerSql);

            tableResult.print();
        }
    }

}
