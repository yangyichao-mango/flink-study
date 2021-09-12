package flink.examples.sql._07.query._04_window._04_TumbleWindowTest_GlobalAgg;


public final class _04_TumbleWindowTest_GlobalWindowAggsHandler$232
        implements org.apache.flink.table.runtime.generated.NamespaceAggsHandleFunction<Long> {

    private transient org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.SlicedUnsharedSliceAssigner
            sliceAssigner$163;
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
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$164;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$165;
    private org.apache.flink.table.runtime.dataview.StateMapView distinctAcc_0_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData distinctAcc_0_dataview_raw_value;
    private org.apache.flink.table.api.dataview.MapView distinct_view_0;
    org.apache.flink.table.data.GenericRowData acc$167 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData acc$169 = new org.apache.flink.table.data.GenericRowData(6);
    private org.apache.flink.table.api.dataview.MapView otherMapView$221;
    private transient org.apache.flink.table.data.conversion.RawObjectConverter converter$222;
    org.apache.flink.table.data.GenericRowData aggValue$231 = new org.apache.flink.table.data.GenericRowData(7);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    private Long namespace;

    public _04_TumbleWindowTest_GlobalWindowAggsHandler$232(Object[] references) throws Exception {
        sliceAssigner$163 =
                (((org.apache.flink.table.runtime.operators.window.slicing.SliceAssigners.SlicedUnsharedSliceAssigner) references[0]));
        externalSerializer$164 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[1]));
        externalSerializer$165 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[2]));
        converter$222 = (((org.apache.flink.table.data.conversion.RawObjectConverter) references[3]));
    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

        distinctAcc_0_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("distinctAcc_0", true, externalSerializer$164, externalSerializer$165);
        distinctAcc_0_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinctAcc_0_dataview);

        distinct_view_0 = distinctAcc_0_dataview;

        converter$222.open(getRuntimeContext().getUserCodeClassLoader());

    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        boolean isNull$176;
        long result$177;
        long field$178;
        boolean isNull$178;
        boolean isNull$179;
        long result$180;
        boolean isNull$183;
        boolean result$184;
        boolean isNull$188;
        boolean result$189;
        long field$193;
        boolean isNull$193;
        boolean isNull$195;
        long result$196;
        isNull$178 = accInput.isNullAt(2);
        field$178 = -1L;
        if (!isNull$178) {
            field$178 = accInput.getLong(2);
        }
        isNull$193 = accInput.isNullAt(3);
        field$193 = -1L;
        if (!isNull$193) {
            field$193 = accInput.getLong(3);
        }


        isNull$176 = agg0_count1IsNull || false;
        result$177 = -1L;
        if (!isNull$176) {

            result$177 = (long) (agg0_count1 + ((long) 1L));

        }

        agg0_count1 = result$177;
        ;
        agg0_count1IsNull = isNull$176;


        long result$182 = -1L;
        boolean isNull$182;
        if (isNull$178) {

            isNull$182 = agg1_sumIsNull;
            if (!isNull$182) {
                result$182 = agg1_sum;
            }
        } else {
            long result$181 = -1L;
            boolean isNull$181;
            if (agg1_sumIsNull) {

                isNull$181 = isNull$178;
                if (!isNull$181) {
                    result$181 = field$178;
                }
            } else {


                isNull$179 = agg1_sumIsNull || isNull$178;
                result$180 = -1L;
                if (!isNull$179) {

                    result$180 = (long) (agg1_sum + field$178);

                }

                isNull$181 = isNull$179;
                if (!isNull$181) {
                    result$181 = result$180;
                }
            }
            isNull$182 = isNull$181;
            if (!isNull$182) {
                result$182 = result$181;
            }
        }
        agg1_sum = result$182;
        ;
        agg1_sumIsNull = isNull$182;


        long result$187 = -1L;
        boolean isNull$187;
        if (isNull$178) {

            isNull$187 = agg2_maxIsNull;
            if (!isNull$187) {
                result$187 = agg2_max;
            }
        } else {
            long result$186 = -1L;
            boolean isNull$186;
            if (agg2_maxIsNull) {

                isNull$186 = isNull$178;
                if (!isNull$186) {
                    result$186 = field$178;
                }
            } else {
                isNull$183 = isNull$178 || agg2_maxIsNull;
                result$184 = false;
                if (!isNull$183) {

                    result$184 = field$178 > agg2_max;

                }

                long result$185 = -1L;
                boolean isNull$185;
                if (result$184) {

                    isNull$185 = isNull$178;
                    if (!isNull$185) {
                        result$185 = field$178;
                    }
                } else {

                    isNull$185 = agg2_maxIsNull;
                    if (!isNull$185) {
                        result$185 = agg2_max;
                    }
                }
                isNull$186 = isNull$185;
                if (!isNull$186) {
                    result$186 = result$185;
                }
            }
            isNull$187 = isNull$186;
            if (!isNull$187) {
                result$187 = result$186;
            }
        }
        agg2_max = result$187;
        ;
        agg2_maxIsNull = isNull$187;


        long result$192 = -1L;
        boolean isNull$192;
        if (isNull$178) {

            isNull$192 = agg3_minIsNull;
            if (!isNull$192) {
                result$192 = agg3_min;
            }
        } else {
            long result$191 = -1L;
            boolean isNull$191;
            if (agg3_minIsNull) {

                isNull$191 = isNull$178;
                if (!isNull$191) {
                    result$191 = field$178;
                }
            } else {
                isNull$188 = isNull$178 || agg3_minIsNull;
                result$189 = false;
                if (!isNull$188) {

                    result$189 = field$178 < agg3_min;

                }

                long result$190 = -1L;
                boolean isNull$190;
                if (result$189) {

                    isNull$190 = isNull$178;
                    if (!isNull$190) {
                        result$190 = field$178;
                    }
                } else {

                    isNull$190 = agg3_minIsNull;
                    if (!isNull$190) {
                        result$190 = agg3_min;
                    }
                }
                isNull$191 = isNull$190;
                if (!isNull$191) {
                    result$191 = result$190;
                }
            }
            isNull$192 = isNull$191;
            if (!isNull$192) {
                result$192 = result$191;
            }
        }
        agg3_min = result$192;
        ;
        agg3_minIsNull = isNull$192;


        Long distinctKey$194 = (Long) field$193;
        if (isNull$193) {
            distinctKey$194 = null;
        }

        Long value$198 = (Long) distinct_view_0.get(distinctKey$194);
        if (value$198 == null) {
            value$198 = 0L;
        }

        boolean is_distinct_value_changed_0 = false;

        long existed$199 = ((long) value$198) & (1L << 0);
        if (existed$199 == 0) {  // not existed
            value$198 = ((long) value$198) | (1L << 0);
            is_distinct_value_changed_0 = true;

            long result$197 = -1L;
            boolean isNull$197;
            if (isNull$193) {

                isNull$197 = agg4_countIsNull;
                if (!isNull$197) {
                    result$197 = agg4_count;
                }
            } else {


                isNull$195 = agg4_countIsNull || false;
                result$196 = -1L;
                if (!isNull$195) {

                    result$196 = (long) (agg4_count + ((long) 1L));

                }

                isNull$197 = isNull$195;
                if (!isNull$197) {
                    result$197 = result$196;
                }
            }
            agg4_count = result$197;
            ;
            agg4_countIsNull = isNull$197;

        }

        if (is_distinct_value_changed_0) {
            distinct_view_0.put(distinctKey$194, value$198);
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

        long field$200;
        boolean isNull$200;
        boolean isNull$201;
        long result$202;
        long field$203;
        boolean isNull$203;
        boolean isNull$204;
        long result$205;
        long field$208;
        boolean isNull$208;
        boolean isNull$209;
        boolean result$210;
        long field$214;
        boolean isNull$214;
        boolean isNull$215;
        boolean result$216;
        org.apache.flink.table.data.binary.BinaryRawValueData field$220;
        boolean isNull$220;
        boolean isNull$226;
        long result$227;
        isNull$208 = otherAcc.isNullAt(2);
        field$208 = -1L;
        if (!isNull$208) {
            field$208 = otherAcc.getLong(2);
        }
        isNull$203 = otherAcc.isNullAt(1);
        field$203 = -1L;
        if (!isNull$203) {
            field$203 = otherAcc.getLong(1);
        }
        isNull$200 = otherAcc.isNullAt(0);
        field$200 = -1L;
        if (!isNull$200) {
            field$200 = otherAcc.getLong(0);
        }
        isNull$214 = otherAcc.isNullAt(3);
        field$214 = -1L;
        if (!isNull$214) {
            field$214 = otherAcc.getLong(3);
        }

        isNull$220 = otherAcc.isNullAt(5);
        field$220 = null;
        if (!isNull$220) {
            field$220 = ((org.apache.flink.table.data.binary.BinaryRawValueData) otherAcc.getRawValue(5));
        }
        otherMapView$221 = null;
        if (!isNull$220) {
            otherMapView$221 =
                    (org.apache.flink.table.api.dataview.MapView) converter$222
                            .toExternal((org.apache.flink.table.data.binary.BinaryRawValueData) field$220);
        }


        isNull$201 = agg0_count1IsNull || isNull$200;
        result$202 = -1L;
        if (!isNull$201) {

            result$202 = (long) (agg0_count1 + field$200);

        }

        agg0_count1 = result$202;
        ;
        agg0_count1IsNull = isNull$201;


        long result$207 = -1L;
        boolean isNull$207;
        if (isNull$203) {

            isNull$207 = agg1_sumIsNull;
            if (!isNull$207) {
                result$207 = agg1_sum;
            }
        } else {
            long result$206 = -1L;
            boolean isNull$206;
            if (agg1_sumIsNull) {

                isNull$206 = isNull$203;
                if (!isNull$206) {
                    result$206 = field$203;
                }
            } else {


                isNull$204 = agg1_sumIsNull || isNull$203;
                result$205 = -1L;
                if (!isNull$204) {

                    result$205 = (long) (agg1_sum + field$203);

                }

                isNull$206 = isNull$204;
                if (!isNull$206) {
                    result$206 = result$205;
                }
            }
            isNull$207 = isNull$206;
            if (!isNull$207) {
                result$207 = result$206;
            }
        }
        agg1_sum = result$207;
        ;
        agg1_sumIsNull = isNull$207;


        long result$213 = -1L;
        boolean isNull$213;
        if (isNull$208) {

            isNull$213 = agg2_maxIsNull;
            if (!isNull$213) {
                result$213 = agg2_max;
            }
        } else {
            long result$212 = -1L;
            boolean isNull$212;
            if (agg2_maxIsNull) {

                isNull$212 = isNull$208;
                if (!isNull$212) {
                    result$212 = field$208;
                }
            } else {
                isNull$209 = isNull$208 || agg2_maxIsNull;
                result$210 = false;
                if (!isNull$209) {

                    result$210 = field$208 > agg2_max;

                }

                long result$211 = -1L;
                boolean isNull$211;
                if (result$210) {

                    isNull$211 = isNull$208;
                    if (!isNull$211) {
                        result$211 = field$208;
                    }
                } else {

                    isNull$211 = agg2_maxIsNull;
                    if (!isNull$211) {
                        result$211 = agg2_max;
                    }
                }
                isNull$212 = isNull$211;
                if (!isNull$212) {
                    result$212 = result$211;
                }
            }
            isNull$213 = isNull$212;
            if (!isNull$213) {
                result$213 = result$212;
            }
        }
        agg2_max = result$213;
        ;
        agg2_maxIsNull = isNull$213;


        long result$219 = -1L;
        boolean isNull$219;
        if (isNull$214) {

            isNull$219 = agg3_minIsNull;
            if (!isNull$219) {
                result$219 = agg3_min;
            }
        } else {
            long result$218 = -1L;
            boolean isNull$218;
            if (agg3_minIsNull) {

                isNull$218 = isNull$214;
                if (!isNull$218) {
                    result$218 = field$214;
                }
            } else {
                isNull$215 = isNull$214 || agg3_minIsNull;
                result$216 = false;
                if (!isNull$215) {

                    result$216 = field$214 < agg3_min;

                }

                long result$217 = -1L;
                boolean isNull$217;
                if (result$216) {

                    isNull$217 = isNull$214;
                    if (!isNull$217) {
                        result$217 = field$214;
                    }
                } else {

                    isNull$217 = agg3_minIsNull;
                    if (!isNull$217) {
                        result$217 = agg3_min;
                    }
                }
                isNull$218 = isNull$217;
                if (!isNull$218) {
                    result$218 = result$217;
                }
            }
            isNull$219 = isNull$218;
            if (!isNull$219) {
                result$219 = result$218;
            }
        }
        agg3_min = result$219;
        ;
        agg3_minIsNull = isNull$219;


        Iterable<java.util.Map.Entry> otherEntries$229 =
                (Iterable<java.util.Map.Entry>) otherMapView$221.entries();
        if (otherEntries$229 != null) {
            for (java.util.Map.Entry entry : otherEntries$229) {
                Long distinctKey$223 = (Long) entry.getKey();
                long field$224 = -1L;
                boolean isNull$225 = true;
                if (distinctKey$223 != null) {
                    isNull$225 = false;
                    field$224 = (long) distinctKey$223;
                }
                Long otherValue = (Long) entry.getValue();
                Long thisValue = (Long) distinct_view_0.get(distinctKey$223);
                if (thisValue == null) {
                    thisValue = 0L;
                }
                boolean is_distinct_value_changed_0 = false;
                boolean is_distinct_value_empty_0 = false;


                long existed$230 = ((long) thisValue) & (1L << 0);
                if (existed$230 == 0) {  // not existed
                    long otherExisted = ((long) otherValue) & (1L << 0);
                    if (otherExisted != 0) {  // existed in other
                        is_distinct_value_changed_0 = true;
                        // do accumulate

                        long result$228 = -1L;
                        boolean isNull$228;
                        if (isNull$225) {

                            isNull$228 = agg4_countIsNull;
                            if (!isNull$228) {
                                result$228 = agg4_count;
                            }
                        } else {


                            isNull$226 = agg4_countIsNull || false;
                            result$227 = -1L;
                            if (!isNull$226) {

                                result$227 = (long) (agg4_count + ((long) 1L));

                            }

                            isNull$228 = isNull$226;
                            if (!isNull$228) {
                                result$228 = result$227;
                            }
                        }
                        agg4_count = result$228;
                        ;
                        agg4_countIsNull = isNull$228;

                    }
                }

                thisValue = ((long) thisValue) | ((long) otherValue);
                is_distinct_value_empty_0 = false;

                if (is_distinct_value_empty_0) {
                    distinct_view_0.remove(distinctKey$223);
                } else if (is_distinct_value_changed_0) { // value is not empty and is changed, do update
                    distinct_view_0.put(distinctKey$223, thisValue);
                }
            } // end foreach
        } // end otherEntries != null


    }

    @Override
    public void setAccumulators(Long ns, org.apache.flink.table.data.RowData acc)
            throws Exception {
        namespace = (Long) ns;

        long field$170;
        boolean isNull$170;
        long field$171;
        boolean isNull$171;
        long field$172;
        boolean isNull$172;
        long field$173;
        boolean isNull$173;
        long field$174;
        boolean isNull$174;
        org.apache.flink.table.data.binary.BinaryRawValueData field$175;
        boolean isNull$175;
        isNull$174 = acc.isNullAt(4);
        field$174 = -1L;
        if (!isNull$174) {
            field$174 = acc.getLong(4);
        }
        isNull$170 = acc.isNullAt(0);
        field$170 = -1L;
        if (!isNull$170) {
            field$170 = acc.getLong(0);
        }
        isNull$171 = acc.isNullAt(1);
        field$171 = -1L;
        if (!isNull$171) {
            field$171 = acc.getLong(1);
        }
        isNull$173 = acc.isNullAt(3);
        field$173 = -1L;
        if (!isNull$173) {
            field$173 = acc.getLong(3);
        }

        // when namespace is null, the dataview is used in heap, no key and namespace set
        if (namespace != null) {
            distinctAcc_0_dataview.setCurrentNamespace(namespace);
            distinct_view_0 = distinctAcc_0_dataview;
        } else {
            isNull$175 = acc.isNullAt(5);
            field$175 = null;
            if (!isNull$175) {
                field$175 = ((org.apache.flink.table.data.binary.BinaryRawValueData) acc.getRawValue(5));
            }
            distinct_view_0 = (org.apache.flink.table.api.dataview.MapView) field$175.getJavaObject();
        }

        isNull$172 = acc.isNullAt(2);
        field$172 = -1L;
        if (!isNull$172) {
            field$172 = acc.getLong(2);
        }

        agg0_count1 = field$170;
        ;
        agg0_count1IsNull = isNull$170;


        agg1_sum = field$171;
        ;
        agg1_sumIsNull = isNull$171;


        agg2_max = field$172;
        ;
        agg2_maxIsNull = isNull$172;


        agg3_min = field$173;
        ;
        agg3_minIsNull = isNull$173;


        agg4_count = field$174;
        ;
        agg4_countIsNull = isNull$174;


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$169 = new org.apache.flink.table.data.GenericRowData(6);


        if (agg0_count1IsNull) {
            acc$169.setField(0, null);
        } else {
            acc$169.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            acc$169.setField(1, null);
        } else {
            acc$169.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            acc$169.setField(2, null);
        } else {
            acc$169.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            acc$169.setField(3, null);
        } else {
            acc$169.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            acc$169.setField(4, null);
        } else {
            acc$169.setField(4, agg4_count);
        }


        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$168 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(distinct_view_0);

        if (false) {
            acc$169.setField(5, null);
        } else {
            acc$169.setField(5, distinct_acc$168);
        }


        return acc$169;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$167 = new org.apache.flink.table.data.GenericRowData(6);


        if (false) {
            acc$167.setField(0, null);
        } else {
            acc$167.setField(0, ((long) 0L));
        }


        if (true) {
            acc$167.setField(1, null);
        } else {
            acc$167.setField(1, ((long) -1L));
        }


        if (true) {
            acc$167.setField(2, null);
        } else {
            acc$167.setField(2, ((long) -1L));
        }


        if (true) {
            acc$167.setField(3, null);
        } else {
            acc$167.setField(3, ((long) -1L));
        }


        if (false) {
            acc$167.setField(4, null);
        } else {
            acc$167.setField(4, ((long) 0L));
        }


        org.apache.flink.table.api.dataview.MapView mapview$166 = new org.apache.flink.table.api.dataview.MapView();
        org.apache.flink.table.data.binary.BinaryRawValueData distinct_acc$166 =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(mapview$166);

        if (false) {
            acc$167.setField(5, null);
        } else {
            acc$167.setField(5, distinct_acc$166);
        }


        return acc$167;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue(Long ns) throws Exception {
        namespace = (Long) ns;


        aggValue$231 = new org.apache.flink.table.data.GenericRowData(7);


        if (agg0_count1IsNull) {
            aggValue$231.setField(0, null);
        } else {
            aggValue$231.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            aggValue$231.setField(1, null);
        } else {
            aggValue$231.setField(1, agg1_sum);
        }


        if (agg2_maxIsNull) {
            aggValue$231.setField(2, null);
        } else {
            aggValue$231.setField(2, agg2_max);
        }


        if (agg3_minIsNull) {
            aggValue$231.setField(3, null);
        } else {
            aggValue$231.setField(3, agg3_min);
        }


        if (agg4_countIsNull) {
            aggValue$231.setField(4, null);
        } else {
            aggValue$231.setField(4, agg4_count);
        }


        if (false) {
            aggValue$231.setField(5, null);
        } else {
            aggValue$231.setField(5, org.apache.flink.table.data.TimestampData
                    .fromEpochMillis(sliceAssigner$163.getWindowStart(namespace)));
        }


        if (false) {
            aggValue$231.setField(6, null);
        } else {
            aggValue$231.setField(6, org.apache.flink.table.data.TimestampData.fromEpochMillis(namespace));
        }


        return aggValue$231;

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