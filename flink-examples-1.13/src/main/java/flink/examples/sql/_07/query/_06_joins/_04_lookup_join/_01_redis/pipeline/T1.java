///* 1 */
///* 2 */
//
//import java.util.LinkedList;
//import java.util.List;
//
///* 3 */
///* 4 */      public class LookupFunction$4
//        /* 5 */          extends org.apache.flink.api.common.functions.RichFlatMapFunction {
//    /* 6 */
//    /* 7 */        private transient flink.examples.sql._03.source_sink.table.redis.v2.source.RedisRowDataLookupFunction function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc;
//    /* 8 */        private TableFunctionResultConverterCollector$2 resultConverterCollector$3 = null;
//    /* 9 */
//    /* 10 */        public LookupFunction$4(Object[] references) throws Exception {
//        /* 11 */          function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc = (((flink.examples.sql._03.source_sink.table.redis.v2.source.RedisRowDataLookupFunction) references[0]));
//        /* 12 */        }
//    /* 13 */
//    /* 14 */
//    /* 15 */
//    /* 16 */        @Override
//    /* 17 */        public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
//        /* 18 */
//        /* 19 */          function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc.open(new org.apache.flink.table.functions.FunctionContext(getRuntimeContext()));
//        /* 20 */
//        /* 21 */
//        /* 22 */          resultConverterCollector$3 = new TableFunctionResultConverterCollector$2();
//        /* 23 */          resultConverterCollector$3.setRuntimeContext(getRuntimeContext());
//        /* 24 */          resultConverterCollector$3.open(new org.apache.flink.configuration.Configuration());
//        /* 25 */
//        /* 26 */
//        /* 27 */          function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc.setCollector(resultConverterCollector$3);
//        /* 28 */
//        /* 29 */        }
//    /* 30 */
//    /* 31 */        @Override
//    /* 32 */        public void flatMap(Object _in1, org.apache.flink.util.Collector c) throws Exception {
//        /* 33 */          List<org.apache.flink.table.data.RowData> l = (List<org.apache.flink.table.data.RowData>) _in1;
//        /* 34 */          List<org.apache.flink.table.data.binary.BinaryStringData> list = new LinkedList<>();
//        /* 35 */          for (int i = 0; i < l.size(); i++) {
//            /* 36 */
//            /* 37 */              org.apache.flink.table.data.RowData in1 = (org.apache.flink.table.data.RowData) l.get(i);
//            /* 38 */
//            /* 39 */
//            /* 40 */              org.apache.flink.table.data.binary.BinaryStringData field$0;
//            /* 41 */              boolean isNull$0;
//            /* 42 */
//            /* 43 */              isNull$0 = in1.isNullAt(2);
//            /* 44 */              field$0 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//            /* 45 */              if (!isNull$0) {
//                /* 46 */                field$0 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(2));
//                /* 47 */              }
//            /* 48 */
//            /* 49 */              list.add(field$0);
//            /* 50 */          }
//        /* 51 */
//        /* 52 */
//        /* 53 */          resultConverterCollector$3.setCollector(c);
//        /* 54 */
//        /* 55 */
//        /* 56 */          function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc.eval((List<org.apache.flink.table.data.binary.BinaryStringData>) list);
//        /* 57 */
//        /* 58 */
//        /* 59 */        }
//    /* 60 */
//    /* 61 */        @Override
//    /* 62 */        public void close() throws Exception {
//        /* 63 */
//        /* 64 */          function_flink$examples$sql$_03$source_sink$table$redis$v2$source$RedisRowDataLookupFunction$9a02959d27765bacc6e3b2107f2d01bc.close();
//        /* 65 */
//        /* 66 */        }
//    /* 67 */
//    /* 68 */
//    /* 69 */              public class TableFunctionResultConverterCollector$2 extends org.apache.flink.table.runtime.collector.WrappingCollector {
//        /* 70 */
//        /* 71 */
//        /* 72 */
//        /* 73 */                public TableFunctionResultConverterCollector$2() throws Exception {
//            /* 74 */
//            /* 75 */                }
//        /* 76 */
//        /* 77 */                @Override
//        /* 78 */                public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
//            /* 79 */
//            /* 80 */                }
//        /* 81 */
//        /* 82 */                @Override
//        /* 83 */                public void collect(Object record) throws Exception {
//            /* 84 */                  List<org.apache.flink.table.data.RowData> externalResult$1 = (List<org.apache.flink.table.data.RowData>) record;
//            /* 85 */
//            /* 86 */
//            /* 87 */
//            /* 88 */
//            /* 89 */                  if (externalResult$1 != null) {
//                /* 90 */                    outputResult(externalResult$1);
//                /* 91 */                  }
//            /* 92 */
//            /* 93 */                }
//        /* 94 */
//        /* 95 */                @Override
//        /* 96 */                public void close() {
//            /* 97 */                  try {
//                /* 98 */
//                /* 99 */                  } catch (Exception e) {
//                /* 100 */                    throw new RuntimeException(e);
//                /* 101 */                  }
//            /* 102 */                }
//        /* 103 */              }
//    /* 104 */
//    /* 105 */      }
///* 106 */