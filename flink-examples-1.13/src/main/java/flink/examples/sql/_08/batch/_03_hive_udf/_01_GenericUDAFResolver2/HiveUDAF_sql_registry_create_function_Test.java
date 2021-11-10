package flink.examples.sql._08.batch._03_hive_udf._01_GenericUDAFResolver2;

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
public class HiveUDAF_sql_registry_create_function_Test {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        FlinkEnv flinkEnv = FlinkEnvUtils.getBatchTableEnv(args);

        // TODO sql 执行创建 hive udaf 可以正常执行，create function 执行完成之后就会被注册到 hive catalog 中

        String sql2 = "CREATE FUNCTION test_hive_udaf as 'flink.examples.sql._08.batch._03_hive_udf._01_GenericUDAFResolver2.TestHiveUDAF'";

        String sql3 = "select default.test_hive_udaf(user_id, '20210920')\n"
                + "         , count(1) as part_pv\n"
                + "         , max(order_amount) as part_max\n"
                + "         , min(order_amount) as part_min\n"
                + "    from hive_table\n"
                + "    where p_date between '20210920' and '20210920'\n"
                + "    group by 0";

        flinkEnv.batchTEnv().executeSql(sql2);
        flinkEnv.batchTEnv().executeSql(sql3)
                .print();
    }

}
