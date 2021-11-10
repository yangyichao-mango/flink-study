package flink.examples.sql._08.batch._03_hive_udf._02_GenericUDTF;

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
public class HiveUDTF_sql_registry_create_function_Test {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        FlinkEnv flinkEnv = FlinkEnvUtils.getBatchTableEnv(args);

//        String sql = "drop function default.test_hive_udtf";

        // TODO sql 执行正常，create function 使用的是 hive catalog 没有任何问题
        String sql2 = "CREATE FUNCTION test_hive_udtf as 'flink.examples.sql._08.batch._03_hive_udf._02_GenericUDTF.TestHiveUDTF'";

        String sql3 = "select default.test_hive_udtf(user_id)\n"
                + "    from hive_table\n"
                + "    where p_date between '20210920' and '20210920'\n";

//        flinkEnv.batchTEnv().executeSql(sql);
        flinkEnv.batchTEnv().executeSql(sql2);
        flinkEnv.batchTEnv().executeSql(sql3)
                .print();
    }

}
