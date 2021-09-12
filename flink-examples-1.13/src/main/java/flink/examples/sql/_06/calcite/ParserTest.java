package flink.examples.sql._06.calcite;

import java.util.Arrays;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import flink.examples.sql._01.countdistincterror.udf.Mod_UDF;
import flink.examples.sql._01.countdistincterror.udf.StatusMapper_UDF;


public class ParserTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(10);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useOldPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        DataStream<Tuple3<String, Long, Long>> tuple3DataStream =
                env.fromCollection(Arrays.asList(
                        Tuple3.of("2", 1L, 1627254000000L),
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

        tEnv.registerFunction("mod", new Mod_UDF());

        tEnv.registerFunction("status_mapper", new StatusMapper_UDF());

        tEnv.createTemporaryView("source_db.source_table", tuple3DataStream,
                "status, id, timestamp, rowtime.rowtime");

        String sql = "SELECT\n"
                + "  sum(part_pv) as pv,\n"
                + "  window_start\n"
                + "FROM (\n"
                + "\tSELECT\n"
                + "\t  count(1) as part_pv,\n"
                + "\t  cast(tumble_start(rowtime, INTERVAL '60' SECOND) as bigint) * 1000 as window_start\n"
                + "\tFROM\n"
                + "\t  source_db.source_table\n"
                + "\tGROUP BY\n"
                + "\t  tumble(rowtime, INTERVAL '60' SECOND)\n"
                + "\t  , mod(id, 1024)\n"
                + ")\n"
                + "GROUP BY\n"
                + "  window_start";

        Table result = tEnv.sqlQuery(sql);

        tEnv.toRetractStream(result, Row.class).print();

        env.execute();

    }

}
