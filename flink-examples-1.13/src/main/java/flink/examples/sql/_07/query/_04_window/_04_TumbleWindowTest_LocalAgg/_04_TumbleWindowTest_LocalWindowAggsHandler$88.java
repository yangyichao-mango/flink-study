package flink.examples.sql._07.query._04_window._04_TumbleWindowTest_LocalAgg;


public final class _04_TumbleWindowTest_LocalWindowAggsHandler$88
        implements org.apache.flink.table.runtime.generated.NamespaceAggsHandleFunction<Long> {

    private transient org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.TumblingSliceAssigner
            sliceAssigner$21;
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
    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
    org.apache.flink.table.data.GenericRowData acc$23 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData acc$25 = new org.apache.flink.table.data.GenericRowData(6);
    private org.apache.flink.table.api.dataview.MapView otherMapView$77;
    private transient org.apache.flink.table.data.conversion.RawObjectConverter converter$78;
    org.apache.flink.table.data.GenericRowData aggValue$87 = new org.apache.flink.table.data.GenericRowData(5);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    private Long namespace;

    public _04_TumbleWindowTest_LocalWindowAggsHandler$88(Object[] references) throws Exception {
        sliceAssigner$21 =
                (((org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.TumblingSliceAssigner) references[0]));
        converter$78 = (((org.apache.flink.table.data.conversion.RawObjectConverter) references[1]));
    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

        converter$78.open(getRuntimeContext().getUserCodeClassLoader());

    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        boolean isNull$32;
        long result$33;
        long field$34;
        boolean isNull$34;
        boolean isNull$35;
        long result$36;
        boolean isNull$39;
        boolean result$40;
        boolean isNull$44;
        boolean result$45;
        long field$49;
        boolean isNull$49;
        boolean isNull$51;
        long result$52;
        isNull$34 = accInput.isNullAt(2);
        field$34 = -1L;
        if (!isNull$34) {
            field$34 = accInput.getLong(2);
        }
        isNull$49 = accInput.isNullAt(3);
        field$49 = -1L;
        if (!isNull$49) {
            field$49 = accInput.getLong(3);
        }


        isNull$32 = agg0_count1IsNull || false;
        result$33 = -1L;
        if (!isNull$32) {

            result$33 = (long) (agg0_count1 + ((long) 1L));

        }

        agg0_count1 = result$33;
        ;
        agg0_count1IsNull = isNull$32;


        long result$38 = -1L;
        boolean isNull$38;
        if (isNull$34) {

            isNull$38 = agg1_sumIsNull;
            if (!isNull$38) {
                result$38 = agg1_sum;
            }
        } else {
            long result$37 = -1L;
            boolean isNull$37;
            if (agg1_sumIsNull) {

                isNull$37 = isNull$34;
                if (!isNull$37) {
                    result$37 = field$34;
                }
            } else {


                isNull$35 = agg1_sumIsNull || isNull$34;
                result$36 = -1L;
                if (!isNull$35) {

                    result$36 = (long) (agg1_sum + field$34);

                }

                isNull$37 = isNull$35;
                if (!isNull$37) {
                    result$37 = result$36;
                }
            }
            isNull$38 = isNull$37;
            if (!isNull$38) {
                result$38 = result$37;
            }
        }
        agg1_sum = result$38;
        ;
        agg1_sumIsNull = isNull$38;


        long result$43 = -1L;
        boolean isNull$43;
        if (isNull$34) {

            isNull$43 = agg2_maxIsNull;
            if (!isNull$43) {
                result$43 = agg2_max;
            }
        } else {
            long result$42 = -1L;
            boolean isNull$42;
            if (agg2_maxIsNull) {

                isNull$42 = isNull$34;
                if (!isNull$42) {
                    result$42 = field$34;
                }
            } else {
                isNull$39 = isNull$34 || agg2_maxIsNull;
                result$40 = false;
                if (!isNull$39) {

                    result$40 = field$34 > agg2_max;

                }

                long result$41 = -1L;
                boolean isNull$41;
                if (result$40) {

                    isNull$41 = isNull$34;
                    if (!isNull$41) {
                        result$41 = field$34;
                    }
                } else {

                    isNull$41 = agg2_maxIsNull;
                    if (!isNull$41) {
                        result$41 = agg2_max;
                    }
                }
                isNull$42 = isNull$41;
                if (!isNull$42) {
                    result$42 = result$41;
                }
            }
            isNull$43 = isNull$42;
            if (!isNull$43) {
                result$43 = result$42;
            }
        }
        agg2_max = result$43;
        ;
        agg2_maxIsNull = isNull$43;


        long result$48 = -1L;
        boolean isNull$48;
        if (isNull$34) {

            isNull$48 = agg3_minIsNull;
            if (!isNull$48) {
                result$48 = agg3_min;
            }
        } else {
            long result$47 = -1L;
            boolean isNull$47;
            if (agg3_minIsNull) {

                isNull$47 = isNull$34;
                if (!isNull$47) {
                    result$47 = field$34;
                }
            } else {
                isNull$44 = isNull$34 || agg3_minIsNull;
                result$45 = false;
                if (!isNull$44) {

                    result$45 = field$34 < agg3_min;

                }

                long result$46 = -1L;
                boolean isNull$46;
                if (result$45) {

                    isNull$46 = isNull$34;
                    if (!isNull$46) {
                        result$46 = field$34;
                    }
                } else {

                    isNull$46 = agg3_minIsNull;
                    if (!isNull$46) {
                        result$46 = agg3_min;
                    }
                }
                isNull$47 = isNull$46;
                if (!isNull$47) {
                    result$47 = result$46;
                }
            }
            isNull$48 = isNull$47;
            if (!isNull$48) {
                result$48 = result$47;
            }
        }
        agg3_min = result$48;
        ;
        agg3_minIsNull = isNull$48;


        Long distinctKey$50 = (Long) field$49;
        if (isNull$49) {
            distinctKey$50 = null;
        }

        Long value$54 = (Long) distinct_view_0.get(distinctKey$50);
        if (value$54 == null) {
            value$54 = 0L;
        }

        boolean is_distinct_value_changed_0 = false;

        long existed$55 = ((long) value$54) & (1L << 0);
        if (existed$55 == 0) {  // not existed
            value$54 = ((long) value$54) | (1L << 0);
            is_distinct_value_changed_0 = true;

            long result$53 = -1L;
            boolean isNull$53;
            if (isNull$49) {

                isNull$53 = agg4_countIsNull;
                if (!isNull$53) {
                    result$53 = agg4_count;
                }
            } else {


                isNull$51 = agg4_countIsNull || false;
                result$52 = -1L;
                if (!isNull$51) {

                    result$52 = (long) (agg4_count + ((long) 1L));

                }

                isNull$53 = isNull$51;
                if (!isNull$53) {
                    result$53 = result$52;
                }
            }
            agg4_count = result$53;
            ;
            agg4_countIsNull = isNull$53;

        }

        if (is_distinct_value_changed_0) {
            distinct_view_0.put(distinctKey$50, value$54);
        }


    }

    @Override
    public void retract(org.apache.flink.table.data.RowData retractInput) throws Exception {

        throw new RuntimeException(
                "This function not require retract method, but the retract method is called.");

    }

    @Override
    public void merge(Long ns, org.apache.flink.table.data.RowData otherAcc) throws Exception {
        namespace = (Long) ns;

        long field$56;
        boolean isNull$56;
        boolean isNull$57;
        long result$58;
        long field$59;
        boolean isNull$59;
        boolean isNull$60;
        long result$61;
        long field$64;
        boolean isNull$64;
        boolean isNull$65;
        boolean result$66;
        long field$70;
        boolean isNull$70;
        boolean isNull$71;
        boolean result$72;
        org.apache.flink.table.data.binary.BinaryRawValueData field$76;
        boolean isNull$76;
        boolean isNull$82;
        long result$83;
        isNull$64 = otherAcc.isNullAt(2);
        field$64 = -1L;
        if (!isNull$64) {
            field$64 = otherAcc.getLong(2);
        }
        isNull$59 = otherAcc.isNullAt(1);
        field$59 = -1L;
        if (!isNull$59) {
            field$59 = otherAcc.getLong(1);
        }
        isNull$56 = otherAcc.isNullAt(0);
        field$56 = -1L;
        if (!isNull$56) {
            field$56 = otherAcc.getLong(0);
        }
        isNull$70 = otherAcc.isNullAt(3);
        field$70 = -1L;
        if (!isNull$70) {
            field$70 = otherAcc.getLong(3);
        }

        isNull$76 = otherAcc.isNullAt(5);
        field$76 = null;
        if (!isNull$76) {
            field$76 = ((org.apache.flink.table.data.binary.BinaryRawValueData) otherAcc.getRawValue(5));
        }
        otherMapView$77 = null;
        if (!isNull$76) {
            otherMapView$77 =
                    (org.apache.flink.table.api.dataview.MapView) converter$78
                            .toExternal((org.apache.flink.table.data.binary.BinaryRawValueData) field$76);
        }


        isNull$57 = agg0_count1IsNull || isNull$56;
        result$58 = -1L;
        if (!isNull$57) {

            result$58 = (long) (agg0_count1 + field$56);

        }

        agg0_count1 = result$58;
        ;
        agg0_count1IsNull = isNull$57;


        long result$63 = -1L;
        boolean isNull$63;
        if (isNull$59) {

            isNull$63 = agg1_sumIsNull;
            if (!isNull$63) {
                result$63 = agg1_sum;
            }
        } else {
            long result$62 = -1L;
            boolean isNull$62;
            if (agg1_sumIsNull) {

                isNull$62 = isNull$59;
                if (!isNull$62) {
                    result$62 = field$59;
                }
            } else {


                isNull$60 = agg1_sumIsNull || isNull$59;
                result$61 = -1L;
                if (!isNull$60) {

                    result$61 = (long) (agg1_sum + field$59);

                }

                isNull$62 = isNull$60;
                if (!isNull$62) {
                    result$62 = result$61;
                }
            }
            isNull$63 = isNull$62;
            if (!isNull$63) {
                result$63 = result$62;
            }
        }
        agg1_sum = result$63;
        ;
        agg1_sumIsNull = isNull$63;


        long result$69 = -1L;
        boolean isNull$69;
        if (isNull$64) {

            isNull$69 = agg2_maxIsNull;
            if (!isNull$69) {
                result$69 = agg2_max;
            }
        } else {
            long result$68 = -1L;
            boolean isNull$68;
            if (agg2_maxIsNull) {

                isNull$68 = isNull$64;
                if (!isNull$68) {
                    result$68 = field$64;
                }
            } else {
                isNull$65 = isNull$64 || agg2_maxIsNull;
                result$66 = false;
                if (!isNull$65) {

                    result$66 = field$64 > agg2_max;

                }

                long result$67 = -1L;
                boolean isNull$67;
                if (result$66) {

                    isNull$67 = isNull$64;
                    if (!isNull$67) {
                        result$67 = field$64;
                    }
                } else {

                    isNull$67 = agg2_maxIsNull;
                    if (!isNull$67) {
                        result$67 = agg2_max;
                    }
                }
                isNull$68 = isNull$67;
                if (!isNull$68) {
                    result$68 = result$67;
                }
            }
            isNull$69 = isNull$68;
            if (!isNull$69) {
                result$69 = result$68;
            }
        }
        agg2_max = result$69;
        ;
        agg2_maxIsNull = isNull$69;


        long result$75 = -1L;
        boolean isNull$75;
        if (isNull$70) {

            isNull$75 = agg3_minIsNull;
            if (!isNull$75) {
                result$75 = agg3_min;
            }
        } else {
            long result$74 = -1L;
            boolean isNull$74;
            if (agg3_minIsNull) {

                isNull$74 = isNull$70;
                if (!isNull$74) {
                    result$74 = field$70;
                }
            } else {
                isNull$71 = isNull$70 || agg3_minIsNull;
                result$72 = false;
                if (!isNull$71) {

                    result$72 = field$70 < agg3_min;

                }

                long result$73 = -1L;
                boolean isNull$73;
                if (result$72) {

                    isNull$73 = isNull$70;
                    if (!isNull$73) {
                        result$73 = field$70;
                    }
                } else {

                    isNull$73 = agg3_minIsNull;
                    if (!isNull$73) {
                        result$73 = agg3_min;
                    }
                }
                isNull$74 = isNull$73;
                if (!isNull$74) {
                    result$74 = result$73;
                }
            }
            isNull$75 = isNull$74;
            if (!isNull$75) {
                result$75 = result$74;
            }
        }
        agg3_min = result$75;
        ;
        agg3_minIsNull = isNull$75;


        Iterable<java.util.Map.Entry> otherEntries$85 =
                (Iterable<java.util.Map.Entry>) otherMapView$77.entries();
        if (otherEntries$85 != null) {
            for (java.util.Map.Entry entry : otherEntries$85) {
                Long distinctKey$79 = (Long) entry.getKey();
                long field$80 = -1L;
                boolean isNull$81 = true;
                if (distinctKey$79 != null) {
                    isNull$81 = false;
                    field$80 = (long) distinctKey$79;
                }
                Long otherValue = (Long) entry.getValue();
                Long thisValue = (Long) distinct_view_0.get(distinctKey$79);
                if (thisValue == null) {
                    thisValue = 0L;
                }
                boolean is_distinct_value_changed_0 = false;
                boolean is_distinct_value_empty_0 = false;


                long existed$86 = ((long) thisValue) & (1L << 0);
                if (existed$86 == 0) {  // not existed
                    long otherExisted = ((long) otherValue) & (1L << 0);
                    if (otherExisted != 0) {  // existed in other
                        is_distinct_value_changed_0 = true;
                        // do accumulate

                        long result$84 = -1L;
                        boolean isNull$84;
                        if (isNull$81) {

                            isNull$84 = agg4_countIsNull;
                            if (!isNull$84) {
                                result$84 = agg4_count;
                            }
                        } else {


                            isNull$82 = agg4_countIsNull || false;
                            result$83 = -1L;
                            if (!isNull$82) {

                                result$83 = (long) (agg4_count + ((long) 1L));

                            }

                            isNull$84 = isNull$82;
                            if (!isNull$84) {
                                result$84 = result$83;
                            }
                        }
                        agg4_count = result$84;
                        ;
                        agg4_countIsNull = isNull$84;

                    }
                }

                thisValue = ((long) thisValue) | ((long) otherValue);
                is_distinct_value_empty_0 = false;

                if (is_distinct_value_empty_0) {
                    distinct_view_0.remove(distinctKey$79);
                } else if (is_distinct_value_changed_0) { // value is not empty and is changed, do update
                    distinct_view_0.put(distinctKey$79, thisValue);
                }
            } // end foreach
        } // end otherEntries != null


    }

    @Override
    public void setAccumulators(Long ns, org.apache.flink.table.data.RowData acc)
            throws Exception {
        namespace = (Long) ns;

        long field$26;
        boolean isNull$26;
        long field$27;
        boolean isNull$27;
        long field$28;
        boolean isNull$28;
        long field$29;
        boolean isNull$29;
        long field$30;
        boolean isNull$30;
        org.apache.flink.table.data.binary.BinaryRawValueData field$31;
        boolean isNull$31;
        isNull$30 = acc.isNullAt(4);
        field$30 = -1L;
        if (!isNull$30) {
            field$30 = acc.getLong(4);
        }
        isNull$26 = acc.isNullAt(0);
        field$26 = -1L;
        if (!isNull$26) {
            field$26 = acc.getLong(0);
        }
        isNull$27 = acc.isNullAt(1);
        field$27 = -1L;
        if (!isNull$27) {
            field$27 = acc.getLong(1);
        }
        isNull$29 = acc.isNullAt(3);
        field$29 = -1L;
        if (!isNull$29) {
            field$29 = acc.getLong(3);
        }

        isNull$31 = acc.isNullAt(5);
        field$31 = null;
        if (!isNull$31) {
            field$31 = ((org.apache.flink.table.data.binary.BinaryRawValueData) acc.getRawValue(5));
        }
        distinct_view_0 = (org.apache.flink.table.api.dataview.MapView) field$31.getJavaObject();

        isNull$28 = acc.isNullAt(2);
        field$28 = -1L;
        if (!isNull$28) {
            field$28 = acc.getLong(2);
        }

        agg0_count1 = field$26;
        ;
        agg0_count1IsNull = isNull$26;


        agg1_sum = field$27;
        ;
        agg1_sumIsNull = isNull$27;


        agg2_max = field$28;
        ;
        agg2_maxIsNull = isNull$28;


        agg3_min = field$29;
        ;
        agg3_minIsNull = isNull$29;


        agg4_count = field$30;
        ;
        agg4_countIsNull = isNull$30;


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$25 = new org.apache.flink.table.data.GenericRowData(6);


        if (agg0_count1IsNull) {
            acc$25.setField(0, null);
        } else {
            acc$25.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            acc$25.setField(1, null);
        } else {
            acc$25.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            acc$25.setField(2, null);
        } else {
            acc$25.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            acc$25.setField(3, null);
        } else {
            acc$25.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            acc$25.setField(4, null);
        } else {
            acc$25.setField(4, agg4_count);
        }


        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$24 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);

        if (false) {
            acc$25.setField(5, null);
        } else {
            acc$25.setField(5, distinct_acc$24);
        }


        return acc$25;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$23 = new org.apache.flink.table.data.GenericRowData(6);


        if (false) {
            acc$23.setField(0, null);
        } else {
            acc$23.setField(0, ((long) 0L));
        }


        if (true) {
            acc$23.setField(1, null);
        } else {
            acc$23.setField(1, ((long) -1L));
        }


        if (true) {
            acc$23.setField(2, null);
        } else {
            acc$23.setField(2, ((long) -1L));
        }


        if (true) {
            acc$23.setField(3, null);
        } else {
            acc$23.setField(3, ((long) -1L));
        }


        if (false) {
            acc$23.setField(4, null);
        } else {
            acc$23.setField(4, ((long) 0L));
        }


        org.apache.flink.table.api.dataview.MapView mapview$22 = new org.apache.flink.table.api.dataview.MapView();
        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$22 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$22);

        if (false) {
            acc$23.setField(5, null);
        } else {
            acc$23.setField(5, distinct_acc$22);
        }


        return acc$23;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue(Long ns) throws Exception {
        namespace = (Long) ns;


        aggValue$87 = new org.apache.flink.table.data.GenericRowData(5);


        if (agg0_count1IsNull) {
            aggValue$87.setField(0, null);
        } else {
            aggValue$87.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            aggValue$87.setField(1, null);
        } else {
            aggValue$87.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            aggValue$87.setField(2, null);
        } else {
            aggValue$87.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            aggValue$87.setField(3, null);
        } else {
            aggValue$87.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            aggValue$87.setField(4, null);
        } else {
            aggValue$87.setField(4, agg4_count);
        }


        return aggValue$87;

    }

    @Override
    public void cleanup(Long ns) throws Exception {
        namespace = (Long) ns;


    }

    @Override
    public void close() throws Exception {

    }
}