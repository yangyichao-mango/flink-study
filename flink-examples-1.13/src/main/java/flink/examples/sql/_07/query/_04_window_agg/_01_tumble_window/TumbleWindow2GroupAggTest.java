package flink.examples.sql._07.query._04_window_agg._01_tumble_window;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class TumbleWindow2GroupAggTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--enable.hive.module.v2", "false"});

        String sql = "-- 数据源表\n"
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
                + "  'rows-per-second' = '10',\n"
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
                + "    pv BIGINT,\n"
                + "    sum_price BIGINT,\n"
                + "    max_price BIGINT,\n"
                + "    min_price BIGINT,\n"
                + "    uv BIGINT,\n"
                + "    window_start bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "-- 数据处理逻辑\n"
                + "insert into sink_table\n"
                + "select dim,\n"
                + "       sum(bucket_pv) as pv,\n"
                + "       sum(bucket_sum_price) as sum_price,\n"
                + "       max(bucket_max_price) as max_price,\n"
                + "       min(bucket_min_price) as min_price,\n"
                + "       sum(bucket_uv) as uv,\n"
                + "       max(window_start) as window_start\n"
                + "from (\n"
                + "     select dim,\n"
                + "            count(*) as bucket_pv,\n"
                + "            sum(price) as bucket_sum_price,\n"
                + "            max(price) as bucket_max_price,\n"
                + "            min(price) as bucket_min_price,\n"
                + "            -- 计算 uv 数\n"
                + "            count(distinct user_id) as bucket_uv,\n"
                + "            cast((UNIX_TIMESTAMP(CAST(row_time AS STRING))) / 60 as bigint) as window_start\n"
                + "     from source_table\n"
                + "     group by\n"
                + "            -- 按照用户 id 进行分桶，防止数据倾斜\n"
                + "            mod(user_id, 1024),\n"
                + "            dim,\n"
                + "            cast((UNIX_TIMESTAMP(CAST(row_time AS STRING))) / 60 as bigint)\n"
                + ")\n"
                + "group by dim,\n"
                + "         window_start";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.2 WINDOW TVF TUMBLE WINDOW 案例");

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }
}