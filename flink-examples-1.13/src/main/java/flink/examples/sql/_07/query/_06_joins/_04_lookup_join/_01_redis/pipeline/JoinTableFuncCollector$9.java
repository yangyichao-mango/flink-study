//
//import java.util.List;
//
//public class JoinTableFuncCollector$9 extends org.apache.flink.table.runtime.collector.TableFunctionCollector {
//
//    org.apache.flink.table.data.GenericRowData out = new org.apache.flink.table.data.GenericRowData(2);
//    org.apache.flink.table.data.utils.JoinedRowData joinedRow$7 = new org.apache.flink.table.data.utils.JoinedRowData();
//
//    public JoinTableFuncCollector$9(Object[] references) throws Exception {
//
//    }
//
//    @Override
//    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
//
//    }
//
//    @Override
//    public void collect(Object record) throws Exception {
//        List<org.apache.flink.table.data.RowData> in1 = (List<org.apache.flink.table.data.RowData>) getInput();
//        List<org.apache.flink.table.data.RowData> in2 = (List<org.apache.flink.table.data.RowData>) record;
//
//        for (int i = 0; i < in1.size(); i++) {
//
//            org.apache.flink.table.data.binary.BinaryStringData field$5;
//            boolean isNull$5;
//            long field$6;
//            boolean isNull$6;
//            isNull$6 = in2.get(i).isNullAt(1);
//            field$6 = -1L;
//            if (!isNull$6) {
//                field$6 = in2.get(i).getLong(1);
//            }
//            isNull$5 = in2.get(i).isNullAt(0);
//            field$5 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//            if (!isNull$5) {
//                field$5 = ((org.apache.flink.table.data.binary.BinaryStringData) in2.get(i).getString(0));
//            }
//
//
//
//
//
//
//            if (isNull$5) {
//                out.setField(0, null);
//            } else {
//                out.setField(0, field$5);
//            }
//
//
//
//            if (isNull$6) {
//                out.setField(1, null);
//            } else {
//                out.setField(1, field$6);
//            }
//
//
//            joinedRow$7.replace(in1.get(i), out);
//            joinedRow$7.setRowKind(in1.get(i).getRowKind());
//            outputResult(joinedRow$7);
//
//        }
//    }
//
//    @Override
//    public void close() throws Exception {
//
//    }
//}
//