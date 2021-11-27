package flink.examples.sql._07.query._06_joins._03_temporal_join._02_row_time;

import java.util.Arrays;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Temporal_Join_EventTime_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "1.13.2 Interval Join 处理时间案例");
        flinkEnv.env().setParallelism(1);

        String exampleSql = "CREATE TABLE show_log (\n"
                + "    log_id BIGINT,\n"
                + "    show_params STRING,\n"
                + "    proctime AS PROCTIME()\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.show_params.length' = '1',\n"
                + "  'fields.log_id.min' = '1',\n"
                + "  'fields.log_id.max' = '10'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE click_log (\n"
                + "    log_id BIGINT,\n"
                + "    click_params STRING,\n"
                + "    proctime AS PROCTIME()\n"
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
                + "    show_log.log_id as s_id,\n"
                + "    show_log.show_params as s_params,\n"
                + "    click_log.log_id as c_id,\n"
                + "    click_log.click_params as c_params\n"
                + "FROM show_log FULL JOIN click_log ON show_log.log_id = click_log.log_id\n"
                + "AND show_log.proctime BETWEEN click_log.proctime - INTERVAL '4' HOUR AND click_log.proctime;";

        /**
         * join 算子：{@link org.apache.flink.streaming.api.operators.co.KeyedCoProcessOperator}
         *                 -> {@link org.apache.flink.table.runtime.operators.join.interval.ProcTimeIntervalJoin}
         *                       -> {@link org.apache.flink.table.runtime.operators.join.interval.IntervalJoinFunction}
         */

        Arrays.stream(exampleSql.split(";"))
                .forEach(flinkEnv.streamTEnv()::executeSql);
    }

}
