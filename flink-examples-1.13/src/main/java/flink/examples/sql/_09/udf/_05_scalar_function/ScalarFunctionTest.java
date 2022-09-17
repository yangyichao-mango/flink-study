package flink.examples.sql._09.udf._05_scalar_function;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class ScalarFunctionTest {


    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().createFunction("set_string", SetStringUDF.class);
        flinkEnv.streamTEnv().createFunction("explode_udtf", ExplodeUDTF.class);
        flinkEnv.streamTEnv().createFunction("get_map_value", GetMapValue.class);

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
                + "    name_explode STRING,\n"
                + "    i STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO target_table\n"
                + "SELECT *, cast(get_map_value(name_explode, cast('a' as string)) as string) as i\n"
                + "FROM Orders\n"
                + "LEFT JOIN lateral TABLE(\n"
                + "        explode_udtf(\n"
                + "          set_string(name)\n"
                + "        )\n"
                + "      ) AS t(name_explode) ON TRUE";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
