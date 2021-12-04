//
//public class LookupFunction$4
//        extends org.apache.flink.api.common.functions.RichFlatMapFunction {
//
//    private transient flink.examples.sql._03.source_sink.table.redis.v2.source.RedisRowDataLookupFunction function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$48c9b464341243406b9f0b4a0ba51d1c;
//    private TableFunctionResultConverterCollector$2 resultConverterCollector$3 = null;
//
//    public LookupFunction$4(Object[] references) throws Exception {
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$48c9b464341243406b9f0b4a0ba51d1c =
//                (((flink.examples.sql._03.source_sink.table.redis.v2.source.RedisRowDataLookupFunction) references[0]));
//    }
//
//
//
//    @Override
//    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$48c9b464341243406b9f0b4a0ba51d1c.open(new org.apache.flink.table.functions.FunctionContext(getRuntimeContext()));
//
//
//        resultConverterCollector$3 = new TableFunctionResultConverterCollector$2();
//        resultConverterCollector$3.setRuntimeContext(getRuntimeContext());
//        resultConverterCollector$3.open(new org.apache.flink.configuration.Configuration());
//
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$48c9b464341243406b9f0b4a0ba51d1c.setCollector(resultConverterCollector$3);
//
//    }
//
//    @Override
//    public void flatMap(Object _in1, org.apache.flink.util.Collector c) throws Exception {
//        org.apache.flink.table.data.RowData in1 = (org.apache.flink.table.data.RowData) _in1;
//        org.apache.flink.table.data.binary.BinaryStringData field$0;
//        boolean isNull$0;
//        isNull$0 = in1.isNullAt(2);
//        field$0 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//        if (!isNull$0) {
//            field$0 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(2));
//        }
//        resultConverterCollector$3.setCollector(c);
//        if (isNull$0) {
//            // skip
//        } else {
//            function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$48c9b464341243406b9f0b4a0ba51d1c
//                    .eval((org.apache.flink.table.data.binary.BinaryStringData) field$0);
//        }
//
//
//    }
//
//    @Override
//    public void close() throws Exception {
//
//        function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$48c9b464341243406b9f0b4a0ba51d1c.close();
//
//    }
//
//
//    public class TableFunctionResultConverterCollector$2 extends org.apache.flink.table.runtime.collector.WrappingCollector {
//
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
//            org.apache.flink.table.data.RowData externalResult$1 = (org.apache.flink.table.data.RowData) record;
//
//
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
//