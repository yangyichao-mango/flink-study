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
public class HiveUDTF_hive_module_registry_Test {

    public static void main(String[] args) throws IOException {

        FlinkEnv flinkEnv = FlinkEnvUtils.getBatchTableEnv(args);

        // TODO 可以成功执行没有任何问题
        flinkEnv.hiveModuleV2().registryHiveUDF("test_hive_udtf", TestHiveUDTF.class.getName());

        String sql3 = "select test_hive_udtf(user_id) as (a)\n"
                + "    from hive_table\n"
                + "    where p_date between '20210920' and '20210920'\n";

        flinkEnv.batchTEnv()
                .executeSql(sql3)
                .print();
    }

}
