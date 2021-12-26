package flink.examples.sql._07.query._04_window_agg._02_cumulate_window;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class CumulateWindowGroupingSetsBigintTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE TABLE source_table (\n"
                + "    age BIGINT,\n"
                + "    sex STRING,\n"
                + "    user_id BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.age.min' = '1',\n"
                + "  'fields.age.max' = '10',\n"
                + "  'fields.sex.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    age STRING,\n"
                + "    sex STRING,\n"
                + "    uv BIGINT,\n"
                + "    window_end bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "select age,\n"
                + "       sex,\n"
                + "       sum(bucket_uv) as uv,\n"
                + "       max(window_end) as window_end\n"
                + "from (\n"
                + "     SELECT UNIX_TIMESTAMP(CAST(window_end AS STRING)) * 1000 as window_end, \n"
                + "            window_start, \n"
                + "            if (age is null, 'ALL', cast(age as string)) as age,\n"
                + "            if (sex is null, 'ALL', sex) as sex,\n"
                + "            count(distinct user_id) as bucket_uv\n"
                + "     FROM TABLE(CUMULATE(\n"
                + "               TABLE source_table\n"
                + "               , DESCRIPTOR(row_time)\n"
                + "               , INTERVAL '5' SECOND\n"
                + "               , INTERVAL '1' DAY))\n"
                + "     GROUP BY window_start, \n"
                + "              window_end,\n"
                + "              GROUPING SETS ((), (age), (sex), (age, sex)),\n"
                + "              mod(user_id, 1024)\n"
                + ")\n"
                + "group by age,\n"
                + "         sex,\n"
                + "         window_end;";

        for (String innerSql : sql.split(";")) {
            flinkEnv.streamTEnv().executeSql(innerSql);
        }
    }

}
