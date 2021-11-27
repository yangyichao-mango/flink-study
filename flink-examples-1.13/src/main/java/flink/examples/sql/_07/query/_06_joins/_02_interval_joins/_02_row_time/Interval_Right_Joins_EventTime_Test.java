package flink.examples.sql._07.query._06_joins._02_interval_joins._02_row_time;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Interval_Right_Joins_EventTime_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.2 Interval Outer Join 事件时间案例");

        String sql = "CREATE TABLE show_log_table (\n"
                + "    log_id BIGINT,\n"
                + "    show_params STRING,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.show_params.length' = '1',\n"
                + "  'fields.log_id.min' = '1',\n"
                + "  'fields.log_id.max' = '10'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE click_log_table (\n"
                + "    log_id BIGINT,\n"
                + "    click_params STRING,\n"
                + "    row_time AS cast(CURRENT_TIMESTAMP as timestamp(3)),\n"
                + "    WATERMARK FOR row_time AS row_time\n"
                + ")\n"
                + "WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.click_params.length' = '1',\n"
                + "  'fields.log_id.min' = '1',\n"
                + "  'fields.log_id.max' = '10'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    s_id BIGINT,\n"
                + "    s_params STRING,\n"
                + "    c_id BIGINT,\n"
                + "    c_params STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    show_log_table.log_id as s_id,\n"
                + "    show_log_table.show_params as s_params,\n"
                + "    click_log_table.log_id as c_id,\n"
                + "    click_log_table.click_params as c_params\n"
                + "FROM show_log_table RIGHT JOIN click_log_table ON show_log_table.log_id = click_log_table.log_id\n"
                + "AND show_log_table.row_time BETWEEN click_log_table.row_time - INTERVAL '4' HOUR AND click_log_table.row_time;";

        /**
         * join 算子：{@link org.apache.flink.table.runtime.operators.join.KeyedCoProcessOperatorWithWatermarkDelay}
         *                 -> {@link org.apache.flink.table.runtime.operators.join.interval.RowTimeIntervalJoin}
         *                       -> {@link org.apache.flink.table.runtime.operators.join.interval.IntervalJoinFunction}
         */

        Arrays.stream(sql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
