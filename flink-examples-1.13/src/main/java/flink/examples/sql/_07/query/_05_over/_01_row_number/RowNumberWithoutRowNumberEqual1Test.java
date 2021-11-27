package flink.examples.sql._07.query._05_over._01_row_number;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class RowNumberWithoutRowNumberEqual1Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);


        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    server_timestamp BIGINT,\n"
                + "    proctime AS PROCTIME()\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10',\n"
                + "  'fields.name.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.server_timestamp.min' = '1',\n"
                + "  'fields.server_timestamp.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    rn BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "CREATE FUNCTION mod_udf as 'flink.examples.sql._07.query._05_over._01_row_number.Scalar_UDF';\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select mod_udf(user_id, 1024) as user_id,\n"
                + "       name,\n"
                + "       rn\n"
                + "from (\n"
                + "      SELECT\n"
                + "          user_id,\n"
                + "          name,\n"
                + "          row_number() over(partition by user_id order by proctime) as rn\n"
                + "      FROM source_table\n"
                + ")\n";

        /**
         * join 算子：{@link org.apache.flink.table.runtime.operators.join.stream.StreamingJoinOperator}
          */

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
