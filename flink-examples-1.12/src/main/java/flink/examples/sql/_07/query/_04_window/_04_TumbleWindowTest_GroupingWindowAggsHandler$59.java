package flink.examples.sql._07.query._04_window;


public final class _04_TumbleWindowTest_GroupingWindowAggsHandler$59 implements
        org.apache.flink.table.runtime.generated.NamespaceAggsHandleFunction<org.apache.flink.table.runtime.operators.window.TimeWindow> {

    long agg0_count1;
    boolean agg0_count1IsNull;
    long agg1_sum;
    boolean agg1_sumIsNull;
    long agg2_max;
    boolean agg2_maxIsNull;
    long agg3_min;
    boolean agg3_minIsNull;
    long agg4_count;
    boolean agg4_countIsNull;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$22;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$23;
    private org.apache.flink.table.runtime.dataview.StateMapView distinctAcc_0_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData distinctAcc_0_dataview_raw_value;
    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
    org.apache.flink.table.data.GenericRowData acc$25 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData acc$27 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData aggValue$58 = new org.apache.flink.table.data.GenericRowData(9);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    private org.apache.flink.table.runtime.operators.window.TimeWindow namespace;

    public _04_TumbleWindowTest_GroupingWindowAggsHandler$59(Object[] references) throws Exception {
        externalSerializer$22 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[0]));
        externalSerializer$23 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[1]));
    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

        distinctAcc_0_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("distinctAcc_0", true, externalSerializer$22, externalSerializer$23);
        distinctAcc_0_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinctAcc_0_dataview);

        distinct_view_0 = distinctAcc_0_dataview;
    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        boolean isNull$34;
        long result$35;
        long field$36;
        boolean isNull$36;
        boolean isNull$37;
        long result$38;
        boolean isNull$41;
        boolean result$42;
        boolean isNull$46;
        boolean result$47;
        long field$51;
        boolean isNull$51;
        boolean isNull$53;
        long result$54;
        isNull$51 = accInput.isNullAt(4);
        field$51 = -1L;
        if (!isNull$51) {
            field$51 = accInput.getLong(4);
        }
        isNull$36 = accInput.isNullAt(3);
        field$36 = -1L;
        if (!isNull$36) {
            field$36 = accInput.getLong(3);
        }


        isNull$34 = agg0_count1IsNull || false;
        result$35 = -1L;
        if (!isNull$34) {

            result$35 = (long) (agg0_count1 + ((long) 1L));

        }

        agg0_count1 = result$35;
        ;
        agg0_count1IsNull = isNull$34;


        long result$40 = -1L;
        boolean isNull$40;
        if (isNull$36) {

            isNull$40 = agg1_sumIsNull;
            if (!isNull$40) {
                result$40 = agg1_sum;
            }
        } else {
            long result$39 = -1L;
            boolean isNull$39;
            if (agg1_sumIsNull) {

                isNull$39 = isNull$36;
                if (!isNull$39) {
                    result$39 = field$36;
                }
            } else {


                isNull$37 = agg1_sumIsNull || isNull$36;
                result$38 = -1L;
                if (!isNull$37) {

                    result$38 = (long) (agg1_sum + field$36);

                }

                isNull$39 = isNull$37;
                if (!isNull$39) {
                    result$39 = result$38;
                }
            }
            isNull$40 = isNull$39;
            if (!isNull$40) {
                result$40 = result$39;
            }
        }
        agg1_sum = result$40;
        ;
        agg1_sumIsNull = isNull$40;


        long result$45 = -1L;
        boolean isNull$45;
        if (isNull$36) {

            isNull$45 = agg2_maxIsNull;
            if (!isNull$45) {
                result$45 = agg2_max;
            }
        } else {
            long result$44 = -1L;
            boolean isNull$44;
            if (agg2_maxIsNull) {

                isNull$44 = isNull$36;
                if (!isNull$44) {
                    result$44 = field$36;
                }
            } else {
                isNull$41 = isNull$36 || agg2_maxIsNull;
                result$42 = false;
                if (!isNull$41) {

                    result$42 = field$36 > agg2_max;

                }

                long result$43 = -1L;
                boolean isNull$43;
                if (result$42) {

                    isNull$43 = isNull$36;
                    if (!isNull$43) {
                        result$43 = field$36;
                    }
                } else {

                    isNull$43 = agg2_maxIsNull;
                    if (!isNull$43) {
                        result$43 = agg2_max;
                    }
                }
                isNull$44 = isNull$43;
                if (!isNull$44) {
                    result$44 = result$43;
                }
            }
            isNull$45 = isNull$44;
            if (!isNull$45) {
                result$45 = result$44;
            }
        }
        agg2_max = result$45;
        ;
        agg2_maxIsNull = isNull$45;


        long result$50 = -1L;
        boolean isNull$50;
        if (isNull$36) {

            isNull$50 = agg3_minIsNull;
            if (!isNull$50) {
                result$50 = agg3_min;
            }
        } else {
            long result$49 = -1L;
            boolean isNull$49;
            if (agg3_minIsNull) {

                isNull$49 = isNull$36;
                if (!isNull$49) {
                    result$49 = field$36;
                }
            } else {
                isNull$46 = isNull$36 || agg3_minIsNull;
                result$47 = false;
                if (!isNull$46) {

                    result$47 = field$36 < agg3_min;

                }

                long result$48 = -1L;
                boolean isNull$48;
                if (result$47) {

                    isNull$48 = isNull$36;
                    if (!isNull$48) {
                        result$48 = field$36;
                    }
                } else {

                    isNull$48 = agg3_minIsNull;
                    if (!isNull$48) {
                        result$48 = agg3_min;
                    }
                }
                isNull$49 = isNull$48;
                if (!isNull$49) {
                    result$49 = result$48;
                }
            }
            isNull$50 = isNull$49;
            if (!isNull$50) {
                result$50 = result$49;
            }
        }
        agg3_min = result$50;
        ;
        agg3_minIsNull = isNull$50;


        Long distinctKey$52 = (Long) field$51;
        if (isNull$51) {
            distinctKey$52 = null;
        }

        Long value$56 = (Long) distinct_view_0.get(distinctKey$52);
        if (value$56 == null) {
            value$56 = 0L;
        }

        boolean is_distinct_value_changed_0 = false;

        long existed$57 = ((long) value$56) & (1L << 0);
        if (existed$57 == 0) {  // not existed
            value$56 = ((long) value$56) | (1L << 0);
            is_distinct_value_changed_0 = true;

            long result$55 = -1L;
            boolean isNull$55;
            if (isNull$51) {

                isNull$55 = agg4_countIsNull;
                if (!isNull$55) {
                    result$55 = agg4_count;
                }
            } else {


                isNull$53 = agg4_countIsNull || false;
                result$54 = -1L;
                if (!isNull$53) {

                    result$54 = (long) (agg4_count + ((long) 1L));

                }

                isNull$55 = isNull$53;
                if (!isNull$55) {
                    result$55 = result$54;
                }
            }
            agg4_count = result$55;
            ;
            agg4_countIsNull = isNull$55;

        }

        if (is_distinct_value_changed_0) {
            distinct_view_0.put(distinctKey$52, value$56);
        }


    }

    @Override
    public void retract(org.apache.flink.table.data.RowData retractInput) throws Exception {

        throw new RuntimeException(
                "This function not require retract method, but the retract method is called.");

    }

    @Override
    public void merge(org.apache.flink.table.runtime.operators.window.TimeWindow ns,
            org.apache.flink.table.data.RowData otherAcc) throws Exception {
        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;

        throw new RuntimeException("This function not require merge method, but the merge method is called.");

    }

    @Override
    public void setAccumulators(org.apache.flink.table.runtime.operators.window.TimeWindow ns,
            org.apache.flink.table.data.RowData acc)
            throws Exception {
        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;

        long field$28;
        boolean isNull$28;
        long field$29;
        boolean isNull$29;
        long field$30;
        boolean isNull$30;
        long field$31;
        boolean isNull$31;
        long field$32;
        boolean isNull$32;
        org.apache.flink.table.data.binary.BinaryRawValueData field$33;
        boolean isNull$33;
        isNull$32 = acc.isNullAt(4);
        field$32 = -1L;
        if (!isNull$32) {
            field$32 = acc.getLong(4);
        }
        isNull$28 = acc.isNullAt(0);
        field$28 = -1L;
        if (!isNull$28) {
            field$28 = acc.getLong(0);
        }
        isNull$29 = acc.isNullAt(1);
        field$29 = -1L;
        if (!isNull$29) {
            field$29 = acc.getLong(1);
        }
        isNull$31 = acc.isNullAt(3);
        field$31 = -1L;
        if (!isNull$31) {
            field$31 = acc.getLong(3);
        }

        // when namespace is null, the dataview is used in heap, no key and namespace set
        if (namespace != null) {
            distinctAcc_0_dataview.setCurrentNamespace(namespace);
            distinct_view_0 = distinctAcc_0_dataview;
        } else {
            isNull$33 = acc.isNullAt(5);
            field$33 = null;
            if (!isNull$33) {
                field$33 = ((org.apache.flink.table.data.binary.BinaryRawValueData) acc.getRawValue(5));
            }
            distinct_view_0 = (org.apache.flink.table.api.dataview.MapView) field$33.getJavaObject();
        }

        isNull$30 = acc.isNullAt(2);
        field$30 = -1L;
        if (!isNull$30) {
            field$30 = acc.getLong(2);
        }

        agg0_count1 = field$28;
        ;
        agg0_count1IsNull = isNull$28;


        agg1_sum = field$29;
        ;
        agg1_sumIsNull = isNull$29;


        agg2_max = field$30;
        ;
        agg2_maxIsNull = isNull$30;


        agg3_min = field$31;
        ;
        agg3_minIsNull = isNull$31;


        agg4_count = field$32;
        ;
        agg4_countIsNull = isNull$32;


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$27 = new org.apache.flink.table.data.GenericRowData(6);


        if (agg0_count1IsNull) {
            acc$27.setField(0, null);
        } else {
            acc$27.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            acc$27.setField(1, null);
        } else {
            acc$27.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            acc$27.setField(2, null);
        } else {
            acc$27.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            acc$27.setField(3, null);
        } else {
            acc$27.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            acc$27.setField(4, null);
        } else {
            acc$27.setField(4, agg4_count);
        }


        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$26 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);

        if (false) {
            acc$27.setField(5, null);
        } else {
            acc$27.setField(5, distinct_acc$26);
        }


        return acc$27;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$25 = new org.apache.flink.table.data.GenericRowData(6);


        if (false) {
            acc$25.setField(0, null);
        } else {
            acc$25.setField(0, ((long) 0L));
        }


        if (true) {
            acc$25.setField(1, null);
        } else {
            acc$25.setField(1, ((long) -1L));
        }


        if (true) {
            acc$25.setField(2, null);
        } else {
            acc$25.setField(2, ((long) -1L));
        }


        if (true) {
            acc$25.setField(3, null);
        } else {
            acc$25.setField(3, ((long) -1L));
        }


        if (false) {
            acc$25.setField(4, null);
        } else {
            acc$25.setField(4, ((long) 0L));
        }


        org.apache.flink.table.api.dataview.MapView mapview$24 = new org.apache.flink.table.api.dataview.MapView();
        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$24 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$24);

        if (false) {
            acc$25.setField(5, null);
        } else {
            acc$25.setField(5, distinct_acc$24);
        }


        return acc$25;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue(org.apache.flink.table.runtime.operators.window.TimeWindow ns)
            throws Exception {
        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;


        aggValue$58 = new org.apache.flink.table.data.GenericRowData(9);


        if (agg0_count1IsNull) {
            aggValue$58.setField(0, null);
        } else {
            aggValue$58.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            aggValue$58.setField(1, null);
        } else {
            aggValue$58.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            aggValue$58.setField(2, null);
        } else {
            aggValue$58.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            aggValue$58.setField(3, null);
        } else {
            aggValue$58.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            aggValue$58.setField(4, null);
        } else {
            aggValue$58.setField(4, agg4_count);
        }


        if (false) {
            aggValue$58.setField(5, null);
        } else {
            aggValue$58.setField(5, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace.getStart()));
        }


        if (false) {
            aggValue$58.setField(6, null);
        } else {
            aggValue$58.setField(6, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace.getEnd()));
        }


        if (false) {
            aggValue$58.setField(7, null);
        } else {
            aggValue$58.setField(7, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace.getEnd() - 1));
        }


        if (true) {
            aggValue$58.setField(8, null);
        } else {
            aggValue$58.setField(8, org.apache.flink.table.data.TimestampData.fromEpochMillis(-1L));
        }


        return aggValue$58;

    }

    @Override
    public void cleanup(org.apache.flink.table.runtime.operators.window.TimeWindow ns) throws Exception {
        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;

        distinctAcc_0_dataview.setCurrentNamespace(namespace);
        distinctAcc_0_dataview.clear();


    }

    @Override
    public void close() throws Exception {

    }
}