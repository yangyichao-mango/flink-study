package flink.examples.sql._07.query._04_window_agg;


public final class _04_TumbleWindowTest_WatermarkGenerator$6
        extends org.apache.flink.table.runtime.generated.WatermarkGenerator {


    public _04_TumbleWindowTest_WatermarkGenerator$6(Object[] references) throws Exception {

    }

    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {

    }

    @Override
    public Long currentWatermark(org.apache.flink.table.data.RowData row) throws Exception {

        org.apache.flink.table.data.TimestampData field$7;
        boolean isNull$7;
        boolean isNull$8;
        org.apache.flink.table.data.TimestampData result$9;
        isNull$7 = row.isNullAt(3);
        field$7 = null;
        if (!isNull$7) {
            field$7 = row.getTimestamp(3, 3);
        }


        isNull$8 = isNull$7 || false;
        result$9 = null;
        if (!isNull$8) {

            result$9 = org.apache.flink.table.data.TimestampData
                    .fromEpochMillis(field$7.getMillisecond() - ((long) 5000L), field$7.getNanoOfMillisecond());

        }

        if (isNull$8) {
            return null;
        } else {
            return result$9.getMillisecond();
        }
    }

    @Override
    public void close() throws Exception {

    }
}