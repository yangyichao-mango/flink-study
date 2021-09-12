package flink.examples.sql._07.query._01_select_where;

public class _01_StreamExecCalc$10 extends org.apache.flink.table.runtime.operators.TableStreamOperator
        implements org.apache.flink.streaming.api.operators.OneInputStreamOperator {

    private final Object[] references;
    org.apache.flink.table.data.BoxedWrapperRowData out = new org.apache.flink.table.data.BoxedWrapperRowData(2);
    private final org.apache.flink.streaming.runtime.streamrecord.StreamRecord outElement =
            new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(null);

    public _01_StreamExecCalc$10(
            Object[] references,
            org.apache.flink.streaming.runtime.tasks.StreamTask task,
            org.apache.flink.streaming.api.graph.StreamConfig config,
            org.apache.flink.streaming.api.operators.Output output,
            org.apache.flink.streaming.runtime.tasks.ProcessingTimeService processingTimeService) throws Exception {
        this.references = references;

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

        long field$1;
        boolean isNull$1;
        boolean isNull$2;
        boolean result$3;
        org.apache.flink.table.data.DecimalData field$4;
        boolean isNull$4;


        isNull$1 = in1.isNullAt(0);
        field$1 = -1L;
        if (!isNull$1) {
            field$1 = in1.getLong(0);
        }


        isNull$2 = isNull$1 || false;
        result$3 = false;
        if (!isNull$2) {

            result$3 = field$1 == ((long) 10L);

        }

        if (result$3) {
            isNull$4 = in1.isNullAt(1);
            field$4 = null;
            if (!isNull$4) {
                field$4 = in1.getDecimal(1, 32, 2);
            }

            out.setRowKind(in1.getRowKind());


            if (false) {
                out.setNullAt(0);
            } else {
                out.setLong(0, ((long) 10L));
            }


            if (isNull$4) {
                out.setNullAt(1);
            } else {
                out.setNonPrimitiveValue(1, field$4);
            }


            output.collect(outElement.replace(out));

        }

    }


    @Override
    public void close() throws Exception {
        super.close();

    }


}