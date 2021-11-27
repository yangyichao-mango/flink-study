package flink.examples.sql._07.query._06_joins._02_interval_joins._01_outer_join;


public class WindowJoinFunction$46
        extends org.apache.flink.api.common.functions.RichFlatJoinFunction {

    final org.apache.flink.table.dataformat.JoinedRow joinedRow = new org.apache.flink.table.dataformat.JoinedRow();

    public WindowJoinFunction$46(Object[] references) throws Exception {

    }


    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {

    }

    @Override
    public void join(Object _in1, Object _in2, org.apache.flink.util.Collector c) throws Exception {
        org.apache.flink.table.dataformat.BaseRow in1 = (org.apache.flink.table.dataformat.BaseRow) _in1;
        org.apache.flink.table.dataformat.BaseRow in2 = (org.apache.flink.table.dataformat.BaseRow) _in2;

        int result$40;
        boolean isNull$40;
        int field$41;
        boolean isNull$41;
        int result$42;
        boolean isNull$42;
        int field$43;
        boolean isNull$43;
        boolean isNull$44;
        boolean result$45;
        result$40 = -1;
        isNull$40 = true;
        if (in1 != null) {
            isNull$41 = in1.isNullAt(0);
            field$41 = -1;
            if (!isNull$41) {
                field$41 = in1.getInt(0);
            }
            result$40 = field$41;
            isNull$40 = isNull$41;
        }
        result$42 = -1;
        isNull$42 = true;
        if (in2 != null) {
            isNull$43 = in2.isNullAt(0);
            field$43 = -1;
            if (!isNull$43) {
                field$43 = in2.getInt(0);
            }
            result$42 = field$43;
            isNull$42 = isNull$43;
        }


        isNull$44 = isNull$40 || isNull$42;
        result$45 = false;
        if (!isNull$44) {

            result$45 = result$40 == result$42;

        }

        if (result$45) {

            joinedRow.replace(in1, in2);
            c.collect(joinedRow);
        }
    }

    @Override
    public void close() throws Exception {

    }
}