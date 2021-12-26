package flink.examples.sql._03.source_sink;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class DataStreamSourceProcessingTimeTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        // 1. 分配 watermark
        DataStream<Row> r = env.addSource(new UserDefinedSource());

        // 2. 使用 proctime.proctime 的方式将 f2 字段指为处理时间时间戳
        Table sourceTable = tEnv.fromDataStream(r, "f0, f1, f2, proctime.proctime");

        tEnv.createTemporaryView("source_table", sourceTable);

        // 3. 在 tumble window 中使用 f2
        String tumbleWindowSql =
                "SELECT TUMBLE_START(proctime, INTERVAL '5' SECOND), COUNT(DISTINCT f0)\n"
                + "FROM source_table\n"
                + "GROUP BY TUMBLE(proctime, INTERVAL '5' SECOND)"
                ;

        Table resultTable = tEnv.sqlQuery(tumbleWindowSql);

        tEnv.toDataStream(resultTable, Row.class).print();

        env.execute();
    }


    private static class UserDefinedSource implements SourceFunction<Row>, ResultTypeQueryable<Row> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<Row> sourceContext) throws Exception {

            int i = 0;

            while (!this.isCancel) {

                sourceContext.collect(Row.of("a" + i, "b", System.currentTimeMillis()));

                Thread.sleep(10L);
                i++;
            }

        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }

        @Override
        public TypeInformation<Row> getProducedType() {
            return new RowTypeInfo(TypeInformation.of(String.class), TypeInformation.of(String.class),
                    TypeInformation.of(Long.class));
        }
    }

}
