package flink.examples.sql._07.query._17_table_options;

import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class Dml_Syc_False_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(new String[] {"--enable.hive.module.v2", "false"});

        flinkEnv.env().setParallelism(1);

        flinkEnv.streamTEnv()
                .getConfig()
                .getConfiguration()
                .setString("table.dml-sync", "false");

        String sql = "CREATE TABLE source_table (\n"
                + "    id BIGINT,\n"
                + "    money BIGINT,\n"
                + "    row_time AS TO_TIMESTAMP_LTZ(cast(UNIX_TIMESTAMP() as bigint) * 1000, 3),\n"
                + "    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.id.min' = '1',\n"
                + "  'fields.id.max' = '100000',\n"
                + "  'fields.money.min' = '1',\n"
                + "  'fields.money.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table_1 (\n"
                + "    window_end timestamp(3),\n"
                + "    window_start timestamp(3),\n"
                + "    sum_money BIGINT,\n"
                + "    count_distinct_id BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table_2 (\n"
                + "    id bigint,\n"
                + "    window_end timestamp(3),\n"
                + "    window_start timestamp(3),\n"
                + "    sum_money BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table_1\n"
                + "SELECT window_end, \n"
                + "      window_start, \n"
                + "      sum(money) as sum_money,\n"
                + "      count(distinct id) as count_distinct_id\n"
                + "FROM TABLE(CUMULATE(\n"
                + "         TABLE source_table\n"
                + "         , DESCRIPTOR(row_time)\n"
                + "         , INTERVAL '5' SECOND\n"
                + "         , INTERVAL '1' DAY))\n"
                + "GROUP BY window_start, \n"
                + "        window_end\n"
                + ";\n"
                + "\n"
                + "insert into sink_table_2\n"
                + "SELECT id, \n"
                + "      window_end, \n"
                + "      window_start, \n"
                + "      sum(money) as sum_money\n"
                + "FROM TABLE(CUMULATE(\n"
                + "         TABLE source_table\n"
                + "         , DESCRIPTOR(row_time)\n"
                + "         , INTERVAL '5' SECOND\n"
                + "         , INTERVAL '1' DAY))\n"
                + "GROUP BY window_start, \n"
                + "        window_end, \n"
                + "        id\n"
                + ";";

        StatementSet statementSet = flinkEnv.streamTEnv().createStatementSet();

        for (String innerSql : sql.split(";")) {

            if (innerSql.contains("insert")) {
                statementSet.addInsertSql(innerSql);
            } else {
                TableResult tableResult = flinkEnv.streamTEnv()
                        .executeSql(innerSql);
            }
        }

        statementSet.execute();
    }

}
