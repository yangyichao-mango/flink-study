package flink.examples.sql._07.query._04_window._04_TumbleWindowTest_GlobalAgg;


public final class _04_TumbleWindowTest_StateWindowAggsHandler$300
        implements org.apache.flink.table.runtime.generated.NamespaceAggsHandleFunction<Long> {

    private transient org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.SlicedUnsharedSliceAssigner
            sliceAssigner$233;
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
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$234;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$235;
    private org.apache.flink.table.runtime.dataview.StateMapView distinctAcc_0_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData distinctAcc_0_dataview_raw_value;
    private org.apache.flink.table.runtime.dataview.StateMapView distinctAcc_0_dataview_backup;
    private org.apache.flink.table.data.binary.BinaryRawValueData distinctAcc_0_dataview_backup_raw_value;
    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
    private org.apache.flink.table.api.dataview.MapView distinct_backup_view_0;
    org.apache.flink.table.data.GenericRowData acc$237 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData acc$239 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData aggValue$299 = new org.apache.flink.table.data.GenericRowData(7);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    private Long namespace;

    public _04_TumbleWindowTest_StateWindowAggsHandler$300(Object[] references) throws Exception {
        sliceAssigner$233 =
                (((org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.SlicedUnsharedSliceAssigner) references[0]));
        externalSerializer$234 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[1]));
        externalSerializer$235 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[2]));
    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

        distinctAcc_0_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("distinctAcc_0", true, externalSerializer$234, externalSerializer$235);
        distinctAcc_0_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinctAcc_0_dataview);


        distinctAcc_0_dataview_backup = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("distinctAcc_0", true, externalSerializer$234, externalSerializer$235);
        distinctAcc_0_dataview_backup_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinctAcc_0_dataview_backup);

        distinct_view_0 = distinctAcc_0_dataview;
        distinct_backup_view_0 = distinctAcc_0_dataview_backup;
    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        boolean isNull$246;
        long result$247;
        long field$248;
        boolean isNull$248;
        boolean isNull$249;
        long result$250;
        boolean isNull$253;
        boolean result$254;
        boolean isNull$258;
        boolean result$259;
        long field$263;
        boolean isNull$263;
        boolean isNull$265;
        long result$266;
        isNull$248 = accInput.isNullAt(2);
        field$248 = -1L;
        if (!isNull$248) {
            field$248 = accInput.getLong(2);
        }
        isNull$263 = accInput.isNullAt(3);
        field$263 = -1L;
        if (!isNull$263) {
            field$263 = accInput.getLong(3);
        }


        isNull$246 = agg0_count1IsNull || false;
        result$247 = -1L;
        if (!isNull$246) {

            result$247 = (long) (agg0_count1 + ((long) 1L));

        }

        agg0_count1 = result$247;
        ;
        agg0_count1IsNull = isNull$246;


        long result$252 = -1L;
        boolean isNull$252;
        if (isNull$248) {

            isNull$252 = agg1_sumIsNull;
            if (!isNull$252) {
                result$252 = agg1_sum;
            }
        } else {
            long result$251 = -1L;
            boolean isNull$251;
            if (agg1_sumIsNull) {

                isNull$251 = isNull$248;
                if (!isNull$251) {
                    result$251 = field$248;
                }
            } else {


                isNull$249 = agg1_sumIsNull || isNull$248;
                result$250 = -1L;
                if (!isNull$249) {

                    result$250 = (long) (agg1_sum + field$248);

                }

                isNull$251 = isNull$249;
                if (!isNull$251) {
                    result$251 = result$250;
                }
            }
            isNull$252 = isNull$251;
            if (!isNull$252) {
                result$252 = result$251;
            }
        }
        agg1_sum = result$252;
        ;
        agg1_sumIsNull = isNull$252;


        long result$257 = -1L;
        boolean isNull$257;
        if (isNull$248) {

            isNull$257 = agg2_maxIsNull;
            if (!isNull$257) {
                result$257 = agg2_max;
            }
        } else {
            long result$256 = -1L;
            boolean isNull$256;
            if (agg2_maxIsNull) {

                isNull$256 = isNull$248;
                if (!isNull$256) {
                    result$256 = field$248;
                }
            } else {
                isNull$253 = isNull$248 || agg2_maxIsNull;
                result$254 = false;
                if (!isNull$253) {

                    result$254 = field$248 > agg2_max;

                }

                long result$255 = -1L;
                boolean isNull$255;
                if (result$254) {

                    isNull$255 = isNull$248;
                    if (!isNull$255) {
                        result$255 = field$248;
                    }
                } else {

                    isNull$255 = agg2_maxIsNull;
                    if (!isNull$255) {
                        result$255 = agg2_max;
                    }
                }
                isNull$256 = isNull$255;
                if (!isNull$256) {
                    result$256 = result$255;
                }
            }
            isNull$257 = isNull$256;
            if (!isNull$257) {
                result$257 = result$256;
            }
        }
        agg2_max = result$257;
        ;
        agg2_maxIsNull = isNull$257;


        long result$262 = -1L;
        boolean isNull$262;
        if (isNull$248) {

            isNull$262 = agg3_minIsNull;
            if (!isNull$262) {
                result$262 = agg3_min;
            }
        } else {
            long result$261 = -1L;
            boolean isNull$261;
            if (agg3_minIsNull) {

                isNull$261 = isNull$248;
                if (!isNull$261) {
                    result$261 = field$248;
                }
            } else {
                isNull$258 = isNull$248 || agg3_minIsNull;
                result$259 = false;
                if (!isNull$258) {

                    result$259 = field$248 < agg3_min;

                }

                long result$260 = -1L;
                boolean isNull$260;
                if (result$259) {

                    isNull$260 = isNull$248;
                    if (!isNull$260) {
                        result$260 = field$248;
                    }
                } else {

                    isNull$260 = agg3_minIsNull;
                    if (!isNull$260) {
                        result$260 = agg3_min;
                    }
                }
                isNull$261 = isNull$260;
                if (!isNull$261) {
                    result$261 = result$260;
                }
            }
            isNull$262 = isNull$261;
            if (!isNull$262) {
                result$262 = result$261;
            }
        }
        agg3_min = result$262;
        ;
        agg3_minIsNull = isNull$262;


        Long distinctKey$264 = (Long) field$263;
        if (isNull$263) {
            distinctKey$264 = null;
        }

        Long value$268 = (Long) distinct_view_0.get(distinctKey$264);
        if (value$268 == null) {
            value$268 = 0L;
        }

        boolean is_distinct_value_changed_0 = false;

        long existed$269 = ((long) value$268) & (1L << 0);
        if (existed$269 == 0) {  // not existed
            value$268 = ((long) value$268) | (1L << 0);
            is_distinct_value_changed_0 = true;

            long result$267 = -1L;
            boolean isNull$267;
            if (isNull$263) {

                isNull$267 = agg4_countIsNull;
                if (!isNull$267) {
                    result$267 = agg4_count;
                }
            } else {


                isNull$265 = agg4_countIsNull || false;
                result$266 = -1L;
                if (!isNull$265) {

                    result$266 = (long) (agg4_count + ((long) 1L));

                }

                isNull$267 = isNull$265;
                if (!isNull$267) {
                    result$267 = result$266;
                }
            }
            agg4_count = result$267;
            ;
            agg4_countIsNull = isNull$267;

        }

        if (is_distinct_value_changed_0) {
            distinct_view_0.put(distinctKey$264, value$268);
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

        long field$270;
        boolean isNull$270;
        boolean isNull$271;
        long result$272;
        long field$273;
        boolean isNull$273;
        boolean isNull$274;
        long result$275;
        long field$278;
        boolean isNull$278;
        boolean isNull$279;
        boolean result$280;
        long field$284;
        boolean isNull$284;
        boolean isNull$285;
        boolean result$286;
        org.apache.flink.table.data.binary.BinaryRawValueData field$290;
        boolean isNull$290;
        boolean isNull$294;
        long result$295;
        isNull$278 = otherAcc.isNullAt(2);
        field$278 = -1L;
        if (!isNull$278) {
            field$278 = otherAcc.getLong(2);
        }
        isNull$273 = otherAcc.isNullAt(1);
        field$273 = -1L;
        if (!isNull$273) {
            field$273 = otherAcc.getLong(1);
        }
        isNull$270 = otherAcc.isNullAt(0);
        field$270 = -1L;
        if (!isNull$270) {
            field$270 = otherAcc.getLong(0);
        }
        isNull$284 = otherAcc.isNullAt(3);
        field$284 = -1L;
        if (!isNull$284) {
            field$284 = otherAcc.getLong(3);
        }

        // when namespace is null, the dataview is used in heap, no key and namespace set
        if (namespace != null) {
            distinctAcc_0_dataview_backup.setCurrentNamespace(namespace);
            distinct_backup_view_0 = distinctAcc_0_dataview_backup;
        } else {
            isNull$290 = otherAcc.isNullAt(5);
            field$290 = null;
            if (!isNull$290) {
                field$290 = ((org.apache.flink.table.data.binary.BinaryRawValueData) otherAcc.getRawValue(5));
            }
            distinct_backup_view_0 = (org.apache.flink.table.api.dataview.MapView) field$290.getJavaObject();
        }


        isNull$271 = agg0_count1IsNull || isNull$270;
        result$272 = -1L;
        if (!isNull$271) {

            result$272 = (long) (agg0_count1 + field$270);

        }

        agg0_count1 = result$272;
        ;
        agg0_count1IsNull = isNull$271;


        long result$277 = -1L;
        boolean isNull$277;
        if (isNull$273) {

            isNull$277 = agg1_sumIsNull;
            if (!isNull$277) {
                result$277 = agg1_sum;
            }
        } else {
            long result$276 = -1L;
            boolean isNull$276;
            if (agg1_sumIsNull) {

                isNull$276 = isNull$273;
                if (!isNull$276) {
                    result$276 = field$273;
                }
            } else {


                isNull$274 = agg1_sumIsNull || isNull$273;
                result$275 = -1L;
                if (!isNull$274) {

                    result$275 = (long) (agg1_sum + field$273);

                }

                isNull$276 = isNull$274;
                if (!isNull$276) {
                    result$276 = result$275;
                }
            }
            isNull$277 = isNull$276;
            if (!isNull$277) {
                result$277 = result$276;
            }
        }
        agg1_sum = result$277;
        ;
        agg1_sumIsNull = isNull$277;


        long result$283 = -1L;
        boolean isNull$283;
        if (isNull$278) {

            isNull$283 = agg2_maxIsNull;
            if (!isNull$283) {
                result$283 = agg2_max;
            }
        } else {
            long result$282 = -1L;
            boolean isNull$282;
            if (agg2_maxIsNull) {

                isNull$282 = isNull$278;
                if (!isNull$282) {
                    result$282 = field$278;
                }
            } else {
                isNull$279 = isNull$278 || agg2_maxIsNull;
                result$280 = false;
                if (!isNull$279) {

                    result$280 = field$278 > agg2_max;

                }

                long result$281 = -1L;
                boolean isNull$281;
                if (result$280) {

                    isNull$281 = isNull$278;
                    if (!isNull$281) {
                        result$281 = field$278;
                    }
                } else {

                    isNull$281 = agg2_maxIsNull;
                    if (!isNull$281) {
                        result$281 = agg2_max;
                    }
                }
                isNull$282 = isNull$281;
                if (!isNull$282) {
                    result$282 = result$281;
                }
            }
            isNull$283 = isNull$282;
            if (!isNull$283) {
                result$283 = result$282;
            }
        }
        agg2_max = result$283;
        ;
        agg2_maxIsNull = isNull$283;


        long result$289 = -1L;
        boolean isNull$289;
        if (isNull$284) {

            isNull$289 = agg3_minIsNull;
            if (!isNull$289) {
                result$289 = agg3_min;
            }
        } else {
            long result$288 = -1L;
            boolean isNull$288;
            if (agg3_minIsNull) {

                isNull$288 = isNull$284;
                if (!isNull$288) {
                    result$288 = field$284;
                }
            } else {
                isNull$285 = isNull$284 || agg3_minIsNull;
                result$286 = false;
                if (!isNull$285) {

                    result$286 = field$284 < agg3_min;

                }

                long result$287 = -1L;
                boolean isNull$287;
                if (result$286) {

                    isNull$287 = isNull$284;
                    if (!isNull$287) {
                        result$287 = field$284;
                    }
                } else {

                    isNull$287 = agg3_minIsNull;
                    if (!isNull$287) {
                        result$287 = agg3_min;
                    }
                }
                isNull$288 = isNull$287;
                if (!isNull$288) {
                    result$288 = result$287;
                }
            }
            isNull$289 = isNull$288;
            if (!isNull$289) {
                result$289 = result$288;
            }
        }
        agg3_min = result$289;
        ;
        agg3_minIsNull = isNull$289;


        Iterable<java.util.Map.Entry> otherEntries$297 =
                (Iterable<java.util.Map.Entry>) distinct_backup_view_0.entries();
        if (otherEntries$297 != null) {
            for (java.util.Map.Entry entry : otherEntries$297) {
                Long distinctKey$291 = (Long) entry.getKey();
                long field$292 = -1L;
                boolean isNull$293 = true;
                if (distinctKey$291 != null) {
                    isNull$293 = false;
                    field$292 = (long) distinctKey$291;
                }
                Long otherValue = (Long) entry.getValue();
                Long thisValue = (Long) distinct_view_0.get(distinctKey$291);
                if (thisValue == null) {
                    thisValue = 0L;
                }
                boolean is_distinct_value_changed_0 = false;
                boolean is_distinct_value_empty_0 = false;


                long existed$298 = ((long) thisValue) & (1L << 0);
                if (existed$298 == 0) {  // not existed
                    long otherExisted = ((long) otherValue) & (1L << 0);
                    if (otherExisted != 0) {  // existed in other
                        is_distinct_value_changed_0 = true;
                        // do accumulate

                        long result$296 = -1L;
                        boolean isNull$296;
                        if (isNull$293) {

                            isNull$296 = agg4_countIsNull;
                            if (!isNull$296) {
                                result$296 = agg4_count;
                            }
                        } else {


                            isNull$294 = agg4_countIsNull || false;
                            result$295 = -1L;
                            if (!isNull$294) {

                                result$295 = (long) (agg4_count + ((long) 1L));

                            }

                            isNull$296 = isNull$294;
                            if (!isNull$296) {
                                result$296 = result$295;
                            }
                        }
                        agg4_count = result$296;
                        ;
                        agg4_countIsNull = isNull$296;

                    }
                }

                thisValue = ((long) thisValue) | ((long) otherValue);
                is_distinct_value_empty_0 = false;

                if (is_distinct_value_empty_0) {
                    distinct_view_0.remove(distinctKey$291);
                } else if (is_distinct_value_changed_0) { // value is not empty and is changed, do update
                    distinct_view_0.put(distinctKey$291, thisValue);
                }
            } // end foreach
        } // end otherEntries != null


    }

    @Override
    public void setAccumulators(Long ns, org.apache.flink.table.data.RowData acc)
            throws Exception {
        namespace = (Long) ns;

        long field$240;
        boolean isNull$240;
        long field$241;
        boolean isNull$241;
        long field$242;
        boolean isNull$242;
        long field$243;
        boolean isNull$243;
        long field$244;
        boolean isNull$244;
        org.apache.flink.table.data.binary.BinaryRawValueData field$245;
        boolean isNull$245;
        isNull$244 = acc.isNullAt(4);
        field$244 = -1L;
        if (!isNull$244) {
            field$244 = acc.getLong(4);
        }
        isNull$240 = acc.isNullAt(0);
        field$240 = -1L;
        if (!isNull$240) {
            field$240 = acc.getLong(0);
        }
        isNull$241 = acc.isNullAt(1);
        field$241 = -1L;
        if (!isNull$241) {
            field$241 = acc.getLong(1);
        }
        isNull$243 = acc.isNullAt(3);
        field$243 = -1L;
        if (!isNull$243) {
            field$243 = acc.getLong(3);
        }

        // when namespace is null, the dataview is used in heap, no key and namespace set
        if (namespace != null) {
            distinctAcc_0_dataview.setCurrentNamespace(namespace);
            distinct_view_0 = distinctAcc_0_dataview;
        } else {
            isNull$245 = acc.isNullAt(5);
            field$245 = null;
            if (!isNull$245) {
                field$245 = ((org.apache.flink.table.data.binary.BinaryRawValueData) acc.getRawValue(5));
            }
            distinct_view_0 = (org.apache.flink.table.api.dataview.MapView) field$245.getJavaObject();
        }

        isNull$242 = acc.isNullAt(2);
        field$242 = -1L;
        if (!isNull$242) {
            field$242 = acc.getLong(2);
        }

        agg0_count1 = field$240;
        ;
        agg0_count1IsNull = isNull$240;


        agg1_sum = field$241;
        ;
        agg1_sumIsNull = isNull$241;


        agg2_max = field$242;
        ;
        agg2_maxIsNull = isNull$242;


        agg3_min = field$243;
        ;
        agg3_minIsNull = isNull$243;


        agg4_count = field$244;
        ;
        agg4_countIsNull = isNull$244;


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$239 = new org.apache.flink.table.data.GenericRowData(6);


        if (agg0_count1IsNull) {
            acc$239.setField(0, null);
        } else {
            acc$239.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            acc$239.setField(1, null);
        } else {
            acc$239.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            acc$239.setField(2, null);
        } else {
            acc$239.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            acc$239.setField(3, null);
        } else {
            acc$239.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            acc$239.setField(4, null);
        } else {
            acc$239.setField(4, agg4_count);
        }


        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$238 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);

        if (false) {
            acc$239.setField(5, null);
        } else {
            acc$239.setField(5, distinct_acc$238);
        }


        return acc$239;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$237 = new org.apache.flink.table.data.GenericRowData(6);


        if (false) {
            acc$237.setField(0, null);
        } else {
            acc$237.setField(0, ((long) 0L));
        }


        if (true) {
            acc$237.setField(1, null);
        } else {
            acc$237.setField(1, ((long) -1L));
        }


        if (true) {
            acc$237.setField(2, null);
        } else {
            acc$237.setField(2, ((long) -1L));
        }


        if (true) {
            acc$237.setField(3, null);
        } else {
            acc$237.setField(3, ((long) -1L));
        }


        if (false) {
            acc$237.setField(4, null);
        } else {
            acc$237.setField(4, ((long) 0L));
        }


        org.apache.flink.table.api.dataview.MapView mapview$236 = new org.apache.flink.table.api.dataview.MapView();
        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$236 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$236);

        if (false) {
            acc$237.setField(5, null);
        } else {
            acc$237.setField(5, distinct_acc$236);
        }


        return acc$237;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue(Long ns) throws Exception {
        namespace = (Long) ns;


        aggValue$299 = new org.apache.flink.table.data.GenericRowData(7);


        if (agg0_count1IsNull) {
            aggValue$299.setField(0, null);
        } else {
            aggValue$299.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            aggValue$299.setField(1, null);
        } else {
            aggValue$299.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            aggValue$299.setField(2, null);
        } else {
            aggValue$299.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            aggValue$299.setField(3, null);
        } else {
            aggValue$299.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            aggValue$299.setField(4, null);
        } else {
            aggValue$299.setField(4, agg4_count);
        }


        if (false) {
            aggValue$299.setField(5, null);
        } else {
            aggValue$299.setField(5, org.apache.flink.table.data.TimestampData
                    .fromEpochMillis(sliceAssigner$233.getWindowStart(namespace)));
        }


        if (false) {
            aggValue$299.setField(6, null);
        } else {
            aggValue$299.setField(6, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace));
        }


        return aggValue$299;

    }

    @Override
    public void cleanup(Long ns) throws Exception {
        namespace = (Long) ns;

        distinctAcc_0_dataview.setCurrentNamespace(namespace);
        distinctAcc_0_dataview.clear();


    }

    @Override
    public void close() throws Exception {

    }
}