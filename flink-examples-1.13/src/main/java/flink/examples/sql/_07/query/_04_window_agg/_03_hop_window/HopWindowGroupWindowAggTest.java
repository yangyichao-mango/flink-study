package flink.examples.sql._07.query._04_window_agg._03_hop_window;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class HopWindowGroupWindowAggTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        String sql = "-- 数据源表，用户购买行为记录表\n"
                + "CREATE TABLE source_table (\n"
                + "    -- 维度数据\n"
                + "    dim STRING,\n"
                + "    -- 用户 id\n"
                + "    user_id BIGINT,\n"
                + "    -- 用户\n"
                + "    price BIGINT,\n"
                + "    -- 事件时间戳\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    -- watermark 设置\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '5',\n"
                + "  'fields.dim.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "-- 数据汇表\n"
                + "CREATE TABLE sink_table (\n"
                + "    dim STRING,\n"
                + "    pv BIGINT, -- 购买商品数量\n"
                + "    window_start bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "-- 数据处理逻辑\n"
                + "insert into sink_table\n"
                + "select dim,\n"
                + "\t   sum(bucket_pv) as pv,\n"
                + "\t   window_start\n"
                + "from (\n"
                + "\t SELECT dim,\n"
                + "\t \t    UNIX_TIMESTAMP(CAST(session_start(row_time, interval '1' second) AS STRING)) * 1000 as "
                + "window_start, \n"
                + "\t        count(1) as bucket_pv\n"
                + "\t FROM source_table\n"
                + "\t GROUP BY dim\n"
                + "\t\t\t  , mod(user_id, 1024)\n"
                + "              , session(row_time, interval '1' second)\n"
                + ")\n"
                + "group by dim,\n"
                + "\t\t window_start";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
