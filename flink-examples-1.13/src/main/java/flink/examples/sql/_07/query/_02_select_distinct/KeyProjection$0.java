package flink.examples.sql._07.query._02_select_distinct;


public class KeyProjection$0 implements
        org.apache.flink.table.runtime.generated.Projection<org.apache.flink.table.data.RowData,
                org.apache.flink.table.data.binary.BinaryRowData> {

    org.apache.flink.table.data.binary.BinaryRowData out = new org.apache.flink.table.data.binary.BinaryRowData(1);
    org.apache.flink.table.data.writer.BinaryRowWriter outWriter =
            new org.apache.flink.table.data.writer.BinaryRowWriter(out);

    public KeyProjection$0(Object[] references) throws Exception {

    }

    @Override
    public org.apache.flink.table.data.binary.BinaryRowData apply(org.apache.flink.table.data.RowData in1) {
        org.apache.flink.table.data.binary.BinaryStringData field$1;
        boolean isNull$1;


        outWriter.reset();

        isNull$1 = in1.isNullAt(0);
        field$1 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$1) {
            field$1 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(0));
        }
        if (isNull$1) {
            outWriter.setNullAt(0);
        } else {
            outWriter.writeString(0, field$1);
        }

        outWriter.complete();


        return out;
    }
}
