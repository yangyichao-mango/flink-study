package flink.examples.sql._02.timezone;

import java.util.Arrays;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import flink.examples.sql._01.countdistincterror.udf.Mod_UDF;
import flink.examples.sql._01.countdistincterror.udf.StatusMapper_UDF;


public class TimeZoneTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("table.local-time-zone", "GMT+08:00");

        DataStream<Tuple3<String, Long, Long>> tuple3DataStream =
                flinkEnv.env().fromCollection(Arrays.asList(
                        Tuple3.of("2", 1L, 1627254000000L), // 北京时间：2021-07-26 07:00:00
                        Tuple3.of("2", 1L, 1627218000000L + 5000L),
                        Tuple3.of("2", 101L, 1627218000000L + 6000L),
                        Tuple3.of("2", 201L, 1627218000000L + 7000L),
                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
                        Tuple3.of("2", 301L, 1627218000000L + 86400000 + 7000L)))
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<Tuple3<String, Long, Long>>(Time.seconds(0L)) {
                            @Override
                            public long extractTimestamp(Tuple3<String, Long, Long> element) {
                                return element.f2;
                            }
                        });

        flinkEnv.streamTEnv().registerFunction("mod", new Mod_UDF());

        flinkEnv.streamTEnv().registerFunction("status_mapper", new StatusMapper_UDF());

        flinkEnv.streamTEnv().createTemporaryView("source_db.source_table", tuple3DataStream,
                "status, id, timestamp, rowtime.rowtime");

        String sql = "SELECT\n"
                + "  count(1),\n"
                + "  cast(tumble_start(rowtime, INTERVAL '1' DAY) as string)\n"
                + "FROM\n"
                + "  source_db.source_table\n"
                + "GROUP BY\n"
                + "  tumble(rowtime, INTERVAL '1' DAY)";

        Table result = flinkEnv.streamTEnv().sqlQuery(sql);

        flinkEnv.streamTEnv().toAppendStream(result, Row.class).print();

        flinkEnv.env().execute();
    }
}
