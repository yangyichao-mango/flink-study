package flink.examples.sql._07.query._04_window_agg._02_cumulate_window;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class TumbleWindowEarlyFireTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.getStreamTableEnvironment().getConfig().getConfiguration().setString("table.exec.emit.early-fire.enabled", "true");
        flinkEnv.getStreamTableEnvironment().getConfig().getConfiguration().setString("table.exec.emit.early-fire.delay", "60 s");

        String sql = "CREATE TABLE source_table (\n"
                + "    dim BIGINT,\n"
                + "    user_id BIGINT,\n"
                + "    price BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.dim.min' = '1',\n"
                + "  'fields.dim.max' = '2',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    dim BIGINT,\n"
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
                + "            count(distinct user_id) as bucket_uv,\n"
                + "            UNIX_TIMESTAMP(CAST(tumble_start(row_time, interval '1' DAY) AS STRING)) * 1000 as window_start\n"
                + "     from source_table\n"
                + "     group by\n"
                + "            mod(user_id, 1024),\n"
                + "            dim,\n"
                + "            tumble(row_time, interval '1' DAY)\n"
                + ")\n"
                + "group by dim,\n"
                + "         window_start";

        flinkEnv.getStreamTableEnvironment().getConfig().getConfiguration().setString("pipeline.name", "1.13.2 WINDOW TVF TUMBLE WINDOW EARLY FIRE 案例");

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.getStreamTableEnvironment()::executeSql);



        /**
         * 两阶段聚合
         * 本地 agg：{@link org.apache.flink.table.runtime.operators.aggregate.window.LocalSlicingWindowAggOperator}
         *                   -> {@link org.apache.flink.table.runtime.operators.aggregate.window.combines.LocalAggCombiner}
         *
         * key agg；{@link org.apache.flink.table.runtime.operators.window.slicing.SlicingWindowOperator}
         *    -> {@link org.apache.flink.table.runtime.operators.aggregate.window.processors.SliceUnsharedWindowAggProcessor}
         *                   -> {@link org.apache.flink.table.runtime.operators.aggregate.window.combines.GlobalAggCombiner}
         */
    }

}
