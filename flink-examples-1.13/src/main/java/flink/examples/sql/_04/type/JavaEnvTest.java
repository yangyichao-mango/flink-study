package flink.examples.sql._04.type;//package flink.examples.sql._04.type;
//
//
//import java.util.Arrays;
//
//import org.apache.flink.api.java.tuple.Tuple3;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
//import org.apache.flink.streaming.api.windowing.time.Time;
//import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
//import org.apache.flink.types.Row;
//
//public class JavaEnvTest {
//
//    public static void main(String[] args) throws Exception {
//
//
//        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
//        sEnv.setParallelism(1);
//
//        // create a TableEnvironment for streaming queries
//        StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(sEnv);
//
//        sTableEnv.registerFunction("table1", new TableFunc0());
//
//        DataStream<Tuple3<String, Long, Long>> tuple3DataStream =
//                sEnv.fromCollection(Arrays.asList(
//                        Tuple3.of("2", 1L, 1627254000000L),
//                        Tuple3.of("2", 1L, 1627218000000L + 5000L),
//                        Tuple3.of("2", 101L, 1627218000000L + 6000L),
//                        Tuple3.of("2", 201L, 1627218000000L + 7000L),
//                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
//                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
//                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
//                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
//                        Tuple3.of("2", 301L, 1627218000000L + 7000L),
//                        Tuple3.of("2", 301L, 1627218000000L + 86400000 + 7000L)))
//                        .assignTimestampsAndWatermarks(
//                                new BoundedOutOfOrdernessTimestampExtractor<Tuple3<String, Long, Long>>(Time.seconds(0L)) {
//                                    @Override
//                                    public long extractTimestamp(Tuple3<String, Long, Long> element) {
//                                        return element.f2;
//                                    }
//                                });
//
//        sTableEnv.createTemporaryView("source_db.source_table", tuple3DataStream,
//                "status, id, timestamp, rowtime.rowtime");
//
//        String sql = "select * \n"
//                + "from source_db.source_table as a\n"
//                + "LEFT JOIN LATERAL TABLE(table1(a.status)) AS DIM(status_new) ON TRUE";
//
//        sTableEnv.toAppendStream(sTableEnv.sqlQuery(sql), Row.class).print();
//
//        sEnv.execute();
//
//    }
//
//}
