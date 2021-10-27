//package flink.examples.sql._07.query._04_window_agg._02_cumulate_window.earlyfire;
//
///**
// * {@link org.apache.flink.table.runtime.operators.window.AggregateWindowOperator}
// */
//public final class GroupingWindowAggsHandler$57
//        implements
//        org.apache.flink.table.runtime.generated.NamespaceAggsHandleFunction<org.apache.flink.table.runtime.operators.window.TimeWindow> {
//
//    long agg0_count1;
//    boolean agg0_count1IsNull;
//    long agg1_sum;
//    boolean agg1_sumIsNull;
//    long agg2_max;
//    boolean agg2_maxIsNull;
//    long agg3_min;
//    boolean agg3_minIsNull;
//    long agg4_count;
//    boolean agg4_countIsNull;
//    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$20;
//    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$21;
//    private org.apache.flink.table.runtime.dataview.StateMapView distinctAcc_0_dataview;
//    private org.apache.flink.table.data.binary.BinaryRawValueData distinctAcc_0_dataview_raw_value;
//    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
//    org.apache.flink.table.data.GenericRowData acc$23 = new org.apache.flink.table.data.GenericRowData(6);
//    org.apache.flink.table.data.GenericRowData acc$25 = new org.apache.flink.table.data.GenericRowData(6);
//    org.apache.flink.table.data.GenericRowData aggValue$56 = new org.apache.flink.table.data.GenericRowData(9);
//
//    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;
//
//    private org.apache.flink.table.runtime.operators.window.TimeWindow namespace;
//
//    public GroupingWindowAggsHandler$57(Object[] references) throws Exception {
//        externalSerializer$20 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[0]));
//        externalSerializer$21 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[1]));
//    }
//
//    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
//        return store.getRuntimeContext();
//    }
//
//    @Override
//    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
//        this.store = store;
//
//        distinctAcc_0_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
//                .getStateMapView("distinctAcc_0", true, externalSerializer$20, externalSerializer$21);
//        distinctAcc_0_dataview_raw_value =
//                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinctAcc_0_dataview);
//
//        distinct_view_0 = distinctAcc_0_dataview;
//    }
//
//    @Override
//    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {
//
//        boolean isNull$32;
//        long result$33;
//        long field$34;
//        boolean isNull$34;
//        boolean isNull$35;
//        long result$36;
//        boolean isNull$39;
//        boolean result$40;
//        boolean isNull$44;
//        boolean result$45;
//        long field$49;
//        boolean isNull$49;
//        boolean isNull$51;
//        long result$52;
//        isNull$49 = accInput.isNullAt(4);
//        field$49 = -1L;
//        if (!isNull$49) {
//            field$49 = accInput.getLong(4);
//        }
//        isNull$34 = accInput.isNullAt(3);
//        field$34 = -1L;
//        if (!isNull$34) {
//            field$34 = accInput.getLong(3);
//        }
//
//
//        isNull$32 = agg0_count1IsNull || false;
//        result$33 = -1L;
//        if (!isNull$32) {
//
//            result$33 = (long) (agg0_count1 + ((long) 1L));
//
//        }
//
//        agg0_count1 = result$33;
//        ;
//        agg0_count1IsNull = isNull$32;
//
//
//        long result$38 = -1L;
//        boolean isNull$38;
//        if (isNull$34) {
//
//            isNull$38 = agg1_sumIsNull;
//            if (!isNull$38) {
//                result$38 = agg1_sum;
//            }
//        } else {
//            long result$37 = -1L;
//            boolean isNull$37;
//            if (agg1_sumIsNull) {
//
//                isNull$37 = isNull$34;
//                if (!isNull$37) {
//                    result$37 = field$34;
//                }
//            } else {
//
//
//                isNull$35 = agg1_sumIsNull || isNull$34;
//                result$36 = -1L;
//                if (!isNull$35) {
//
//                    result$36 = (long) (agg1_sum + field$34);
//
//                }
//
//                isNull$37 = isNull$35;
//                if (!isNull$37) {
//                    result$37 = result$36;
//                }
//            }
//            isNull$38 = isNull$37;
//            if (!isNull$38) {
//                result$38 = result$37;
//            }
//        }
//        agg1_sum = result$38;
//        ;
//        agg1_sumIsNull = isNull$38;
//
//
//        long result$43 = -1L;
//        boolean isNull$43;
//        if (isNull$34) {
//
//            isNull$43 = agg2_maxIsNull;
//            if (!isNull$43) {
//                result$43 = agg2_max;
//            }
//        } else {
//            long result$42 = -1L;
//            boolean isNull$42;
//            if (agg2_maxIsNull) {
//
//                isNull$42 = isNull$34;
//                if (!isNull$42) {
//                    result$42 = field$34;
//                }
//            } else {
//                isNull$39 = isNull$34 || agg2_maxIsNull;
//                result$40 = false;
//                if (!isNull$39) {
//
//                    result$40 = field$34 > agg2_max;
//
//                }
//
//                long result$41 = -1L;
//                boolean isNull$41;
//                if (result$40) {
//
//                    isNull$41 = isNull$34;
//                    if (!isNull$41) {
//                        result$41 = field$34;
//                    }
//                } else {
//
//                    isNull$41 = agg2_maxIsNull;
//                    if (!isNull$41) {
//                        result$41 = agg2_max;
//                    }
//                }
//                isNull$42 = isNull$41;
//                if (!isNull$42) {
//                    result$42 = result$41;
//                }
//            }
//            isNull$43 = isNull$42;
//            if (!isNull$43) {
//                result$43 = result$42;
//            }
//        }
//        agg2_max = result$43;
//        ;
//        agg2_maxIsNull = isNull$43;
//
//
//        long result$48 = -1L;
//        boolean isNull$48;
//        if (isNull$34) {
//
//            isNull$48 = agg3_minIsNull;
//            if (!isNull$48) {
//                result$48 = agg3_min;
//            }
//        } else {
//            long result$47 = -1L;
//            boolean isNull$47;
//            if (agg3_minIsNull) {
//
//                isNull$47 = isNull$34;
//                if (!isNull$47) {
//                    result$47 = field$34;
//                }
//            } else {
//                isNull$44 = isNull$34 || agg3_minIsNull;
//                result$45 = false;
//                if (!isNull$44) {
//
//                    result$45 = field$34 < agg3_min;
//
//                }
//
//                long result$46 = -1L;
//                boolean isNull$46;
//                if (result$45) {
//
//                    isNull$46 = isNull$34;
//                    if (!isNull$46) {
//                        result$46 = field$34;
//                    }
//                } else {
//
//                    isNull$46 = agg3_minIsNull;
//                    if (!isNull$46) {
//                        result$46 = agg3_min;
//                    }
//                }
//                isNull$47 = isNull$46;
//                if (!isNull$47) {
//                    result$47 = result$46;
//                }
//            }
//            isNull$48 = isNull$47;
//            if (!isNull$48) {
//                result$48 = result$47;
//            }
//        }
//        agg3_min = result$48;
//        ;
//        agg3_minIsNull = isNull$48;
//
//
//        java.lang.Long distinctKey$50 = (java.lang.Long) field$49;
//        if (isNull$49) {
//            distinctKey$50 = null;
//        }
//
//        java.lang.Long value$54 = (java.lang.Long) distinct_view_0.get(distinctKey$50);
//        if (value$54 == null) {
//            value$54 = 0L;
//        }
//
//        boolean is_distinct_value_changed_0 = false;
//
//        long existed$55 = ((long) value$54) & (1L << 0);
//        if (existed$55 == 0) {  // not existed
//            value$54 = ((long) value$54) | (1L << 0);
//            is_distinct_value_changed_0 = true;
//
//            long result$53 = -1L;
//            boolean isNull$53;
//            if (isNull$49) {
//
//                isNull$53 = agg4_countIsNull;
//                if (!isNull$53) {
//                    result$53 = agg4_count;
//                }
//            } else {
//
//
//                isNull$51 = agg4_countIsNull || false;
//                result$52 = -1L;
//                if (!isNull$51) {
//
//                    result$52 = (long) (agg4_count + ((long) 1L));
//
//                }
//
//                isNull$53 = isNull$51;
//                if (!isNull$53) {
//                    result$53 = result$52;
//                }
//            }
//            agg4_count = result$53;
//            ;
//            agg4_countIsNull = isNull$53;
//
//        }
//
//        if (is_distinct_value_changed_0) {
//            distinct_view_0.put(distinctKey$50, value$54);
//        }
//
//
//    }
//
//    @Override
//    public void retract(org.apache.flink.table.data.RowData retractInput) throws Exception {
//
//        throw new java.lang.RuntimeException(
//                "This function not require retract method, but the retract method is called.");
//
//    }
//
//    @Override
//    public void merge(Object ns, org.apache.flink.table.data.RowData otherAcc) throws Exception {
//        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;
//
//        throw new java.lang.RuntimeException("This function not require merge method, but the merge method is called.");
//
//    }
//
//    @Override
//    public void setAccumulators(Object ns, org.apache.flink.table.data.RowData acc)
//            throws Exception {
//        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;
//
//        long field$26;
//        boolean isNull$26;
//        long field$27;
//        boolean isNull$27;
//        long field$28;
//        boolean isNull$28;
//        long field$29;
//        boolean isNull$29;
//        long field$30;
//        boolean isNull$30;
//        org.apache.flink.table.data.binary.BinaryRawValueData field$31;
//        boolean isNull$31;
//        isNull$30 = acc.isNullAt(4);
//        field$30 = -1L;
//        if (!isNull$30) {
//            field$30 = acc.getLong(4);
//        }
//        isNull$26 = acc.isNullAt(0);
//        field$26 = -1L;
//        if (!isNull$26) {
//            field$26 = acc.getLong(0);
//        }
//        isNull$27 = acc.isNullAt(1);
//        field$27 = -1L;
//        if (!isNull$27) {
//            field$27 = acc.getLong(1);
//        }
//        isNull$29 = acc.isNullAt(3);
//        field$29 = -1L;
//        if (!isNull$29) {
//            field$29 = acc.getLong(3);
//        }
//
//        // when namespace is null, the dataview is used in heap, no key and namespace set
//        if (namespace != null) {
//            distinctAcc_0_dataview.setCurrentNamespace(namespace);
//            distinct_view_0 = distinctAcc_0_dataview;
//        } else {
//            isNull$31 = acc.isNullAt(5);
//            field$31 = null;
//            if (!isNull$31) {
//                field$31 = ((org.apache.flink.table.data.binary.BinaryRawValueData) acc.getRawValue(5));
//            }
//            distinct_view_0 = (org.apache.flink.table.api.dataview.MapView) field$31.getJavaObject();
//        }
//
//        isNull$28 = acc.isNullAt(2);
//        field$28 = -1L;
//        if (!isNull$28) {
//            field$28 = acc.getLong(2);
//        }
//
//        agg0_count1 = field$26;
//        ;
//        agg0_count1IsNull = isNull$26;
//
//
//        agg1_sum = field$27;
//        ;
//        agg1_sumIsNull = isNull$27;
//
//
//        agg2_max = field$28;
//        ;
//        agg2_maxIsNull = isNull$28;
//
//
//        agg3_min = field$29;
//        ;
//        agg3_minIsNull = isNull$29;
//
//
//        agg4_count = field$30;
//        ;
//        agg4_countIsNull = isNull$30;
//
//
//    }
//
//    @Override
//    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {
//
//
//        acc$25 = new org.apache.flink.table.data.GenericRowData(6);
//
//
//        if (agg0_count1IsNull) {
//            acc$25.setField(0, null);
//        } else {
//            acc$25.setField(0, agg0_count1);
//        }
//
//
//        if (agg1_sumIsNull) {
//            acc$25.setField(1, null);
//        } else {
//            acc$25.setField(1, agg1_sum);
//        }
//
//
//        if (agg2_maxIsNull) {
//            acc$25.setField(2, null);
//        } else {
//            acc$25.setField(2, agg2_max);
//        }
//
//
//        if (agg3_minIsNull) {
//            acc$25.setField(3, null);
//        } else {
//            acc$25.setField(3, agg3_min);
//        }
//
//
//        if (agg4_countIsNull) {
//            acc$25.setField(4, null);
//        } else {
//            acc$25.setField(4, agg4_count);
//        }
//
//
//        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$24 =
//                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);
//
//        if (false) {
//            acc$25.setField(5, null);
//        } else {
//            acc$25.setField(5, distinct_acc$24);
//        }
//
//
//        return acc$25;
//
//    }
//
//    @Override
//    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {
//
//
//        acc$23 = new org.apache.flink.table.data.GenericRowData(6);
//
//
//        if (false) {
//            acc$23.setField(0, null);
//        } else {
//            acc$23.setField(0, ((long) 0L));
//        }
//
//
//        if (true) {
//            acc$23.setField(1, null);
//        } else {
//            acc$23.setField(1, ((long) -1L));
//        }
//
//
//        if (true) {
//            acc$23.setField(2, null);
//        } else {
//            acc$23.setField(2, ((long) -1L));
//        }
//
//
//        if (true) {
//            acc$23.setField(3, null);
//        } else {
//            acc$23.setField(3, ((long) -1L));
//        }
//
//
//        if (false) {
//            acc$23.setField(4, null);
//        } else {
//            acc$23.setField(4, ((long) 0L));
//        }
//
//
//        org.apache.flink.table.api.dataview.MapView mapview$22 = new org.apache.flink.table.api.dataview.MapView();
//        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$22 =
//                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$22);
//
//        if (false) {
//            acc$23.setField(5, null);
//        } else {
//            acc$23.setField(5, distinct_acc$22);
//        }
//
//
//        return acc$23;
//
//    }
//
//    @Override
//    public org.apache.flink.table.data.RowData getValue(Object ns) throws Exception {
//        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;
//
//
//        aggValue$56 = new org.apache.flink.table.data.GenericRowData(9);
//
//
//        if (agg0_count1IsNull) {
//            aggValue$56.setField(0, null);
//        } else {
//            aggValue$56.setField(0, agg0_count1);
//        }
//
//
//        if (agg1_sumIsNull) {
//            aggValue$56.setField(1, null);
//        } else {
//            aggValue$56.setField(1, agg1_sum);
//        }
//
//
//        if (agg2_maxIsNull) {
//            aggValue$56.setField(2, null);
//        } else {
//            aggValue$56.setField(2, agg2_max);
//        }
//
//
//        if (agg3_minIsNull) {
//            aggValue$56.setField(3, null);
//        } else {
//            aggValue$56.setField(3, agg3_min);
//        }
//
//
//        if (agg4_countIsNull) {
//            aggValue$56.setField(4, null);
//        } else {
//            aggValue$56.setField(4, agg4_count);
//        }
//
//
//        if (false) {
//            aggValue$56.setField(5, null);
//        } else {
//            aggValue$56.setField(5, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace.getStart()));
//        }
//
//
//        if (false) {
//            aggValue$56.setField(6, null);
//        } else {
//            aggValue$56.setField(6, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace.getEnd()));
//        }
//
//
//        if (false) {
//            aggValue$56.setField(7, null);
//        } else {
//            aggValue$56.setField(7,
//                    org.apache.flink.table.data.TimestampData.fromEpochMillis(
//                            namespace.getEnd() - 1)
//            );
//        }
//
//
//        if (true) {
//            aggValue$56.setField(8, null);
//        } else {
//            aggValue$56.setField(8, org.apache.flink.table.data.TimestampData.fromEpochMillis(-1L));
//        }
//
//
//        return aggValue$56;
//
//    }
//
//    @Override
//    public void cleanup(Object ns) throws Exception {
//        namespace = (org.apache.flink.table.runtime.operators.window.TimeWindow) ns;
//
//        distinctAcc_0_dataview.setCurrentNamespace(namespace);
//        distinctAcc_0_dataview.clear();
//
//
//    }
//
//    @Override
//    public void close() throws Exception {
//
//    }
//}