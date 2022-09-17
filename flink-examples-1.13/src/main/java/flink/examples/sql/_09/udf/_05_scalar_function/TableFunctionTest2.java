package flink.examples.sql._09.udf._05_scalar_function;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class TableFunctionTest2 {


    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().createFunction("explode_udtf_v2", ExplodeUDTFV2.class);

        String sql = "CREATE TABLE Orders (\n"
                + "    order_id BIGINT NOT NULL,\n"
                + "    name STRING,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10',\n"
                + "  'fields.name.length' = '1',\n"
                + "  'fields.order_id.min' = '1',\n"
                + "  'fields.order_id.max' = '10'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE target_table (\n"
                + "    order_id BIGINT NOT NULL,\n"
                + "    name STRING,\n"
                + "    row_time timestamp(3),\n"
                + "    i STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO target_table\n"
                + "SELECT order_id, name, row_time,  name_explode[2] as i\n"
                + "FROM Orders \n"
                + "LEFT JOIN lateral TABLE(explode_udtf_v2(name)) AS t(name_explode) ON TRUE\n";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
