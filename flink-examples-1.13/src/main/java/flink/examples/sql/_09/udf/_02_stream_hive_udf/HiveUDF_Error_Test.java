package flink.examples.sql._09.udf._02_stream_hive_udf;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


/**
 * hadoop 启动：/usr/local/Cellar/hadoop/3.2.1/sbin/start-all.sh
 * http://localhost:9870/
 * http://localhost:8088/cluster
 *
 * hive 启动：$HIVE_HOME/bin/hive --service metastore &
 * hive cli：$HIVE_HOME/bin/hive
 */
public class HiveUDF_Error_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--enable.hive.module.v2", "false"});

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    `params` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._09.udf._02_stream_hive_udf.UserDefinedSource'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    `log_id` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "select user_id,\n"
                + "       get_json_object(params, '$.log_id') as log_id\n"
                + "from source_table\n";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "Hive UDF 测试案例");

        for (String innerSql : sql.split(";")) {

            flinkEnv.streamTEnv().executeSql(innerSql);
        }

    }

}
