package flink.examples.sql._12_data_type._01_interval;

import org.apache.flink.table.api.TableResult;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Timestamp3_Interval_To_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE TABLE sink_table (\n"
                + "    result_interval_year TIMESTAMP(3),\n"
                + "    result_interval_year_p TIMESTAMP(3),\n"
                + "    result_interval_year_p_to_month TIMESTAMP(3),\n"
                + "    result_interval_month TIMESTAMP(3),\n"
                + "    result_interval_day TIMESTAMP(3),\n"
                + "    result_interval_day_p1 TIMESTAMP(3),\n"
                + "    result_interval_day_p1_to_hour TIMESTAMP(3),\n"
                + "    result_interval_day_p1_to_minute TIMESTAMP(3),\n"
                + "    result_interval_day_p1_to_second_p2 TIMESTAMP(3),\n"
                + "    result_interval_hour TIMESTAMP(3),\n"
                + "    result_interval_hour_to_minute TIMESTAMP(3),\n"
                + "    result_interval_hour_to_second TIMESTAMP(3),\n"
                + "    result_interval_minute TIMESTAMP(3),\n"
                + "    result_interval_minute_to_second_p2 TIMESTAMP(3),\n"
                + "    result_interval_second TIMESTAMP(3),\n"
                + "    result_interval_second_p2 TIMESTAMP(3)\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "SELECT\n"
                + "    f1 + INTERVAL '10' YEAR as result_interval_year\n"
                + "    , f1 + INTERVAL '100' YEAR(3) as result_interval_year_p\n"
                + "    , f1 + INTERVAL '10-03' YEAR(3) TO MONTH as result_interval_year_p_to_month\n"
                + "    , f1 + INTERVAL '13' MONTH as result_interval_month\n"
                + "    , f1 + INTERVAL '10' DAY as result_interval_day\n"
                + "    , f1 + INTERVAL '100' DAY(3) as result_interval_day_p1\n"
                + "    , f1 + INTERVAL '10 03' DAY(3) TO HOUR as result_interval_day_p1_to_hour\n"
                + "    , f1 + INTERVAL '10 03:12' DAY(3) TO MINUTE as result_interval_day_p1_to_minute\n"
                + "    , f1 + INTERVAL '10 00:00:00.004' DAY TO SECOND(3) as result_interval_day_p1_to_second_p2\n"
                + "    , f1 + INTERVAL '10' HOUR as result_interval_hour\n"
                + "    , f1 + INTERVAL '10:03' HOUR TO MINUTE as result_interval_hour_to_minute\n"
                + "    , f1 + INTERVAL '00:00:00.004' HOUR TO SECOND(3) as result_interval_hour_to_second\n"
                + "    , f1 + INTERVAL '10' MINUTE as result_interval_minute\n"
                + "    , f1 + INTERVAL '05:05.006' MINUTE TO SECOND(3) as result_interval_minute_to_second_p2\n"
                + "    , f1 + INTERVAL '3' SECOND as result_interval_second\n"
                + "    , f1 + INTERVAL '300' SECOND(3) as result_interval_second_p2\n"
                + "FROM (SELECT CAST('1990-10-14 10:20:45.123' as TIMESTAMP(3)) as f1)"
                ;

        for (String innerSql : sql.split(";")) {
            TableResult tableResult = flinkEnv.streamTEnv().executeSql(innerSql);

            tableResult.print();
        }
    }

}
