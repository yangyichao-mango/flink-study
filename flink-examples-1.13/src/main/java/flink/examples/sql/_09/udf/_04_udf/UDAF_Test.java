package flink.examples.sql._09.udf._04_udf;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.ScalarFunction;

import flink.examples.FlinkEnvUtils;
import flink.examples.FlinkEnvUtils.FlinkEnv;
import flink.examples.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UDAF_Test {

    public static void main(String[] args) throws Exception {

        FlinkEnv flinkEnv = FlinkEnvUtils.getStreamTableEnv(args);

        String sql = "CREATE TEMPORARY FUNCTION test_hive_udf as 'flink.examples.sql._09.udf._04_udf.UDAF_Test$CollectList2';\n"
                + "CREATE TEMPORARY FUNCTION to_json_udf as 'flink.examples.sql._09.udf._04_udf.UDAF_Test$ToJson';\n"
                + "CREATE TABLE source_table (\n"
                + "    user_id BIGINT,\n"
                + "    `params` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'user_defined',\n"
                + "  'format' = 'json',\n"
                + "  'class.name' = 'flink.examples.sql._09.udf._02_stream_hive_udf.UserDefinedSource'\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE sink_table (\n"
                + "    user_id BIGINT,\n"
                + "    `log_id` STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'print'\n"
                + ");\n"
                + "\n"
                + "insert into sink_table\n"
                + "select user_id,\n"
//                + "       to_json_udf(test_hive_udf(params, cast(0 as int), cast('a' as string), cast(0 as bigint))) as log_id\n"
                + "       to_json_udf(test_hive_udf(params)) as log_id\n"
                + "from source_table\n"
                + "group by user_id\n";

        flinkEnv.streamTEnv().getConfig().getConfiguration().setString("pipeline.name", "UDAF 测试案例");

        for (String innerSql : sql.split(";")) {

            flinkEnv.streamTEnv().executeSql(innerSql);
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sentence implements Comparable<Sentence> {
        private String msgid;
        private Integer type;
        private String content;
        private Long ts;

        public int compareTo(Sentence s) {
            return s.equals(this) ? 1 : 0;
        }
    }

    public static class CollectList1 extends AggregateFunction<Sentence, Sentence> {

        @Override
        public Sentence getValue(Sentence strings) {
            return new Sentence();
        }

        @Override
        public Sentence createAccumulator() {
            return new Sentence();
        }

        public void accumulate(Sentence list, String msgid, Integer type, String content, Long ts) {

        }

        public void merge(Sentence list, Iterable<Sentence> it) {
        }

//        @Override
//        public TypeInformation<Sentence> getAccumulatorType() {
//
//            return Types.POJO(Sentence.class);
//
//        }
//
//        @Override
//        public TypeInformation<Sentence> getResultType() {
//
//            return Types.POJO(Sentence.class);
//        }
    }


    public static class CollectList extends AggregateFunction<List<Sentence>, List<Sentence>> {

        @Override
        public List<Sentence> getValue(List<Sentence> strings) {
            return strings;
        }

        @Override
        public List<Sentence> createAccumulator() {
            return new ArrayList<>();
        }

        public void accumulate(List<Sentence> list, String msgid, Integer type, String content, Long ts) {
            list.add(new Sentence(msgid, type, content, ts));
        }

        public void merge(List<Sentence> list, Iterable<List<Sentence>> it) {
            for (List<Sentence> list1 : it) {
                list.addAll(list1);
            }
        }

        @Override
        public TypeInformation<List<Sentence>> getAccumulatorType() {

            return TypeInformation.of(new TypeHint<List<Sentence>>() {
            });

        }

        @Override
        public TypeInformation<List<Sentence>> getResultType() {

            return TypeInformation.of(new TypeHint<List<Sentence>>() {
            });
        }
    }

    public static class ToJson extends ScalarFunction {
        public String eval(List<String> in) {
            return JacksonUtils.bean2Json(in);
        }
    }

    /**
     * Set Aggregate
     * @author Liu Yang
     * @date 2022/3/28 16:46
     */
    @FunctionHint(
            input = {@DataTypeHint("STRING")},
            output = @DataTypeHint("STRING")
    )
    public static class CollectList2 extends AggregateFunction<String, TreeSetAccumulator> {

        private String delimiter;

        public void accumulate(TreeSetAccumulator acc, String value){
            if (value == null) {
                return;
            }
            if (value instanceof Comparable) {
                acc.add((String) value);
            }
        }

        @Override
        public String getValue(TreeSetAccumulator accumulator) {
            return JacksonUtils.bean2Json(accumulator.getLocalValue());
        }

        @Override
        public TreeSetAccumulator createAccumulator() {
            return new TreeSetAccumulator<>();
        }
    }

    public static class TreeSetAccumulator<T extends Comparable<?>>
            implements Accumulator<T, TreeSet<T>> {
        private static final long serialVersionUID = 1L;

        // Tips: Construction of sorted collection with non-comparable elements
        private TreeSet<T> localValue = new TreeSet<>();

        @Override
        public void add(T value) {
            localValue.add(value);
        }

        @Override
        public TreeSet<T> getLocalValue() {
            return localValue;
        }

        @Override
        public void resetLocal() {
            localValue.clear();
        }

        @Override
        public void merge(Accumulator<T, TreeSet<T>> other) {
            localValue.addAll(other.getLocalValue());
        }

        @Override
        public Accumulator<T, TreeSet<T>> clone() {
            TreeSetAccumulator<T> newInstance = new TreeSetAccumulator<T>();
            newInstance.localValue = new TreeSet<>(localValue);
            return newInstance;
        }

        @Override
        public String toString() {
            return "TreeSet Accumulator " + localValue;
        }
    }




}
