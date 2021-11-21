package flink.examples.sql._07.query._07_deduplication;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;


public class DeduplicationProcessingTimeTest {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        flinkEnv.env().setParallelism(1);

        String sql = "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    server_timestamp BIGINT,\n"
                + "    proctime AS PROCTIME()\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1',\n"
                + "  'fields.name.length' = '1',\n"
                + "  'fields.user_id.min' = '1',\n"
                + "  'fields.user_id.max' = '10',\n"
                + "  'fields.server_timestamp.min' = '1',\n"
                + "  'fields.server_timestamp.max' = '100000'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    name STRING,\n"
                + "    rn BIGINT\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "INSERT INTO sink_table\n"
                + "select user_id,\n"
                + "       name,\n"
                + "       rn\n"
                + "from (\n"
                + "      SELECT\n"
                + "          user_id,\n"
                + "          name,\n"
                + "          row_number() over(partition by user_id order by proctime) as rn\n"
                + "      FROM source_table\n"
                + ")\n"
                + "where rn = 1";

        /**
         * 算子 {@link org.apache.flink.streaming.api.operators.KeyedProcessOperator}
         *      -- {@link org.apache.flink.table.runtime.operators.deduplicate.ProcTimeDeduplicateKeepFirstRowFunction}
         */

        for (String innerSql : sql.split(";")) {
            flinkEnv.streamTEnv().executeSql(innerSql);
        }
    }

}
