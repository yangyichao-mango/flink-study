package flink.examples.sql._09.udf._01_hive_udf._01_GenericUDF;

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
public class HiveUDF_sql_registry_create_function_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        // TODO stream sql hive udf 创建不报错，执行使用报错 class cast exception
        String sql2 = "CREATE FUNCTION test_hive_udf as 'flink.examples.sql._08.batch._03_hive_udf._04_GenericUDF.TestGenericUDF'";

        String sql = "CREATE TABLE source_table (\n"
                + "    order_id STRING,\n"
                + "    price BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10',\n"
                + "  'fields.order_id.length' = '1',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '1000000'\n"
                + ");\n"
                + "\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    order_id STRING,\n"
                + "    count_result BIGINT,\n"
                + "    sum_result BIGINT,\n"
                + "    avg_result DOUBLE,\n"
                + "    min_result BIGINT,\n"
                + "    max_result BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "select test_hive_udf(order_id) as order_id,\n"
                + "       count(*) as count_result,\n"
                + "       sum(price) as sum_result,\n"
                + "       avg(price) as avg_result,\n"
                + "       min(price) as min_result,\n"
                + "       max(price) as max_result\n"
                + "from source_table\n"
                + "group by order_id";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "GROUP AGG 案例");

        flinkEnv.streamTEnv().executeSql(sql2);

        for (String innerSql : sql.split(";")) {

            flinkEnv.streamTEnv().executeSql(innerSql);
        }

    }

}
