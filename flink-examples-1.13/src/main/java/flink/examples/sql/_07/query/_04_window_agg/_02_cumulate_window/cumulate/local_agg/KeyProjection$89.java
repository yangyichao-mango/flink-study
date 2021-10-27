package flink.examples.sql._07.query._04_window_agg._02_cumulate_window.cumulate.local_agg;


public class KeyProjection$89 implements
        org.apache.flink.table.runtime.generated.Projection<org.apache.flink.table.data.RowData,
                org.apache.flink.table.data.binary.BinaryRowData> {

    org.apache.flink.table.data.binary.BinaryRowData out = new org.apache.flink.table.data.binary.BinaryRowData(2);
    org.apache.flink.table.data.writer.BinaryRowWriter outWriter =
            new org.apache.flink.table.data.writer.BinaryRowWriter(out);

    public KeyProjection$89(Object[] references) throws Exception {

    }

    @Override
    public org.apache.flink.table.data.binary.BinaryRowData apply(org.apache.flink.table.data.RowData in1) {
        org.apache.flink.table.data.binary.BinaryStringData field$90;
        boolean isNull$90;
        int field$91;
        boolean isNull$91;


        outWriter.reset();

        isNull$90 = in1.isNullAt(0);
        field$90 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$90) {
            field$90 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(0));
        }
        if (isNull$90) {
            outWriter.setNullAt(0);
        } else {
            outWriter.writeString(0, field$90);
        }


        isNull$91 = in1.isNullAt(1);
        field$91 = -1;
        if (!isNull$91) {
            field$91 = in1.getInt(1);
        }
        if (isNull$91) {
            outWriter.setNullAt(1);
        } else {
            outWriter.writeInt(1, field$91);
        }

        outWriter.complete();


        return out;
    }
}