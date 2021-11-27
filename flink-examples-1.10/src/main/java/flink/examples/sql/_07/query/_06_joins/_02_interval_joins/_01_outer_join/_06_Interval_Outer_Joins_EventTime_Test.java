package flink.examples.sql._07.query._06_joins._02_interval_joins._01_outer_join;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


public class _06_Interval_Outer_Joins_EventTime_Test {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        env.setRestartStrategy(RestartStrategies.failureRateRestart(6, org.apache.flink.api.common.time.Time
                .of(10L, TimeUnit.MINUTES), org.apache.flink.api.common.time.Time.of(5L, TimeUnit.SECONDS)));
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setParallelism(10);

        // ck 设置
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        env.enableCheckpointing(30 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3L);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode().build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        tEnv.getConfig().getConfiguration().setString("pipeline.name", "1.10.1 Interval Join 事件时间案例");

        DataStream<Row> sourceTable = env.addSource(new UserDefinedSource1())
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.minutes(0L)) {
                    @Override
                    public long extractTimestamp(Row row) {
                        return (long) row.getField(2);
                    }
                });

        tEnv.createTemporaryView("source_table", sourceTable, "user_id, name, timestamp, rowtime.rowtime");

        DataStream<Row> dimTable = env.addSource(new UserDefinedSource2())
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.minutes(0L)) {
                    @Override
                    public long extractTimestamp(Row row) {
                        return (long) row.getField(2);
                    }
                });

        tEnv.createTemporaryView("dim_table", dimTable, "user_id, platform, timestamp, rowtime.rowtime");

        String sql = "SELECT\n"
                + "    s.user_id as user_id,\n"
                + "    s.name as name,\n"
                + "    d.platform as platform\n"
                + "FROM source_table as s\n"
                + "FULL JOIN dim_table as d ON s.user_id = d.user_id\n"
                + "AND s.rowtime BETWEEN d.rowtime AND d.rowtime + INTERVAL '30' SECOND";

        /**
         * join 算子：{@link org.apache.flink.table.runtime.operators.join.KeyedCoProcessOperatorWithWatermarkDelay}
         *                 -> {@link org.apache.flink.table.runtime.operators.join.RowTimeBoundedStreamJoin}
          */

        Table result = tEnv.sqlQuery(sql);

        tEnv.toAppendStream(result, Row.class)
                .print();

        env.execute("1.10.1 Interval Full Join 事件时间案例");

    }

    private static class UserDefinedSource1 implements SourceFunction<Row>, ResultTypeQueryable<Row> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<Row> sourceContext) throws Exception {

            int i = 0;

            while (!this.isCancel) {

                Row row = new Row(3);

                row.setField(0, i);

                row.setField(1, "name");

                long timestamp = System.currentTimeMillis();

                row.setField(2, timestamp);

                sourceContext.collect(row);

                Thread.sleep(1000L);
                i++;
            }

        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }

        @Override
        public TypeInformation<Row> getProducedType() {
            return new RowTypeInfo(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO);
        }
    }

    private static class UserDefinedSource2 implements SourceFunction<Row>, ResultTypeQueryable<Row> {

        private volatile boolean isCancel;

        @Override
        public void run(SourceContext<Row> sourceContext) throws Exception {

            int i = 10;

            while (!this.isCancel) {

                Row row = new Row(3);

                row.setField(0, i);

                row.setField(1, "platform");

                long timestamp = System.currentTimeMillis();

                row.setField(2, timestamp);

                sourceContext.collect(row);

                Thread.sleep(1000L);
                i++;
            }

        }

        @Override
        public void cancel() {
            this.isCancel = true;
        }

        @Override
        public TypeInformation<Row> getProducedType() {
            return new RowTypeInfo(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO);
        }
    }

}
