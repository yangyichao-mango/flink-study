package flink.examples.sql._12_data_type._02_user_defined;

import org.apache.flink.table.api.TableResult;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class UserDefinedDataTypes_Test2 {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE FUNCTION user_scalar_func AS 'flink.examples.sql._12_data_type._02_user_defined.UserScalarFunction';"
                + "\n"
                + "CREATE TABLE source_table (\n"
                + "    user_id BIGINT NOT NULL COMMENT '用户 id'\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '10'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    result_row_1 ROW<age INT, name STRING, totalBalance DECIMAL(10, 2)>,\n"
                + "    result_row_2 STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select\n"
                + "    user_scalar_func(user_id) as result_row_1,\n"
                + "    user_scalar_func(user_scalar_func(user_id)) as result_row_2\n"
                + "from source_table";
                ;

        for (String innerSql : sql.split(";")) {
            TableResult tableResult = flinkEnv.streamTEnv().executeSql(innerSql);

            tableResult.print();
        }
    }

}
