package flink.examples.sql._07.query._08_datastream_trans;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlertExample {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        String createTableSql = "CREATE TABLE source_table (\n"
                + "    id BIGINT,\n"
                + "    money BIGINT,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp_LTZ(3)),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.id.min' = '1',\n"
                + "  'fields.id.max' = '100000',\n"
                + "  'fields.money.min' = '1',\n"
                + "  'fields.money.max' = '100000'\n"
                + ")\n";

        String querySql = "SELECT UNIX_TIMESTAMP(CAST(window_end AS STRING)) * 1000 as window_end, \n"
                + "      window_start, \n"
                + "      sum(money) as sum_money,\n"
                + "      count(distinct id) as count_distinct_id\n"
                + "FROM TABLE(CUMULATE(\n"
                + "         TABLE source_table\n"
                + "         , DESCRIPTOR(row_time)\n"
                + "         , INTERVAL '5' SECOND\n"
                + "         , INTERVAL '1' DAY))\n"
                + "GROUP BY window_start, \n"
                + "        window_end";

        flinkEnv.streamTEnv().executeSql(createTableSql);

        Table resultTable = flinkEnv.streamTEnv().sqlQuery(querySql);

        flinkEnv.streamTEnv()
                .toDataStream(resultTable, Row.class)
                .flatMap(new FlatMapFunction<Row, Object>() {
                    @Override
                    public void flatMap(Row value, Collector<Object> out) throws Exception {
                        long l = Long.parseLong(String.valueOf(value.getField("sum_money")));

                        if (l > 10000L) {
                            log.info("报警，超过 1w");
                        }
                    }
                });

        flinkEnv.env().execute();
    }

}
