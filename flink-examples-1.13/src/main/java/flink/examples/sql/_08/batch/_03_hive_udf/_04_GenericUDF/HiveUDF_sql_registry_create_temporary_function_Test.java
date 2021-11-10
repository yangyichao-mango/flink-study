package flink.examples.sql._08.batch._03_hive_udf._04_GenericUDF;

import java.io.IOException;

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
public class HiveUDF_sql_registry_create_temporary_function_Test {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        FlinkEnv flinkEnv = FlinkEnvUtils.getBatchTableEnv(args);

        // TODO sql 执行创建 hive udf 可以正常执行
        String sql2 = "CREATE TEMPORARY FUNCTION test_hive_udf as 'flink.examples.sql._08.batch._03_hive_udf._04_GenericUDF.TestGenericUDF'";

        String sql3 = "select test_hive_udf(user_id)\n"
                + "    from hive_table\n"
                + "    where p_date between '20210920' and '20210920'\n";

        flinkEnv.batchTEnv().executeSql(sql2);
        flinkEnv.batchTEnv().executeSql(sql3)
                .print();
    }

}
