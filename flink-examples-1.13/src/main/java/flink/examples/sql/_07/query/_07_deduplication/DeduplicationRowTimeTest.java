package flink.examples.sql._07.query._07_deduplication;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class DeduplicationRowTimeTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[]{"--enable.hive.module.v2", "false"});

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT COMMENT '用户 id',\n"
                + "    level STRING COMMENT '用户等级',\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)) COMMENT '事件时间戳',\n"
                + "    WATERMARK FOR row_time AS row_time\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.level.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '1000000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    level STRING COMMENT '等级',\n"
                + "    uv BIGINT COMMENT '当前等级用户数',\n"
                + "    row_time timestamp(3) COMMENT '时间戳'\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select \n"
                + "    level\n"
                + "    , count(1) as uv\n"
                + "    , max(row_time) as row_time\n"
                + "from (\n"
                + "      SELECT\n"
                + "          user_id,\n"
                + "          level,\n"
                + "          row_time,\n"
                + "          row_number() over(partition by user_id order by row_time desc) as rn\n"
                + "      FROM source_table\n"
                + ")\n"
                + "where rn = 1\n"
                + "group by \n"
                + "    level";

        for (String innerSql : sql.split(";")) {
            flinkEnv.streamTEnv().executeSql(innerSql);
        }
    }

}
