//
//import java.util.List;
//
//public class JoinTableFuncCollector$9 extends org.apache.flink.table.runtime.collector.TableFunctionCollector {
//
//    org.apache.flink.table.data.GenericRowData out = new org.apache.flink.table.data.GenericRowData(3);
//    org.apache.flink.table.data.utils.JoinedRowData joinedRow$8 = new org.apache.flink.table.data.utils.JoinedRowData();
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
//        List<org.apache.flink.table.data.RowData> l = (List<org.apache.flink.table.data.RowData>) getInput();
//        List<org.apache.flink.table.data.RowData> r = (List<org.apache.flink.table.data.RowData>) record;
//        for (int i = 0; i < l.size(); i++) {
//            org.apache.flink.table.data.RowData in1 = (org.apache.flink.table.data.RowData) l.get(i);
//            org.apache.flink.table.data.RowData in2 = (org.apache.flink.table.data.RowData) r.get(i);
//            org.apache.flink.table.data.binary.BinaryStringData field$5;
//            boolean isNull$5;
//            org.apache.flink.table.data.binary.BinaryStringData field$6;
//            boolean isNull$6;
//            org.apache.flink.table.data.binary.BinaryStringData field$7;
//            boolean isNull$7;
//            isNull$7 = in2.isNullAt(2);
//            field$7 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//            if (!isNull$7) {
//                field$7 = ((org.apache.flink.table.data.binary.BinaryStringData) in2.getString(2));
//            }
//            isNull$6 = in2.isNullAt(1);
//            field$6 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//            if (!isNull$6) {
//                field$6 = ((org.apache.flink.table.data.binary.BinaryStringData) in2.getString(1));
//            }
//            isNull$5 = in2.isNullAt(0);
//            field$5 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
//            if (!isNull$5) {
//                field$5 = ((org.apache.flink.table.data.binary.BinaryStringData) in2.getString(0));
//            }
//            if (isNull$5) {
//                out.setField(0, null);
//            } else {
//                out.setField(0, field$5);
//            }
//            if (isNull$6) {
//                out.setField(1, null);
//            } else {
//                out.setField(1, field$6);
//            }
//            if (isNull$7) {
//                out.setField(2, null);
//            } else {
//                out.setField(2, field$7);
//            }
//            joinedRow$8.replace(in1, out);
//            joinedRow$8.setRowKind(in1.getRowKind());
//            outputResult(joinedRow$8);
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