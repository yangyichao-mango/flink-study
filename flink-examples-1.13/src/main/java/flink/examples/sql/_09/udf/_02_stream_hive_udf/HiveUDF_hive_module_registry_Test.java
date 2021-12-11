package flink.examples.sql._09.udf._02_stream_hive_udf;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class HiveUDF_hive_module_registry_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

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
                + "       test_hive_udf(params) as log_id\n"
                + "from source_table\n";

        flinkEnv.hiveModuleV2()
                .registryHiveUDF(
                        "test_hive_udf"
                        , TestGenericUDF.class.getName());

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "Hive UDF 测试案例");

        for (String innerSql : sql.split(";")) {

            flinkEnv.streamTEnv().executeSql(innerSql);
        }

    }

}
