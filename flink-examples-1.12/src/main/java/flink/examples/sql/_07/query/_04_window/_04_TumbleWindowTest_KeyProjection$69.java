package flink.examples.sql._07.query._04_window;


public final class _04_TumbleWindowTest_KeyProjection$69 implements
        org.apache.flink.table.runtime.generated.Projection<org.apache.flink.table.data.RowData,
                org.apache.flink.table.data.binary.BinaryRowData> {

    org.apache.flink.table.data.binary.BinaryRowData out = new org.apache.flink.table.data.binary.BinaryRowData(2);
    org.apache.flink.table.data.writer.BinaryRowWriter outWriter =
            new org.apache.flink.table.data.writer.BinaryRowWriter(out);

    public _04_TumbleWindowTest_KeyProjection$69(Object[] references) throws Exception {

    }

    @Override
    public org.apache.flink.table.data.binary.BinaryRowData apply(org.apache.flink.table.data.RowData in1) {
        int field$70;
        boolean isNull$70;
        org.apache.flink.table.data.binary.BinaryStringData field$71;
        boolean isNull$71;
        outWriter.reset();
        isNull$70 = in1.isNullAt(0);
        field$70 = -1;
        if (!isNull$70) {
            field$70 = in1.getInt(0);
        }
        if (isNull$70) {
            outWriter.setNullAt(0);
        } else {
            outWriter.writeInt(0, field$70);
        }

        isNull$71 = in1.isNullAt(1);
        field$71 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$71) {
            field$71 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(1));
        }
        if (isNull$71) {
            outWriter.setNullAt(1);
        } else {
            outWriter.writeString(1, field$71);
        }

        outWriter.complete();


        return out;
    }
}