//package flink.examples.sql._07.query._06_joins._04_lookup_join._01_redis.pipeline;
//
//
//import java.util.LinkedList;
//import java.util.List;
//
//public class PipelineLookupFunction$4
//        extends org.apache.flink.api.common.functions.RichFlatMapFunction {
//
//    private transient flink.examples.sql._03.source_sink.table.redis.v2.source.RedisRowDataLookupFunction
//            function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc;
//    private TableFunctionResultConverterCollector$2 resultConverterCollector$3 = null;
//
//    public PipelineLookupFunction$4(Object[] references) throws Exception {
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc =
//                (((flink.examples.sql._03.source_sink.table.redis.v2.source.RedisRowDataLookupFunction) references[0]));
//    }
//
//
//    @Override
//    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc
//                .open(new org.apache.flink.table.functions.FunctionContext(getRuntimeContext()));
//
//
//        resultConverterCollector$3 = new TableFunctionResultConverterCollector$2();
//        resultConverterCollector$3.setRuntimeContext(getRuntimeContext());
//        resultConverterCollector$3.open(new org.apache.flink.configuration.Configuration());
//
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc
//                .setCollector(resultConverterCollector$3);
//
//    }
//
//    @Override
//    public void flatMap(Object _in1, org.apache.flink.util.Collector c) throws Exception {
//        // 改动第一处
//        List<org.apache.flink.table.data.RowData> in1 = (List<org.apache.flink.table.data.RowData>) _in1;
//
//        List<org.apache.flink.table.data.binary.BinaryStringData> list = new LinkedList<>();
//
//        for (int i = 0; i < in1.size(); i++) {
//
//            org.apache.flink.table.data.binary.BinaryStringData field$0;
//            boolean isNull$0;
//            isNull$0 = in1.get(i).isNullAt(2);
//            field$0 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//            if (!isNull$0) {
//                field$0 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.get(i).getString(2));
//            }
//
//            list.add(field$0);
//        }
//
//        resultConverterCollector$3.setCollector(c);
//
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc
//                .eval(((List<org.apache.flink.table.data.binary.BinaryStringData>) list));
//
//
//    }
//
//    @Override
//    public void close() throws Exception {
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc
//                .close();
//
//    }
//
//
//    public class TableFunctionResultConverterCollector$2
//            extends org.apache.flink.table.runtime.collector.WrappingCollector {
//
//
//        public TableFunctionResultConverterCollector$2() throws Exception {
//
//        }
//
//        @Override
//        public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
//
//        }
//
//        @Override
//        public void collect(Object record) throws Exception {
//            List<org.apache.flink.table.data.RowData> externalResult$1 = (List<org.apache.flink.table.data.RowData>) record;
//
//
//            if (externalResult$1 != null) {
//                outputResult(externalResult$1);
//            }
//
//        }
//
//        @Override
//        public void close() {
//            try {
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//}