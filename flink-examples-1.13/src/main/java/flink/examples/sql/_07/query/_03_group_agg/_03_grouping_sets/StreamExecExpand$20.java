package flink.examples.sql._07.query._03_group_agg._03_grouping_sets;


public class StreamExecExpand$20 extends org.apache.flink.table.runtime.operators.TableStreamOperator
        implements org.apache.flink.streaming.api.operators.OneInputStreamOperator {

    private final Object[] references;
    private transient org.apache.flink.table.runtime.typeutils.StringDataSerializer typeSerializer$15;
    private transient org.apache.flink.table.runtime.typeutils.StringDataSerializer typeSerializer$18;
    org.apache.flink.table.data.BoxedWrapperRowData out = new org.apache.flink.table.data.BoxedWrapperRowData(3);
    private final org.apache.flink.streaming.runtime.streamrecord.StreamRecord outElement =
            new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(null);

    public StreamExecExpand$20(
            Object[] references,
            org.apache.flink.streaming.runtime.tasks.StreamTask task,
            org.apache.flink.streaming.api.graph.StreamConfig config,
            org.apache.flink.streaming.api.operators.Output output,
            org.apache.flink.streaming.runtime.tasks.ProcessingTimeService processingTimeService) throws Exception {
        this.references = references;
        typeSerializer$15 = (((org.apache.flink.table.runtime.typeutils.StringDataSerializer) references[0]));
        typeSerializer$18 = (((org.apache.flink.table.runtime.typeutils.StringDataSerializer) references[1]));
        this.setup(task, config, output);
        if (this instanceof org.apache.flink.streaming.api.operators.AbstractStreamOperator) {
            ((org.apache.flink.streaming.api.operators.AbstractStreamOperator) this)
                    .setProcessingTimeService(processingTimeService);
        }
    }

    @Override
    public void open() throws Exception {
        super.open();

    }

    @Override
    public void processElement(org.apache.flink.streaming.runtime.streamrecord.StreamRecord element) throws Exception {
        org.apache.flink.table.data.RowData in1 = (org.apache.flink.table.data.RowData) element.getValue();

        org.apache.flink.table.data.binary.BinaryStringData field$14;
        boolean isNull$14;
        org.apache.flink.table.data.binary.BinaryStringData field$16;
        org.apache.flink.table.data.binary.BinaryStringData field$17;
        boolean isNull$17;
        org.apache.flink.table.data.binary.BinaryStringData field$19;

        isNull$14 = in1.isNullAt(0);
        field$14 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$14) {
            field$14 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(0));
        }
        field$16 = field$14;
        if (!isNull$14) {
            field$16 = (org.apache.flink.table.data.binary.BinaryStringData) (typeSerializer$15.copy(field$16));
        }


        isNull$17 = in1.isNullAt(1);
        field$17 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$17) {
            field$17 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(1));
        }
        field$19 = field$17;
        if (!isNull$17) {
            field$19 = (org.apache.flink.table.data.binary.BinaryStringData) (typeSerializer$18.copy(field$19));
        }

        out.setRowKind(in1.getRowKind());


        if (isNull$14) {
            out.setNullAt(0);
        } else {
            out.setNonPrimitiveValue(0, field$16);
        }


        if (isNull$17) {
            out.setNullAt(1);
        } else {
            out.setNonPrimitiveValue(1, field$19);
        }


        if (false) {
            out.setNullAt(2);
        } else {
            out.setLong(2, ((long) 0L));
        }


        output.collect(outElement.replace(out));
        out.setRowKind(in1.getRowKind());


        if (isNull$14) {
            out.setNullAt(0);
        } else {
            out.setNonPrimitiveValue(0, field$16);
        }


        if (true) {
            out.setNullAt(1);
        } else {
            out.setNonPrimitiveValue(1,
                    ((org.apache.flink.table.data.binary.BinaryStringData) org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8));
        }


        if (false) {
            out.setNullAt(2);
        } else {
            out.setLong(2, ((long) 1L));
        }


        output.collect(outElement.replace(out));
        out.setRowKind(in1.getRowKind());


        if (true) {
            out.setNullAt(0);
        } else {
            out.setNonPrimitiveValue(0,
                    ((org.apache.flink.table.data.binary.BinaryStringData) org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8));
        }


        if (true) {
            out.setNullAt(1);
        } else {
            out.setNonPrimitiveValue(1,
                    ((org.apache.flink.table.data.binary.BinaryStringData) org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8));
        }


        if (false) {
            out.setNullAt(2);
        } else {
            out.setLong(2, ((long) 3L));
        }


        output.collect(outElement.replace(out));
    }


    @Override
    public void close() throws Exception {
        super.close();

    }


}