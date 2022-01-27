package flink.examples.sql._12_data_type._03_raw;

import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.types.logical.RawType;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Raw_DataTypes_Test2 {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        RawType rawType = new RawType(String.class, StringSerializer.INSTANCE);

        String base64String = rawType.getSerializerString();

        flinkEnv.env().setParallelism(1);

        String sql = String.format("CREATE FUNCTION raw_scalar_func AS 'flink.examples.sql._12_data_type._03_raw.RawScalarFunction';"
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
                + "    result_row_1 RAW('java.lang.String', '%s')\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select\n"
                + "    raw_scalar_func(raw_scalar_func(user_id)) as result_row_1\n"
                + "from source_table", base64String);
                ;

        for (String innerSql : sql.split(";")) {
            TableResult tableResult = flinkEnv.streamTEnv().executeSql(innerSql);

            tableResult.print();
        }
    }

}
