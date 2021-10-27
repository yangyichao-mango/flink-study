package flink.examples.sql._07.query._04_window_agg._02_cumulate_window.earlyfire;


/**
 * {@link org.apache.flink.streaming.api.operators.KeyedProcessOperator}
 */
public final class GroupAggsHandler$210 implements org.apache.flink.table.runtime.generated.AggsHandleFunction {

    long agg0_sum;
    boolean agg0_sumIsNull;
    long agg0_count;
    boolean agg0_countIsNull;
    long agg1_sum;
    boolean agg1_sumIsNull;
    long agg1_count;
    boolean agg1_countIsNull;
    private transient org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction
            function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$105;
    private transient org.apache.flink.table.runtime.typeutils.ExternalSerializer externalSerializer$106;
    private org.apache.flink.table.runtime.dataview.StateMapView agg2$map_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData agg2$map_dataview_raw_value;
    private org.apache.flink.table.runtime.dataview.StateMapView agg2$map_dataview_backup;
    private org.apache.flink.table.data.binary.BinaryRawValueData agg2$map_dataview_backup_raw_value;
    private transient org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction
            function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396;
    private org.apache.flink.table.runtime.dataview.StateMapView agg3$map_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData agg3$map_dataview_raw_value;
    private org.apache.flink.table.runtime.dataview.StateMapView agg3$map_dataview_backup;
    private org.apache.flink.table.data.binary.BinaryRawValueData agg3$map_dataview_backup_raw_value;
    long agg4_sum;
    boolean agg4_sumIsNull;
    long agg4_count;
    boolean agg4_countIsNull;
    private org.apache.flink.table.runtime.dataview.StateMapView agg5$map_dataview;
    private org.apache.flink.table.data.binary.BinaryRawValueData agg5$map_dataview_raw_value;
    private org.apache.flink.table.runtime.dataview.StateMapView agg5$map_dataview_backup;
    private org.apache.flink.table.data.binary.BinaryRawValueData agg5$map_dataview_backup_raw_value;
    long agg6_count1;
    boolean agg6_count1IsNull;
    private transient org.apache.flink.table.data.conversion.StructuredObjectConverter converter$107;
    private transient org.apache.flink.table.data.conversion.StructuredObjectConverter converter$109;
    org.apache.flink.table.data.GenericRowData acc$112 = new org.apache.flink.table.data.GenericRowData(10);
    org.apache.flink.table.data.GenericRowData acc$113 = new org.apache.flink.table.data.GenericRowData(10);
    org.apache.flink.table.data.UpdatableRowData field$119;
    private org.apache.flink.table.data.RowData agg2_acc_internal;
    private org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator
            agg2_acc_external;
    org.apache.flink.table.data.UpdatableRowData field$121;
    private org.apache.flink.table.data.RowData agg3_acc_internal;
    private org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction.MinWithRetractAccumulator
            agg3_acc_external;
    org.apache.flink.table.data.UpdatableRowData field$125;
    private org.apache.flink.table.data.RowData agg5_acc_internal;
    private org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator
            agg5_acc_external;
    org.apache.flink.table.data.GenericRowData aggValue$209 = new org.apache.flink.table.data.GenericRowData(6);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    public GroupAggsHandler$210(java.lang.Object[] references) throws Exception {
        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448 =
                (((org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction) references[0]));
        externalSerializer$105 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[1]));
        externalSerializer$106 = (((org.apache.flink.table.runtime.typeutils.ExternalSerializer) references[2]));
        function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396 =
                (((org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction) references[3]));
        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448 =
                (((org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction) references[4]));
        converter$107 = (((org.apache.flink.table.data.conversion.StructuredObjectConverter) references[5]));
        converter$109 = (((org.apache.flink.table.data.conversion.StructuredObjectConverter) references[6]));
    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                .open(new org.apache.flink.table.functions.FunctionContext(store.getRuntimeContext()));


        agg2$map_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("agg2$map", false, externalSerializer$105, externalSerializer$106);
        agg2$map_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(agg2$map_dataview);


        agg2$map_dataview_backup = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("agg2$map", false, externalSerializer$105, externalSerializer$106);
        agg2$map_dataview_backup_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(agg2$map_dataview_backup);


        function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                .open(new org.apache.flink.table.functions.FunctionContext(store.getRuntimeContext()));


        agg3$map_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("agg3$map", false, externalSerializer$105, externalSerializer$106);
        agg3$map_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(agg3$map_dataview);


        agg3$map_dataview_backup = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("agg3$map", false, externalSerializer$105, externalSerializer$106);
        agg3$map_dataview_backup_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(agg3$map_dataview_backup);


        agg5$map_dataview = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("agg5$map", false, externalSerializer$105, externalSerializer$106);
        agg5$map_dataview_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(agg5$map_dataview);


        agg5$map_dataview_backup = (org.apache.flink.table.runtime.dataview.StateMapView) store
                .getStateMapView("agg5$map", false, externalSerializer$105, externalSerializer$106);
        agg5$map_dataview_backup_raw_value =
                org.apache.flink.table.data.binary.BinaryRawValueData.fromObject(agg5$map_dataview_backup);


        converter$107.open(getRuntimeContext().getUserCodeClassLoader());


        converter$109.open(getRuntimeContext().getUserCodeClassLoader());

    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        long field$127;
        boolean isNull$127;
        boolean isNull$128;
        long result$129;
        boolean isNull$132;
        long result$133;
        long field$135;
        boolean isNull$135;
        boolean isNull$136;
        long result$137;
        boolean isNull$140;
        long result$141;
        long field$143;
        boolean isNull$143;
        long field$144;
        boolean isNull$144;
        long field$145;
        boolean isNull$145;
        boolean isNull$146;
        long result$147;
        boolean isNull$150;
        long result$151;
        long field$153;
        boolean isNull$153;
        boolean isNull$154;
        long result$155;
        isNull$144 = accInput.isNullAt(5);
        field$144 = -1L;
        if (!isNull$144) {
            field$144 = accInput.getLong(5);
        }
        isNull$127 = accInput.isNullAt(2);
        field$127 = -1L;
        if (!isNull$127) {
            field$127 = accInput.getLong(2);
        }
        isNull$143 = accInput.isNullAt(4);
        field$143 = -1L;
        if (!isNull$143) {
            field$143 = accInput.getLong(4);
        }
        isNull$135 = accInput.isNullAt(3);
        field$135 = -1L;
        if (!isNull$135) {
            field$135 = accInput.getLong(3);
        }
        isNull$145 = accInput.isNullAt(6);
        field$145 = -1L;
        if (!isNull$145) {
            field$145 = accInput.getLong(6);
        }
        isNull$153 = accInput.isNullAt(1);
        field$153 = -1L;
        if (!isNull$153) {
            field$153 = accInput.getLong(1);
        }


        long result$131 = -1L;
        boolean isNull$131;
        if (isNull$127) {

            isNull$131 = agg0_sumIsNull;
            if (!isNull$131) {
                result$131 = agg0_sum;
            }
        } else {
            long result$130 = -1L;
            boolean isNull$130;
            if (agg0_sumIsNull) {

                isNull$130 = isNull$127;
                if (!isNull$130) {
                    result$130 = field$127;
                }
            } else {


                isNull$128 = agg0_sumIsNull || isNull$127;
                result$129 = -1L;
                if (!isNull$128) {

                    result$129 = (long) (agg0_sum + field$127);

                }

                isNull$130 = isNull$128;
                if (!isNull$130) {
                    result$130 = result$129;
                }
            }
            isNull$131 = isNull$130;
            if (!isNull$131) {
                result$131 = result$130;
            }
        }
        agg0_sum = result$131;
        ;
        agg0_sumIsNull = isNull$131;


        long result$134 = -1L;
        boolean isNull$134;
        if (isNull$127) {

            isNull$134 = agg0_countIsNull;
            if (!isNull$134) {
                result$134 = agg0_count;
            }
        } else {


            isNull$132 = agg0_countIsNull || false;
            result$133 = -1L;
            if (!isNull$132) {

                result$133 = (long) (agg0_count + ((long) 1L));

            }

            isNull$134 = isNull$132;
            if (!isNull$134) {
                result$134 = result$133;
            }
        }
        agg0_count = result$134;
        ;
        agg0_countIsNull = isNull$134;


        long result$139 = -1L;
        boolean isNull$139;
        if (isNull$135) {

            isNull$139 = agg1_sumIsNull;
            if (!isNull$139) {
                result$139 = agg1_sum;
            }
        } else {
            long result$138 = -1L;
            boolean isNull$138;
            if (agg1_sumIsNull) {

                isNull$138 = isNull$135;
                if (!isNull$138) {
                    result$138 = field$135;
                }
            } else {


                isNull$136 = agg1_sumIsNull || isNull$135;
                result$137 = -1L;
                if (!isNull$136) {

                    result$137 = (long) (agg1_sum + field$135);

                }

                isNull$138 = isNull$136;
                if (!isNull$138) {
                    result$138 = result$137;
                }
            }
            isNull$139 = isNull$138;
            if (!isNull$139) {
                result$139 = result$138;
            }
        }
        agg1_sum = result$139;
        ;
        agg1_sumIsNull = isNull$139;


        long result$142 = -1L;
        boolean isNull$142;
        if (isNull$135) {

            isNull$142 = agg1_countIsNull;
            if (!isNull$142) {
                result$142 = agg1_count;
            }
        } else {


            isNull$140 = agg1_countIsNull || false;
            result$141 = -1L;
            if (!isNull$140) {

                result$141 = (long) (agg1_count + ((long) 1L));

            }

            isNull$142 = isNull$140;
            if (!isNull$142) {
                result$142 = result$141;
            }
        }
        agg1_count = result$142;
        ;
        agg1_countIsNull = isNull$142;


        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                .accumulate(agg2_acc_external, isNull$143 ? null : ((java.lang.Long) field$143));


        function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                .accumulate(agg3_acc_external, isNull$144 ? null : ((java.lang.Long) field$144));


        long result$149 = -1L;
        boolean isNull$149;
        if (isNull$145) {

            isNull$149 = agg4_sumIsNull;
            if (!isNull$149) {
                result$149 = agg4_sum;
            }
        } else {
            long result$148 = -1L;
            boolean isNull$148;
            if (agg4_sumIsNull) {

                isNull$148 = isNull$145;
                if (!isNull$148) {
                    result$148 = field$145;
                }
            } else {


                isNull$146 = agg4_sumIsNull || isNull$145;
                result$147 = -1L;
                if (!isNull$146) {

                    result$147 = (long) (agg4_sum + field$145);

                }

                isNull$148 = isNull$146;
                if (!isNull$148) {
                    result$148 = result$147;
                }
            }
            isNull$149 = isNull$148;
            if (!isNull$149) {
                result$149 = result$148;
            }
        }
        agg4_sum = result$149;
        ;
        agg4_sumIsNull = isNull$149;


        long result$152 = -1L;
        boolean isNull$152;
        if (isNull$145) {

            isNull$152 = agg4_countIsNull;
            if (!isNull$152) {
                result$152 = agg4_count;
            }
        } else {


            isNull$150 = agg4_countIsNull || false;
            result$151 = -1L;
            if (!isNull$150) {

                result$151 = (long) (agg4_count + ((long) 1L));

            }

            isNull$152 = isNull$150;
            if (!isNull$152) {
                result$152 = result$151;
            }
        }
        agg4_count = result$152;
        ;
        agg4_countIsNull = isNull$152;


        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                .accumulate(agg5_acc_external, isNull$153 ? null : ((java.lang.Long) field$153));


        isNull$154 = agg6_count1IsNull || false;
        result$155 = -1L;
        if (!isNull$154) {

            result$155 = (long) (agg6_count1 + ((long) 1L));

        }

        agg6_count1 = result$155;
        ;
        agg6_count1IsNull = isNull$154;


    }

    @Override
    public void retract(org.apache.flink.table.data.RowData retractInput) throws Exception {

        long field$156;
        boolean isNull$156;
        boolean isNull$157;
        long result$158;
        boolean isNull$159;
        long result$160;
        boolean isNull$163;
        long result$164;
        long field$166;
        boolean isNull$166;
        boolean isNull$167;
        long result$168;
        boolean isNull$169;
        long result$170;
        boolean isNull$173;
        long result$174;
        long field$176;
        boolean isNull$176;
        long field$177;
        boolean isNull$177;
        long field$178;
        boolean isNull$178;
        boolean isNull$179;
        long result$180;
        boolean isNull$181;
        long result$182;
        boolean isNull$185;
        long result$186;
        long field$188;
        boolean isNull$188;
        boolean isNull$189;
        long result$190;
        isNull$166 = retractInput.isNullAt(3);
        field$166 = -1L;
        if (!isNull$166) {
            field$166 = retractInput.getLong(3);
        }
        isNull$156 = retractInput.isNullAt(2);
        field$156 = -1L;
        if (!isNull$156) {
            field$156 = retractInput.getLong(2);
        }
        isNull$178 = retractInput.isNullAt(6);
        field$178 = -1L;
        if (!isNull$178) {
            field$178 = retractInput.getLong(6);
        }
        isNull$176 = retractInput.isNullAt(4);
        field$176 = -1L;
        if (!isNull$176) {
            field$176 = retractInput.getLong(4);
        }
        isNull$177 = retractInput.isNullAt(5);
        field$177 = -1L;
        if (!isNull$177) {
            field$177 = retractInput.getLong(5);
        }
        isNull$188 = retractInput.isNullAt(1);
        field$188 = -1L;
        if (!isNull$188) {
            field$188 = retractInput.getLong(1);
        }


        long result$162 = -1L;
        boolean isNull$162;
        if (isNull$156) {

            isNull$162 = agg0_sumIsNull;
            if (!isNull$162) {
                result$162 = agg0_sum;
            }
        } else {
            long result$161 = -1L;
            boolean isNull$161;
            if (agg0_sumIsNull) {


                isNull$157 = false || isNull$156;
                result$158 = -1L;
                if (!isNull$157) {

                    result$158 = (long) (((long) 0L) - field$156);

                }

                isNull$161 = isNull$157;
                if (!isNull$161) {
                    result$161 = result$158;
                }
            } else {


                isNull$159 = agg0_sumIsNull || isNull$156;
                result$160 = -1L;
                if (!isNull$159) {

                    result$160 = (long) (agg0_sum - field$156);

                }

                isNull$161 = isNull$159;
                if (!isNull$161) {
                    result$161 = result$160;
                }
            }
            isNull$162 = isNull$161;
            if (!isNull$162) {
                result$162 = result$161;
            }
        }
        agg0_sum = result$162;
        ;
        agg0_sumIsNull = isNull$162;


        long result$165 = -1L;
        boolean isNull$165;
        if (isNull$156) {

            isNull$165 = agg0_countIsNull;
            if (!isNull$165) {
                result$165 = agg0_count;
            }
        } else {


            isNull$163 = agg0_countIsNull || false;
            result$164 = -1L;
            if (!isNull$163) {

                result$164 = (long) (agg0_count - ((long) 1L));

            }

            isNull$165 = isNull$163;
            if (!isNull$165) {
                result$165 = result$164;
            }
        }
        agg0_count = result$165;
        ;
        agg0_countIsNull = isNull$165;


        long result$172 = -1L;
        boolean isNull$172;
        if (isNull$166) {

            isNull$172 = agg1_sumIsNull;
            if (!isNull$172) {
                result$172 = agg1_sum;
            }
        } else {
            long result$171 = -1L;
            boolean isNull$171;
            if (agg1_sumIsNull) {


                isNull$167 = false || isNull$166;
                result$168 = -1L;
                if (!isNull$167) {

                    result$168 = (long) (((long) 0L) - field$166);

                }

                isNull$171 = isNull$167;
                if (!isNull$171) {
                    result$171 = result$168;
                }
            } else {


                isNull$169 = agg1_sumIsNull || isNull$166;
                result$170 = -1L;
                if (!isNull$169) {

                    result$170 = (long) (agg1_sum - field$166);

                }

                isNull$171 = isNull$169;
                if (!isNull$171) {
                    result$171 = result$170;
                }
            }
            isNull$172 = isNull$171;
            if (!isNull$172) {
                result$172 = result$171;
            }
        }
        agg1_sum = result$172;
        ;
        agg1_sumIsNull = isNull$172;


        long result$175 = -1L;
        boolean isNull$175;
        if (isNull$166) {

            isNull$175 = agg1_countIsNull;
            if (!isNull$175) {
                result$175 = agg1_count;
            }
        } else {


            isNull$173 = agg1_countIsNull || false;
            result$174 = -1L;
            if (!isNull$173) {

                result$174 = (long) (agg1_count - ((long) 1L));

            }

            isNull$175 = isNull$173;
            if (!isNull$175) {
                result$175 = result$174;
            }
        }
        agg1_count = result$175;
        ;
        agg1_countIsNull = isNull$175;


        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                .retract(agg2_acc_external, isNull$176 ? null : ((java.lang.Long) field$176));


        function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                .retract(agg3_acc_external, isNull$177 ? null : ((java.lang.Long) field$177));


        long result$184 = -1L;
        boolean isNull$184;
        if (isNull$178) {

            isNull$184 = agg4_sumIsNull;
            if (!isNull$184) {
                result$184 = agg4_sum;
            }
        } else {
            long result$183 = -1L;
            boolean isNull$183;
            if (agg4_sumIsNull) {


                isNull$179 = false || isNull$178;
                result$180 = -1L;
                if (!isNull$179) {

                    result$180 = (long) (((long) 0L) - field$178);

                }

                isNull$183 = isNull$179;
                if (!isNull$183) {
                    result$183 = result$180;
                }
            } else {


                isNull$181 = agg4_sumIsNull || isNull$178;
                result$182 = -1L;
                if (!isNull$181) {

                    result$182 = (long) (agg4_sum - field$178);

                }

                isNull$183 = isNull$181;
                if (!isNull$183) {
                    result$183 = result$182;
                }
            }
            isNull$184 = isNull$183;
            if (!isNull$184) {
                result$184 = result$183;
            }
        }
        agg4_sum = result$184;
        ;
        agg4_sumIsNull = isNull$184;


        long result$187 = -1L;
        boolean isNull$187;
        if (isNull$178) {

            isNull$187 = agg4_countIsNull;
            if (!isNull$187) {
                result$187 = agg4_count;
            }
        } else {


            isNull$185 = agg4_countIsNull || false;
            result$186 = -1L;
            if (!isNull$185) {

                result$186 = (long) (agg4_count - ((long) 1L));

            }

            isNull$187 = isNull$185;
            if (!isNull$187) {
                result$187 = result$186;
            }
        }
        agg4_count = result$187;
        ;
        agg4_countIsNull = isNull$187;


        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                .retract(agg5_acc_external, isNull$188 ? null : ((java.lang.Long) field$188));


        isNull$189 = agg6_count1IsNull || false;
        result$190 = -1L;
        if (!isNull$189) {

            result$190 = (long) (agg6_count1 - ((long) 1L));

        }

        agg6_count1 = result$190;
        ;
        agg6_count1IsNull = isNull$189;


    }

    @Override
    public void merge(org.apache.flink.table.data.RowData otherAcc) throws Exception {

        throw new java.lang.RuntimeException("This function not require merge method, but the merge method is called.");

    }

    @Override
    public void setAccumulators(org.apache.flink.table.data.RowData acc) throws Exception {

        long field$114;
        boolean isNull$114;
        long field$115;
        boolean isNull$115;
        long field$116;
        boolean isNull$116;
        long field$117;
        boolean isNull$117;
        org.apache.flink.table.data.RowData field$118;
        boolean isNull$118;
        org.apache.flink.table.data.RowData field$120;
        boolean isNull$120;
        long field$122;
        boolean isNull$122;
        long field$123;
        boolean isNull$123;
        org.apache.flink.table.data.RowData field$124;
        boolean isNull$124;
        long field$126;
        boolean isNull$126;
        isNull$124 = acc.isNullAt(8);
        field$124 = null;
        if (!isNull$124) {
            field$124 = acc.getRow(8, 3);
        }
        field$125 = null;
        if (!isNull$124) {
            field$125 = new org.apache.flink.table.data.UpdatableRowData(
                    field$124,
                    3);

            agg5$map_dataview_raw_value.setJavaObject(agg5$map_dataview);
            field$125.setField(2, agg5$map_dataview_raw_value);

        }

        isNull$126 = acc.isNullAt(9);
        field$126 = -1L;
        if (!isNull$126) {
            field$126 = acc.getLong(9);
        }
        isNull$122 = acc.isNullAt(6);
        field$122 = -1L;
        if (!isNull$122) {
            field$122 = acc.getLong(6);
        }

        isNull$118 = acc.isNullAt(4);
        field$118 = null;
        if (!isNull$118) {
            field$118 = acc.getRow(4, 3);
        }
        field$119 = null;
        if (!isNull$118) {
            field$119 = new org.apache.flink.table.data.UpdatableRowData(
                    field$118,
                    3);

            agg2$map_dataview_raw_value.setJavaObject(agg2$map_dataview);
            field$119.setField(2, agg2$map_dataview_raw_value);

        }

        isNull$123 = acc.isNullAt(7);
        field$123 = -1L;
        if (!isNull$123) {
            field$123 = acc.getLong(7);
        }
        isNull$114 = acc.isNullAt(0);
        field$114 = -1L;
        if (!isNull$114) {
            field$114 = acc.getLong(0);
        }
        isNull$115 = acc.isNullAt(1);
        field$115 = -1L;
        if (!isNull$115) {
            field$115 = acc.getLong(1);
        }
        isNull$117 = acc.isNullAt(3);
        field$117 = -1L;
        if (!isNull$117) {
            field$117 = acc.getLong(3);
        }

        isNull$120 = acc.isNullAt(5);
        field$120 = null;
        if (!isNull$120) {
            field$120 = acc.getRow(5, 3);
        }
        field$121 = null;
        if (!isNull$120) {
            field$121 = new org.apache.flink.table.data.UpdatableRowData(
                    field$120,
                    3);

            agg3$map_dataview_raw_value.setJavaObject(agg3$map_dataview);
            field$121.setField(2, agg3$map_dataview_raw_value);

        }

        isNull$116 = acc.isNullAt(2);
        field$116 = -1L;
        if (!isNull$116) {
            field$116 = acc.getLong(2);
        }

        agg0_sum = field$114;
        ;
        agg0_sumIsNull = isNull$114;


        agg0_count = field$115;
        ;
        agg0_countIsNull = isNull$115;


        agg1_sum = field$116;
        ;
        agg1_sumIsNull = isNull$116;


        agg1_count = field$117;
        ;
        agg1_countIsNull = isNull$117;


        agg2_acc_internal = field$119;
        agg2_acc_external =
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) converter$107
                        .toExternal((org.apache.flink.table.data.RowData) agg2_acc_internal);


        agg3_acc_internal = field$121;
        agg3_acc_external =
                (org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction.MinWithRetractAccumulator) converter$109
                        .toExternal((org.apache.flink.table.data.RowData) agg3_acc_internal);


        agg4_sum = field$122;
        ;
        agg4_sumIsNull = isNull$122;


        agg4_count = field$123;
        ;
        agg4_countIsNull = isNull$123;


        agg5_acc_internal = field$125;
        agg5_acc_external =
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) converter$107
                        .toExternal((org.apache.flink.table.data.RowData) agg5_acc_internal);


        agg6_count1 = field$126;
        ;
        agg6_count1IsNull = isNull$126;


    }

    @Override
    public void resetAccumulators() throws Exception {


        agg0_sum = ((long) -1L);
        agg0_sumIsNull = true;


        agg0_count = ((long) 0L);
        agg0_countIsNull = false;


        agg1_sum = ((long) -1L);
        agg1_sumIsNull = true;


        agg1_count = ((long) 0L);
        agg1_countIsNull = false;


        agg2_acc_external =
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                        .createAccumulator();
        agg2_acc_internal = (org.apache.flink.table.data.RowData) converter$107.toInternalOrNull(
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) agg2_acc_external);


        agg3_acc_external =
                (org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction.MinWithRetractAccumulator) function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                        .createAccumulator();
        agg3_acc_internal = (org.apache.flink.table.data.RowData) converter$109.toInternalOrNull(
                (org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction.MinWithRetractAccumulator) agg3_acc_external);


        agg4_sum = ((long) -1L);
        agg4_sumIsNull = true;


        agg4_count = ((long) 0L);
        agg4_countIsNull = false;


        agg5_acc_external =
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                        .createAccumulator();
        agg5_acc_internal = (org.apache.flink.table.data.RowData) converter$107.toInternalOrNull(
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) agg5_acc_external);


        agg6_count1 = ((long) 0L);
        agg6_count1IsNull = false;


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$113 = new org.apache.flink.table.data.GenericRowData(10);


        if (agg0_sumIsNull) {
            acc$113.setField(0, null);
        } else {
            acc$113.setField(0, agg0_sum);
        }


        if (agg0_countIsNull) {
            acc$113.setField(1, null);
        } else {
            acc$113.setField(1, agg0_count);
        }


        if (agg1_sumIsNull) {
            acc$113.setField(2, null);
        } else {
            acc$113.setField(2, agg1_sum);
        }


        if (agg1_countIsNull) {
            acc$113.setField(3, null);
        } else {
            acc$113.setField(3, agg1_count);
        }


        agg2_acc_internal = (org.apache.flink.table.data.RowData) converter$107.toInternalOrNull(
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) agg2_acc_external);
        if (false) {
            acc$113.setField(4, null);
        } else {
            acc$113.setField(4, agg2_acc_internal);
        }


        agg3_acc_internal = (org.apache.flink.table.data.RowData) converter$109.toInternalOrNull(
                (org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction.MinWithRetractAccumulator) agg3_acc_external);
        if (false) {
            acc$113.setField(5, null);
        } else {
            acc$113.setField(5, agg3_acc_internal);
        }


        if (agg4_sumIsNull) {
            acc$113.setField(6, null);
        } else {
            acc$113.setField(6, agg4_sum);
        }


        if (agg4_countIsNull) {
            acc$113.setField(7, null);
        } else {
            acc$113.setField(7, agg4_count);
        }


        agg5_acc_internal = (org.apache.flink.table.data.RowData) converter$107.toInternalOrNull(
                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) agg5_acc_external);
        if (false) {
            acc$113.setField(8, null);
        } else {
            acc$113.setField(8, agg5_acc_internal);
        }


        if (agg6_count1IsNull) {
            acc$113.setField(9, null);
        } else {
            acc$113.setField(9, agg6_count1);
        }


        return acc$113;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$112 = new org.apache.flink.table.data.GenericRowData(10);


        if (true) {
            acc$112.setField(0, null);
        } else {
            acc$112.setField(0, ((long) -1L));
        }


        if (false) {
            acc$112.setField(1, null);
        } else {
            acc$112.setField(1, ((long) 0L));
        }


        if (true) {
            acc$112.setField(2, null);
        } else {
            acc$112.setField(2, ((long) -1L));
        }


        if (false) {
            acc$112.setField(3, null);
        } else {
            acc$112.setField(3, ((long) 0L));
        }


        org.apache.flink.table.data.RowData acc_internal$108 =
                (org.apache.flink.table.data.RowData) (org.apache.flink.table.data.RowData) converter$107
                        .toInternalOrNull(
                                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                                        .createAccumulator());
        if (false) {
            acc$112.setField(4, null);
        } else {
            acc$112.setField(4, acc_internal$108);
        }


        org.apache.flink.table.data.RowData acc_internal$110 =
                (org.apache.flink.table.data.RowData) (org.apache.flink.table.data.RowData) converter$109
                        .toInternalOrNull(
                                (org.apache.flink.table.planner.functions.aggfunctions.MinWithRetractAggFunction.MinWithRetractAccumulator) function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                                        .createAccumulator());
        if (false) {
            acc$112.setField(5, null);
        } else {
            acc$112.setField(5, acc_internal$110);
        }


        if (true) {
            acc$112.setField(6, null);
        } else {
            acc$112.setField(6, ((long) -1L));
        }


        if (false) {
            acc$112.setField(7, null);
        } else {
            acc$112.setField(7, ((long) 0L));
        }


        org.apache.flink.table.data.RowData acc_internal$111 =
                (org.apache.flink.table.data.RowData) (org.apache.flink.table.data.RowData) converter$107
                        .toInternalOrNull(
                                (org.apache.flink.table.planner.functions.aggfunctions.MaxWithRetractAggFunction.MaxWithRetractAccumulator) function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                                        .createAccumulator());
        if (false) {
            acc$112.setField(8, null);
        } else {
            acc$112.setField(8, acc_internal$111);
        }


        if (false) {
            acc$112.setField(9, null);
        } else {
            acc$112.setField(9, ((long) 0L));
        }


        return acc$112;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue() throws Exception {

        boolean isNull$191;
        boolean result$192;
        boolean isNull$194;
        boolean result$195;
        boolean isNull$203;
        boolean result$204;

        aggValue$209 = new org.apache.flink.table.data.GenericRowData(6);

        isNull$191 = agg0_countIsNull || false;
        result$192 = false;
        if (!isNull$191) {

            result$192 = agg0_count == ((long) 0L);

        }

        long result$193 = -1L;
        boolean isNull$193;
        if (result$192) {

            isNull$193 = true;
            if (!isNull$193) {
                result$193 = ((long) -1L);
            }
        } else {

            isNull$193 = agg0_sumIsNull;
            if (!isNull$193) {
                result$193 = agg0_sum;
            }
        }
        if (isNull$193) {
            aggValue$209.setField(0, null);
        } else {
            aggValue$209.setField(0, result$193);
        }


        isNull$194 = agg1_countIsNull || false;
        result$195 = false;
        if (!isNull$194) {

            result$195 = agg1_count == ((long) 0L);

        }

        long result$196 = -1L;
        boolean isNull$196;
        if (result$195) {

            isNull$196 = true;
            if (!isNull$196) {
                result$196 = ((long) -1L);
            }
        } else {

            isNull$196 = agg1_sumIsNull;
            if (!isNull$196) {
                result$196 = agg1_sum;
            }
        }
        if (isNull$196) {
            aggValue$209.setField(1, null);
        } else {
            aggValue$209.setField(1, result$196);
        }


        java.lang.Long value_external$197 = (java.lang.Long)
                function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                        .getValue(agg2_acc_external);
        java.lang.Long value_internal$198 =
                value_external$197;
        boolean valueIsNull$199 = value_internal$198 == null;

        if (valueIsNull$199) {
            aggValue$209.setField(2, null);
        } else {
            aggValue$209.setField(2, value_internal$198);
        }


        java.lang.Long value_external$200 = (java.lang.Long)
                function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                        .getValue(agg3_acc_external);
        java.lang.Long value_internal$201 =
                value_external$200;
        boolean valueIsNull$202 = value_internal$201 == null;

        if (valueIsNull$202) {
            aggValue$209.setField(3, null);
        } else {
            aggValue$209.setField(3, value_internal$201);
        }


        isNull$203 = agg4_countIsNull || false;
        result$204 = false;
        if (!isNull$203) {

            result$204 = agg4_count == ((long) 0L);

        }

        long result$205 = -1L;
        boolean isNull$205;
        if (result$204) {

            isNull$205 = true;
            if (!isNull$205) {
                result$205 = ((long) -1L);
            }
        } else {

            isNull$205 = agg4_sumIsNull;
            if (!isNull$205) {
                result$205 = agg4_sum;
            }
        }
        if (isNull$205) {
            aggValue$209.setField(4, null);
        } else {
            aggValue$209.setField(4, result$205);
        }


        java.lang.Long value_external$206 = (java.lang.Long)
                function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                        .getValue(agg5_acc_external);
        java.lang.Long value_internal$207 =
                value_external$206;
        boolean valueIsNull$208 = value_internal$207 == null;

        if (valueIsNull$208) {
            aggValue$209.setField(5, null);
        } else {
            aggValue$209.setField(5, value_internal$207);
        }


        return aggValue$209;

    }

    @Override
    public void cleanup() throws Exception {

        agg2$map_dataview.clear();


        agg3$map_dataview.clear();


        agg5$map_dataview.clear();


    }

    @Override
    public void close() throws Exception {

        function_org$apache$flink$table$planner$functions$aggfunctions$MaxWithRetractAggFunction$d78f624eeff2a86742b5f64899608448
                .close();


        function_org$apache$flink$table$planner$functions$aggfunctions$MinWithRetractAggFunction$00780063e1d540e25ad535dd2f326396
                .close();

    }
}
