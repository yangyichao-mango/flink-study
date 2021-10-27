package flink.examples.sql._07.query._04_window_agg._02_cumulate_window.cumulate.global_agg;


public class KeyProjection$301 implements
        org.apache.flink.table.runtime.generated.Projection<org.apache.flink.table.data.RowData,
                org.apache.flink.table.data.binary.BinaryRowData> {

    org.apache.flink.table.data.binary.BinaryRowData out = new org.apache.flink.table.data.binary.BinaryRowData(2);
    org.apache.flink.table.data.writer.BinaryRowWriter outWriter =
            new org.apache.flink.table.data.writer.BinaryRowWriter(out);

    public KeyProjection$301(Object[] references) throws Exception {

    }

    @Override
    public org.apache.flink.table.data.binary.BinaryRowData apply(org.apache.flink.table.data.RowData in1) {
        org.apache.flink.table.data.binary.BinaryStringData field$302;
        boolean isNull$302;
        int field$303;
        boolean isNull$303;


        outWriter.reset();

        isNull$302 = in1.isNullAt(0);
        field$302 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$302) {
            field$302 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(0));
        }
        if (isNull$302) {
            outWriter.setNullAt(0);
        } else {
            outWriter.writeString(0, field$302);
        }


        isNull$303 = in1.isNullAt(1);
        field$303 = -1;
        if (!isNull$303) {
            field$303 = in1.getInt(1);
        }
        if (isNull$303) {
            outWriter.setNullAt(1);
        } else {
            outWriter.writeInt(1, field$303);
        }

        outWriter.complete();


        return out;
    }
}