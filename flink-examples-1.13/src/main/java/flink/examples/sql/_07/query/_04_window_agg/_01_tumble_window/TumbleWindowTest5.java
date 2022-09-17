package flink.examples.sql._07.query._04_window_agg._01_tumble_window;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class TumbleWindowTest5 {

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

        flinkEnv.streamTEnv().executeSql(sourceSql);
        flinkEnv.streamTEnv().executeSql(sinkSql);

        String s1 = "\tSELECT \n"
                + "\t\tdim,\n"
                + "\t\tUNIX_TIMESTAMP(CAST(window_start AS STRING)) as rowtime,\n"
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
                + "\t\twindow_start,\n"
                + "\t\twindow_end,\n"
                + "\t\tdim,\n"
                + "\t\tmod(user_id, 1024)\n";

        DataStream<Row> r = flinkEnv.streamTEnv()
                .toRetractStream(flinkEnv.streamTEnv().sqlQuery(s1), Row.class)
                .flatMap(new FlatMapFunction<Tuple2<Boolean, Row>, Row>() {
                    @Override
                    public void flatMap(Tuple2<Boolean, Row> value, Collector<Row> out)
                            throws Exception {
                        out.collect(value.f1);
                    }
                })
                .returns(new RowTypeInfo(TypeInformation.of(String.class), TypeInformation.of(Long.class),
                        TypeInformation.of(Long.class), TypeInformation.of(Long.class), TypeInformation.of(Long.class),
                        TypeInformation.of(Long.class), TypeInformation.of(Long.class)));

        DataStream<Row> d = r
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.minutes(0)) {
                    @Override
                    public long extractTimestamp(Row element) {
                        return element.getFieldAs("f1");
                    }
                });

//        Table t = flinkEnv.streamTEnv().fromDataStream(d, "dim, rowtime, bucket_pv, bucket_sum_price, bucket_max_price, bucket_min_price, bucket_uv, rowtime.rowtime");

        flinkEnv.streamTEnv().createTemporaryView("tmp", d, "dim, bucket_pv, bucket_sum_price, bucket_max_price, bucket_min_price, bucket_uv, rowtime.rowtime");

        String selectWhereSql = "insert into sink_table\n"
                + "select dim,\n"
                + "\t   sum(bucket_pv) as pv,\n"
                + "\t   sum(bucket_sum_price) as sum_price,\n"
                + "\t   max(bucket_max_price) as max_price,\n"
                + "\t   min(bucket_min_price) as min_price,\n"
                + "\t   sum(bucket_uv) as uv,\n"
                + "\t   UNIX_TIMESTAMP(CAST(window_start AS STRING)) as window_start\n"
                + "from TABLE(\n"
                + "\t TUMBLE(\n"
                + "\t\tTABLE tmp\n"
                + "\t\t, DESCRIPTOR(rowtime)\n"
                + "\t\t, INTERVAL '60' SECOND)\n"
                + ")\n"
                + "group by dim,\n"
                + "\t\t window_start, window_end";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.5 WINDOW TVF TUMBLE WINDOW 案例");


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
