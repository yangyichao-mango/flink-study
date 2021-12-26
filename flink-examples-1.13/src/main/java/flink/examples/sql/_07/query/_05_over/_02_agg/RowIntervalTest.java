package flink.examples.sql._07.query._05_over._02_agg;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;

public class RowIntervalTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[]{"--enable.hive.module.v2", "false"});

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE TABLE source_table (\n"
                + "    order_id BIGINT,\n"
                + "    product BIGINT,\n"
                + "    amount BIGINT,\n"
                + "    order_time as cast(CURRENT_TIMESTAMP as TIMESTAMP(3)),\n"
                + "    WATERMARK FOR order_time AS order_time - INTERVAL '0.001' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.order_id.min' = '1',\n"
                + "  'fields.order_id.max' = '2',\n"
                + "  'fields.amount.min' = '1',\n"
                + "  'fields.amount.max' = '2',\n"
                + "  'fields.product.min' = '1',\n"
                + "  'fields.product.max' = '2'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    product BIGINT,\n"
                + "    order_time TIMESTAMP(3),\n"
                + "    amount BIGINT,\n"
                + "    one_hour_prod_amount_sum BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT product, order_time, amount,\n"
                + "  SUM(amount) OVER (\n"
                + "    PARTITION BY product\n"
                + "    ORDER BY order_time\n"
                + "    ROWS BETWEEN 5 PRECEDING AND CURRENT ROW\n"
                + "  ) AS one_hour_prod_amount_sum\n"
                + "FROM source_table";

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
