package flink.examples.sql._07.query._03_group_agg;


public final class _03_CountDistinctGroupAggTest_GroupAggsHandler$17 implements org.apache.flink.table.runtime.generated.AggsHandleFunction {

    long agg0_count;
    boolean agg0_countIsNull;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$2;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$3;
    private org.apache.flink.table.runtime.dataview.StateMapView distinctAcc_0_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData distinctAcc_0_dataview_raw_value;
    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
    org.apache.flink.table.data.GenericRowData acc$5 = new org.apache.flink.table.data.GenericRowData(2);
    org.apache.flink.table.data.GenericRowData acc$7 = new org.apache.flink.table.data.GenericRowData(2);
    org.apache.flink.table.data.GenericRowData aggValue$16 = new org.apache.flink.table.data.GenericRowData(1);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    public _03_CountDistinctGroupAggTest_GroupAggsHandler$17(Object[] references) throws Exception {
        externalSerializer$2 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[0]));
        externalSerializer$3 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[1]));
    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

        distinctAcc_0_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("distinctAcc_0", true, externalSerializer$2, externalSerializer$3);
        distinctAcc_0_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinctAcc_0_dataview);

        distinct_view_0 = distinctAcc_0_dataview;
    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        long field$9;
        boolean isNull$9;
        boolean isNull$11;
        long result$12;
        isNull$9 = accInput.isNullAt(1);
        field$9 = -1L;
        if (!isNull$9) {
            field$9 = accInput.getLong(1);
        }


        Long distinctKey$10 = (Long) field$9;
        if (isNull$9) {
            distinctKey$10 = null;
        }

        Long value$14 = (Long) distinct_view_0.get(distinctKey$10);
        if (value$14 == null) {
            value$14 = 0L;
        }

        boolean is_distinct_value_changed_0 = false;

        long existed$15 = ((long) value$14) & (1L << 0);
        if (existed$15 == 0) {  // not existed
            value$14 = ((long) value$14) | (1L << 0);
            is_distinct_value_changed_0 = true;

            long result$13 = -1L;
            boolean isNull$13;
            if (isNull$9) {

                isNull$13 = agg0_countIsNull;
                if (!isNull$13) {
                    result$13 = agg0_count;
                }
            } else {


                isNull$11 = agg0_countIsNull || false;
                result$12 = -1L;
                if (!isNull$11) {

                    result$12 = (long) (agg0_count + ((long) 1L));

                }

                isNull$13 = isNull$11;
                if (!isNull$13) {
                    result$13 = result$12;
                }
            }
            agg0_count = result$13;
            ;
            agg0_countIsNull = isNull$13;

        }

        if (is_distinct_value_changed_0) {
            distinct_view_0.put(distinctKey$10, value$14);
        }


    }

    @Override
    public void retract(org.apache.flink.table.data.RowData retractInput) throws Exception {

        throw new RuntimeException(
                "This function not require retract method, but the retract method is called.");

    }

    @Override
    public void merge(org.apache.flink.table.data.RowData otherAcc) throws Exception {

        throw new RuntimeException("This function not require merge method, but the merge method is called.");

    }

    @Override
    public void setAccumulators(org.apache.flink.table.data.RowData acc) throws Exception {

        long field$8;
        boolean isNull$8;
        isNull$8 = acc.isNullAt(0);
        field$8 = -1L;
        if (!isNull$8) {
            field$8 = acc.getLong(0);
        }

        distinct_view_0 = distinctAcc_0_dataview;

        agg0_count = field$8;
        ;
        agg0_countIsNull = isNull$8;


    }

    @Override
    public void resetAccumulators() throws Exception {


        agg0_count = ((long) 0L);
        agg0_countIsNull = false;

        distinct_view_0.clear();

    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$7 = new org.apache.flink.table.data.GenericRowData(2);


        if (agg0_countIsNull) {
            acc$7.setField(0, null);
        } else {
            acc$7.setField(0, agg0_count);
        }


        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$6 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);

        if (false) {
            acc$7.setField(1, null);
        } else {
            acc$7.setField(1, distinct_acc$6);
        }


        return acc$7;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$5 = new org.apache.flink.table.data.GenericRowData(2);


        if (false) {
            acc$5.setField(0, null);
        } else {
            acc$5.setField(0, ((long) 0L));
        }


        org.apache.flink.table.api.dataview.MapView mapview$4 = new org.apache.flink.table.api.dataview.MapView();
        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$4 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$4);

        if (false) {
            acc$5.setField(1, null);
        } else {
            acc$5.setField(1, distinct_acc$4);
        }


        return acc$5;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue() throws Exception {


        aggValue$16 = new org.apache.flink.table.data.GenericRowData(1);


        if (agg0_countIsNull) {
            aggValue$16.setField(0, null);
        } else {
            aggValue$16.setField(0, agg0_count);
        }


        return aggValue$16;

    }

    @Override
    public void cleanup() throws Exception {

        distinctAcc_0_dataview.clear();


    }

    @Override
    public void close() throws Exception {

    }
}