package flink.examples.sql._07.query._04_window_agg._01_tumble_window;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class TumbleWindowTest3 {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--table.optimizer.agg-phase-strategy", "TWO_PHASE"});

        String sourceSql = "CREATE TABLE source_table (\n"
                + "    dim STRING,\n"
                + "    user_id BIGINT,\n"
                + "    price BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '10000',\n"
                + "  'fields.dim.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '100000',\n"
                + "  'fields.price.min' = '1',\n"
                + "  'fields.price.max' = '100000'\n"
                + ")";

        String sinkSql = "CREATE TABLE sink_table (\n"
                + "    dim STRING,\n"
                + "    pv BIGINT,\n"
                + "    sum_price BIGINT,\n"
                + "    max_price BIGINT,\n"
                + "    min_price BIGINT,\n"
                + "    uv BIGINT,\n"
                + "    window_start bigint\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ")";

        String selectWhereSql = "insert into sink_table\n"
                + "select dim,\n"
                + "\t   sum(bucket_pv) as pv,\n"
                + "\t   sum(bucket_sum_price) as sum_price,\n"
                + "\t   max(bucket_max_price) as max_price,\n"
                + "\t   min(bucket_min_price) as min_price,\n"
                + "\t   sum(bucket_uv) as uv,\n"
                + "\t   UNIX_TIMESTAMP(CAST(TUMBLE_START(rowtime, INTERVAL '5' MINUTE) AS STRING)) as window_start\n"
                + "from (\n"
                + "\tSELECT \n"
                + "\t\tdim,\n"
                + "\t\twindow_time as rowtime,\n"
                + "\t    count(*) as bucket_pv,\n"
                + "\t    sum(price) as bucket_sum_price,\n"
                + "\t    max(price) as bucket_max_price,\n"
                + "\t    min(price) as bucket_min_price,\n"
                + "\t    count(distinct user_id) as bucket_uv\n"
                + "\tFROM TABLE(TUMBLE(\n"
                + "\t\t\t\tTABLE source_table\n"
                + "\t\t\t\t, DESCRIPTOR(row_time)\n"
                + "\t\t\t\t, INTERVAL '60' SECOND))\n"
                + "\tGROUP BY \n"
                + "\t\twindow_time,window_start, window_end, \n"
                + "\t\tdim,\n"
                + "\t\tmod(user_id, 1024)\n"
                + ")\n"
                + "group by dim,\n"
                + "  \t\tTUMBLE(rowtime, INTERVAL '5' MINUTE)";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.5 WINDOW TVF TUMBLE WINDOW 案例");

        flinkEnv.streamTEnv().executeSql(sourceSql);
        flinkEnv.streamTEnv().executeSql(sinkSql);
        flinkEnv.streamTEnv().executeSql(selectWhereSql);

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
