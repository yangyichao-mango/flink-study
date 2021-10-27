//package flink.examples.sql._07.query._04_window_agg._02_cumulate_window.cumulate.global_agg;
//
//
//public final class LocalWindowAggsHandler$162
//        implements org.apache.flink.table.runtime.generated.NamespaceAggsHandleFunction<java.lang.Long> {
//
//    private transient org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.SlicedSharedSliceAssigner
//            sliceAssigner$95;
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
//    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
//    org.apache.flink.table.data.GenericRowData acc$97 = new org.apache.flink.table.data.GenericRowData(6);
//    org.apache.flink.table.data.GenericRowData acc$99 = new org.apache.flink.table.data.GenericRowData(6);
//    private org.apache.flink.table.api.dataview.MapView otherMapView$151;
//    private transient org.apache.flink.table.data.conversion.RawObjectConverter converter$152;
//    org.apache.flink.table.data.GenericRowData aggValue$161 = new org.apache.flink.table.data.GenericRowData(7);
//
//    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;
//
//    private java.lang.Long namespace;
//
//    public LocalWindowAggsHandler$162(Object[] references) throws Exception {
//        sliceAssigner$95 =
//                (((org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.SlicedSharedSliceAssigner) references[0]));
//        converter$152 = (((org.apache.flink.table.data.conversion.RawObjectConverter) references[1]));
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
//        converter$152.open(getRuntimeContext().getUserCodeClassLoader());
//
//    }
//
//    @Override
//    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {
//
//        boolean isNull$106;
//        long result$107;
//        long field$108;
//        boolean isNull$108;
//        boolean isNull$109;
//        long result$110;
//        boolean isNull$113;
//        boolean result$114;
//        boolean isNull$118;
//        boolean result$119;
//        long field$123;
//        boolean isNull$123;
//        boolean isNull$125;
//        long result$126;
//        isNull$108 = accInput.isNullAt(2);
//        field$108 = -1L;
//        if (!isNull$108) {
//            field$108 = accInput.getLong(2);
//        }
//        isNull$123 = accInput.isNullAt(3);
//        field$123 = -1L;
//        if (!isNull$123) {
//            field$123 = accInput.getLong(3);
//        }
//
//
//        isNull$106 = agg0_count1IsNull || false;
//        result$107 = -1L;
//        if (!isNull$106) {
//
//            result$107 = (long) (agg0_count1 + ((long) 1L));
//
//        }
//
//        agg0_count1 = result$107;
//        ;
//        agg0_count1IsNull = isNull$106;
//
//
//        long result$112 = -1L;
//        boolean isNull$112;
//        if (isNull$108) {
//
//            isNull$112 = agg1_sumIsNull;
//            if (!isNull$112) {
//                result$112 = agg1_sum;
//            }
//        } else {
//            long result$111 = -1L;
//            boolean isNull$111;
//            if (agg1_sumIsNull) {
//
//                isNull$111 = isNull$108;
//                if (!isNull$111) {
//                    result$111 = field$108;
//                }
//            } else {
//
//
//                isNull$109 = agg1_sumIsNull || isNull$108;
//                result$110 = -1L;
//                if (!isNull$109) {
//
//                    result$110 = (long) (agg1_sum + field$108);
//
//                }
//
//                isNull$111 = isNull$109;
//                if (!isNull$111) {
//                    result$111 = result$110;
//                }
//            }
//            isNull$112 = isNull$111;
//            if (!isNull$112) {
//                result$112 = result$111;
//            }
//        }
//        agg1_sum = result$112;
//        ;
//        agg1_sumIsNull = isNull$112;
//
//
//        long result$117 = -1L;
//        boolean isNull$117;
//        if (isNull$108) {
//
//            isNull$117 = agg2_maxIsNull;
//            if (!isNull$117) {
//                result$117 = agg2_max;
//            }
//        } else {
//            long result$116 = -1L;
//            boolean isNull$116;
//            if (agg2_maxIsNull) {
//
//                isNull$116 = isNull$108;
//                if (!isNull$116) {
//                    result$116 = field$108;
//                }
//            } else {
//                isNull$113 = isNull$108 || agg2_maxIsNull;
//                result$114 = false;
//                if (!isNull$113) {
//
//                    result$114 = field$108 > agg2_max;
//
//                }
//
//                long result$115 = -1L;
//                boolean isNull$115;
//                if (result$114) {
//
//                    isNull$115 = isNull$108;
//                    if (!isNull$115) {
//                        result$115 = field$108;
//                    }
//                } else {
//
//                    isNull$115 = agg2_maxIsNull;
//                    if (!isNull$115) {
//                        result$115 = agg2_max;
//                    }
//                }
//                isNull$116 = isNull$115;
//                if (!isNull$116) {
//                    result$116 = result$115;
//                }
//            }
//            isNull$117 = isNull$116;
//            if (!isNull$117) {
//                result$117 = result$116;
//            }
//        }
//        agg2_max = result$117;
//        ;
//        agg2_maxIsNull = isNull$117;
//
//
//        long result$122 = -1L;
//        boolean isNull$122;
//        if (isNull$108) {
//
//            isNull$122 = agg3_minIsNull;
//            if (!isNull$122) {
//                result$122 = agg3_min;
//            }
//        } else {
//            long result$121 = -1L;
//            boolean isNull$121;
//            if (agg3_minIsNull) {
//
//                isNull$121 = isNull$108;
//                if (!isNull$121) {
//                    result$121 = field$108;
//                }
//            } else {
//                isNull$118 = isNull$108 || agg3_minIsNull;
//                result$119 = false;
//                if (!isNull$118) {
//
//                    result$119 = field$108 < agg3_min;
//
//                }
//
//                long result$120 = -1L;
//                boolean isNull$120;
//                if (result$119) {
//
//                    isNull$120 = isNull$108;
//                    if (!isNull$120) {
//                        result$120 = field$108;
//                    }
//                } else {
//
//                    isNull$120 = agg3_minIsNull;
//                    if (!isNull$120) {
//                        result$120 = agg3_min;
//                    }
//                }
//                isNull$121 = isNull$120;
//                if (!isNull$121) {
//                    result$121 = result$120;
//                }
//            }
//            isNull$122 = isNull$121;
//            if (!isNull$122) {
//                result$122 = result$121;
//            }
//        }
//        agg3_min = result$122;
//        ;
//        agg3_minIsNull = isNull$122;
//
//
//        java.lang.Long distinctKey$124 = (java.lang.Long) field$123;
//        if (isNull$123) {
//            distinctKey$124 = null;
//        }
//
//        java.lang.Long value$128 = (java.lang.Long) distinct_view_0.get(distinctKey$124);
//        if (value$128 == null) {
//            value$128 = 0L;
//        }
//
//        boolean is_distinct_value_changed_0 = false;
//
//        long existed$129 = ((long) value$128) & (1L << 0);
//        if (existed$129 == 0) {  // not existed
//            value$128 = ((long) value$128) | (1L << 0);
//            is_distinct_value_changed_0 = true;
//
//            long result$127 = -1L;
//            boolean isNull$127;
//            if (isNull$123) {
//
//                isNull$127 = agg4_countIsNull;
//                if (!isNull$127) {
//                    result$127 = agg4_count;
//                }
//            } else {
//
//
//                isNull$125 = agg4_countIsNull || false;
//                result$126 = -1L;
//                if (!isNull$125) {
//
//                    result$126 = (long) (agg4_count + ((long) 1L));
//
//                }
//
//                isNull$127 = isNull$125;
//                if (!isNull$127) {
//                    result$127 = result$126;
//                }
//            }
//            agg4_count = result$127;
//            ;
//            agg4_countIsNull = isNull$127;
//
//        }
//
//        if (is_distinct_value_changed_0) {
//            distinct_view_0.put(distinctKey$124, value$128);
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
//        namespace = (java.lang.Long) ns;
//
//        long field$130;
//        boolean isNull$130;
//        boolean isNull$131;
//        long result$132;
//        long field$133;
//        boolean isNull$133;
//        boolean isNull$134;
//        long result$135;
//        long field$138;
//        boolean isNull$138;
//        boolean isNull$139;
//        boolean result$140;
//        long field$144;
//        boolean isNull$144;
//        boolean isNull$145;
//        boolean result$146;
//        org.apache.flink.table.data.binary.BinaryRawValueData field$150;
//        boolean isNull$150;
//        boolean isNull$156;
//        long result$157;
//        isNull$130 = otherAcc.isNullAt(2);
//        field$130 = -1L;
//        if (!isNull$130) {
//            field$130 = otherAcc.getLong(2);
//        }
//        isNull$133 = otherAcc.isNullAt(3);
//        field$133 = -1L;
//        if (!isNull$133) {
//            field$133 = otherAcc.getLong(3);
//        }
//
//        isNull$150 = otherAcc.isNullAt(7);
//        field$150 = null;
//        if (!isNull$150) {
//            field$150 = ((org.apache.flink.table.data.binary.BinaryRawValueData) otherAcc.getRawValue(7));
//        }
//        otherMapView$151 = null;
//        if (!isNull$150) {
//            otherMapView$151 =
//                    (org.apache.flink.table.api.dataview.MapView) converter$152
//                            .toExternal((org.apache.flink.table.data.binary.BinaryRawValueData) field$150);
//        }
//
//        isNull$144 = otherAcc.isNullAt(5);
//        field$144 = -1L;
//        if (!isNull$144) {
//            field$144 = otherAcc.getLong(5);
//        }
//        isNull$138 = otherAcc.isNullAt(4);
//        field$138 = -1L;
//        if (!isNull$138) {
//            field$138 = otherAcc.getLong(4);
//        }
//
//
//        isNull$131 = agg0_count1IsNull || isNull$130;
//        result$132 = -1L;
//        if (!isNull$131) {
//
//            result$132 = (long) (agg0_count1 + field$130);
//
//        }
//
//        agg0_count1 = result$132;
//        ;
//        agg0_count1IsNull = isNull$131;
//
//
//        long result$137 = -1L;
//        boolean isNull$137;
//        if (isNull$133) {
//
//            isNull$137 = agg1_sumIsNull;
//            if (!isNull$137) {
//                result$137 = agg1_sum;
//            }
//        } else {
//            long result$136 = -1L;
//            boolean isNull$136;
//            if (agg1_sumIsNull) {
//
//                isNull$136 = isNull$133;
//                if (!isNull$136) {
//                    result$136 = field$133;
//                }
//            } else {
//
//
//                isNull$134 = agg1_sumIsNull || isNull$133;
//                result$135 = -1L;
//                if (!isNull$134) {
//
//                    result$135 = (long) (agg1_sum + field$133);
//
//                }
//
//                isNull$136 = isNull$134;
//                if (!isNull$136) {
//                    result$136 = result$135;
//                }
//            }
//            isNull$137 = isNull$136;
//            if (!isNull$137) {
//                result$137 = result$136;
//            }
//        }
//        agg1_sum = result$137;
//        ;
//        agg1_sumIsNull = isNull$137;
//
//
//        long result$143 = -1L;
//        boolean isNull$143;
//        if (isNull$138) {
//
//            isNull$143 = agg2_maxIsNull;
//            if (!isNull$143) {
//                result$143 = agg2_max;
//            }
//        } else {
//            long result$142 = -1L;
//            boolean isNull$142;
//            if (agg2_maxIsNull) {
//
//                isNull$142 = isNull$138;
//                if (!isNull$142) {
//                    result$142 = field$138;
//                }
//            } else {
//                isNull$139 = isNull$138 || agg2_maxIsNull;
//                result$140 = false;
//                if (!isNull$139) {
//
//                    result$140 = field$138 > agg2_max;
//
//                }
//
//                long result$141 = -1L;
//                boolean isNull$141;
//                if (result$140) {
//
//                    isNull$141 = isNull$138;
//                    if (!isNull$141) {
//                        result$141 = field$138;
//                    }
//                } else {
//
//                    isNull$141 = agg2_maxIsNull;
//                    if (!isNull$141) {
//                        result$141 = agg2_max;
//                    }
//                }
//                isNull$142 = isNull$141;
//                if (!isNull$142) {
//                    result$142 = result$141;
//                }
//            }
//            isNull$143 = isNull$142;
//            if (!isNull$143) {
//                result$143 = result$142;
//            }
//        }
//        agg2_max = result$143;
//        ;
//        agg2_maxIsNull = isNull$143;
//
//
//        long result$149 = -1L;
//        boolean isNull$149;
//        if (isNull$144) {
//
//            isNull$149 = agg3_minIsNull;
//            if (!isNull$149) {
//                result$149 = agg3_min;
//            }
//        } else {
//            long result$148 = -1L;
//            boolean isNull$148;
//            if (agg3_minIsNull) {
//
//                isNull$148 = isNull$144;
//                if (!isNull$148) {
//                    result$148 = field$144;
//                }
//            } else {
//                isNull$145 = isNull$144 || agg3_minIsNull;
//                result$146 = false;
//                if (!isNull$145) {
//
//                    result$146 = field$144 < agg3_min;
//
//                }
//
//                long result$147 = -1L;
//                boolean isNull$147;
//                if (result$146) {
//
//                    isNull$147 = isNull$144;
//                    if (!isNull$147) {
//                        result$147 = field$144;
//                    }
//                } else {
//
//                    isNull$147 = agg3_minIsNull;
//                    if (!isNull$147) {
//                        result$147 = agg3_min;
//                    }
//                }
//                isNull$148 = isNull$147;
//                if (!isNull$148) {
//                    result$148 = result$147;
//                }
//            }
//            isNull$149 = isNull$148;
//            if (!isNull$149) {
//                result$149 = result$148;
//            }
//        }
//        agg3_min = result$149;
//        ;
//        agg3_minIsNull = isNull$149;
//
//
//        java.lang.Iterable<java.util.Map.Entry> otherEntries$159 =
//                (java.lang.Iterable<java.util.Map.Entry>) otherMapView$151.entries();
//        if (otherEntries$159 != null) {
//            for (java.util.Map.Entry entry : otherEntries$159) {
//                java.lang.Long distinctKey$153 = (java.lang.Long) entry.getKey();
//                long field$154 = -1L;
//                boolean isNull$155 = true;
//                if (distinctKey$153 != null) {
//                    isNull$155 = false;
//                    field$154 = (long) distinctKey$153;
//                }
//                java.lang.Long otherValue = (java.lang.Long) entry.getValue();
//                java.lang.Long thisValue = (java.lang.Long) distinct_view_0.get(distinctKey$153);
//                if (thisValue == null) {
//                    thisValue = 0L;
//                }
//                boolean is_distinct_value_changed_0 = false;
//                boolean is_distinct_value_empty_0 = false;
//
//
//                long existed$160 = ((long) thisValue) & (1L << 0);
//                if (existed$160 == 0) {  // not existed
//                    long otherExisted = ((long) otherValue) & (1L << 0);
//                    if (otherExisted != 0) {  // existed in other
//                        is_distinct_value_changed_0 = true;
//                        // do accumulate
//
//                        long result$158 = -1L;
//                        boolean isNull$158;
//                        if (isNull$155) {
//
//                            isNull$158 = agg4_countIsNull;
//                            if (!isNull$158) {
//                                result$158 = agg4_count;
//                            }
//                        } else {
//
//
//                            isNull$156 = agg4_countIsNull || false;
//                            result$157 = -1L;
//                            if (!isNull$156) {
//
//                                result$157 = (long) (agg4_count + ((long) 1L));
//
//                            }
//
//                            isNull$158 = isNull$156;
//                            if (!isNull$158) {
//                                result$158 = result$157;
//                            }
//                        }
//                        agg4_count = result$158;
//                        ;
//                        agg4_countIsNull = isNull$158;
//
//                    }
//                }
//
//                thisValue = ((long) thisValue) | ((long) otherValue);
//                is_distinct_value_empty_0 = false;
//
//                if (is_distinct_value_empty_0) {
//                    distinct_view_0.remove(distinctKey$153);
//                } else if (is_distinct_value_changed_0) { // value is not empty and is changed, do update
//                    distinct_view_0.put(distinctKey$153, thisValue);
//                }
//            } // end foreach
//        } // end otherEntries != null
//
//
//    }
//
//    @Override
//    public void setAccumulators(Object ns, org.apache.flink.table.data.RowData acc)
//            throws Exception {
//        namespace = (java.lang.Long) ns;
//
//        long field$100;
//        boolean isNull$100;
//        long field$101;
//        boolean isNull$101;
//        long field$102;
//        boolean isNull$102;
//        long field$103;
//        boolean isNull$103;
//        long field$104;
//        boolean isNull$104;
//        org.apache.flink.table.data.binary.BinaryRawValueData field$105;
//        boolean isNull$105;
//        isNull$104 = acc.isNullAt(4);
//        field$104 = -1L;
//        if (!isNull$104) {
//            field$104 = acc.getLong(4);
//        }
//        isNull$100 = acc.isNullAt(0);
//        field$100 = -1L;
//        if (!isNull$100) {
//            field$100 = acc.getLong(0);
//        }
//        isNull$101 = acc.isNullAt(1);
//        field$101 = -1L;
//        if (!isNull$101) {
//            field$101 = acc.getLong(1);
//        }
//        isNull$103 = acc.isNullAt(3);
//        field$103 = -1L;
//        if (!isNull$103) {
//            field$103 = acc.getLong(3);
//        }
//
//        isNull$105 = acc.isNullAt(5);
//        field$105 = null;
//        if (!isNull$105) {
//            field$105 = ((org.apache.flink.table.data.binary.BinaryRawValueData) acc.getRawValue(5));
//        }
//        distinct_view_0 = (org.apache.flink.table.api.dataview.MapView) field$105.getJavaObject();
//
//        isNull$102 = acc.isNullAt(2);
//        field$102 = -1L;
//        if (!isNull$102) {
//            field$102 = acc.getLong(2);
//        }
//
//        agg0_count1 = field$100;
//        ;
//        agg0_count1IsNull = isNull$100;
//
//
//        agg1_sum = field$101;
//        ;
//        agg1_sumIsNull = isNull$101;
//
//
//        agg2_max = field$102;
//        ;
//        agg2_maxIsNull = isNull$102;
//
//
//        agg3_min = field$103;
//        ;
//        agg3_minIsNull = isNull$103;
//
//
//        agg4_count = field$104;
//        ;
//        agg4_countIsNull = isNull$104;
//
//
//    }
//
//    @Override
//    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {
//
//
//        acc$99 = new org.apache.flink.table.data.GenericRowData(6);
//
//
//        if (agg0_count1IsNull) {
//            acc$99.setField(0, null);
//        } else {
//            acc$99.setField(0, agg0_count1);
//        }
//
//
//        if (agg1_sumIsNull) {
//            acc$99.setField(1, null);
//        } else {
//            acc$99.setField(1, agg1_sum);
//        }
//
//
//        if (agg2_maxIsNull) {
//            acc$99.setField(2, null);
//        } else {
//            acc$99.setField(2, agg2_max);
//        }
//
//
//        if (agg3_minIsNull) {
//            acc$99.setField(3, null);
//        } else {
//            acc$99.setField(3, agg3_min);
//        }
//
//
//        if (agg4_countIsNull) {
//            acc$99.setField(4, null);
//        } else {
//            acc$99.setField(4, agg4_count);
//        }
//
//
//        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$98 =
//                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);
//
//        if (false) {
//            acc$99.setField(5, null);
//        } else {
//            acc$99.setField(5, distinct_acc$98);
//        }
//
//
//        return acc$99;
//
//    }
//
//    @Override
//    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {
//
//
//        acc$97 = new org.apache.flink.table.data.GenericRowData(6);
//
//
//        if (false) {
//            acc$97.setField(0, null);
//        } else {
//            acc$97.setField(0, ((long) 0L));
//        }
//
//
//        if (true) {
//            acc$97.setField(1, null);
//        } else {
//            acc$97.setField(1, ((long) -1L));
//        }
//
//
//        if (true) {
//            acc$97.setField(2, null);
//        } else {
//            acc$97.setField(2, ((long) -1L));
//        }
//
//
//        if (true) {
//            acc$97.setField(3, null);
//        } else {
//            acc$97.setField(3, ((long) -1L));
//        }
//
//
//        if (false) {
//            acc$97.setField(4, null);
//        } else {
//            acc$97.setField(4, ((long) 0L));
//        }
//
//
//        org.apache.flink.table.api.dataview.MapView mapview$96 = new org.apache.flink.table.api.dataview.MapView();
//        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$96 =
//                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$96);
//
//        if (false) {
//            acc$97.setField(5, null);
//        } else {
//            acc$97.setField(5, distinct_acc$96);
//        }
//
//
//        return acc$97;
//
//    }
//
//    @Override
//    public org.apache.flink.table.data.RowData getValue(Object ns) throws Exception {
//        namespace = (java.lang.Long) ns;
//
//
//        aggValue$161 = new org.apache.flink.table.data.GenericRowData(7);
//
//
//        if (agg0_count1IsNull) {
//            aggValue$161.setField(0, null);
//        } else {
//            aggValue$161.setField(0, agg0_count1);
//        }
//
//
//        if (agg1_sumIsNull) {
//            aggValue$161.setField(1, null);
//        } else {
//            aggValue$161.setField(1, agg1_sum);
//        }
//
//
//        if (agg2_maxIsNull) {
//            aggValue$161.setField(2, null);
//        } else {
//            aggValue$161.setField(2, agg2_max);
//        }
//
//
//        if (agg3_minIsNull) {
//            aggValue$161.setField(3, null);
//        } else {
//            aggValue$161.setField(3, agg3_min);
//        }
//
//
//        if (agg4_countIsNull) {
//            aggValue$161.setField(4, null);
//        } else {
//            aggValue$161.setField(4, agg4_count);
//        }
//
//
//        if (false) {
//            aggValue$161.setField(5, null);
//        } else {
//            aggValue$161.setField(5, org.apache.flink.table.data.TimestampData
//                    .fromEpochMillis(sliceAssigner$95.getWindowStart(namespace)));
//        }
//
//
//        if (false) {
//            aggValue$161.setField(6, null);
//        } else {
//            aggValue$161.setField(6, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace));
//        }
//
//
//        return aggValue$161;
//
//    }
//
//    @Override
//    public void cleanup(Object ns) throws Exception {
//        namespace = (java.lang.Long) ns;
//
//
//    }
//
//    @Override
//    public void close() throws Exception {
//
//    }
//}