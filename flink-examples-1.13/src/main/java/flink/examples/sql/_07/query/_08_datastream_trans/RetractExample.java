//package flink.examples.sql._07.query._08_datastream_trans;
//
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.table.api.Table;
//import org.apache.flink.types.Row;
//
//import flink.examples.FlinkEnvUtils;
//import flink.examples.FlinkEnvUtils.FlinkEnv;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class RetractExample {
//
//    public static void main(String[] args) throws Exception {
//
//        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);
//
//        String createTableSql = "CREATE TABLE source_table (\n"
//                + "    id BIGINT,\n"
//                + "    money BIGINT,\n"
//                + "    `time` as cast(CURRENT_TIMESTAMP as bigint) * 1000\n"
//                + ") WITH (\n"
//                + "  'connector' = 'datagen',\n"
//                + "  'rows-per-second' = '1',\n"
//                + "  'fields.id.min' = '1',\n"
//                + "  'fields.id.max' = '100000',\n"
//                + "  'fields.money.min' = '1',\n"
//                + "  'fields.money.max' = '100000'\n"
//                + ")\n";
//
//        String querySql = "SELECT max(`time`), \n"
//                + "      sum(money) as sum_money\n"
//                + "FROM source_table\n"
//                + "GROUP BY (`time` + 8 * 3600 * 1000) / (24 * 3600 * 1000)";
//
//        flinkEnv.streamTEnv().executeSql(createTableSql);
//
//        Table resultTable = flinkEnv.streamTEnv().sqlQuery(querySql);
//
//        DataStream<Tuple2<Boolean, Row>> d = flinkEnv.streamTEnv()
//                .toChangelogStream(resultTable, Row.class);
//
//        flinkEnv.streamTEnv().from
//
//        flinkEnv.env().execute();
//    }
//
//}
