package flink.examples.sql._07.query._03_group_agg._01_group_agg;


public final class GroupAggsHandler$39 implements org.apache.flink.table.runtime.generated.AggsHandleFunction {

    long agg0_count1;
    boolean agg0_count1IsNull;
    long agg1_sum;
    boolean agg1_sumIsNull;
    long agg2_sum;
    boolean agg2_sumIsNull;
    long agg2_count;
    boolean agg2_countIsNull;
    long agg3_min;
    boolean agg3_minIsNull;
    long agg4_max;
    boolean agg4_maxIsNull;
    org.apache.flink.table.data.GenericRowData acc$2 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData acc$3 = new org.apache.flink.table.data.GenericRowData(6);
    org.apache.flink.table.data.GenericRowData aggValue$38 = new org.apache.flink.table.data.GenericRowData(5);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    public GroupAggsHandler$39(Object[] references) throws Exception {

    }

    private org.apache.flink.api.common.functions.RuntimeContext getRuntimeContext() {
        return store.getRuntimeContext();
    }

    @Override
    public void open(org.apache.flink.table.runtime.dataview.StateDataViewStore store) throws Exception {
        this.store = store;

    }

    @Override
    public void accumulate(org.apache.flink.table.data.RowData accInput) throws Exception {

        boolean isNull$10;
        long result$11;
        long field$12;
        boolean isNull$12;
        boolean isNull$13;
        long result$14;
        boolean isNull$17;
        long result$18;
        boolean isNull$20;
        long result$21;
        boolean isNull$23;
        boolean result$24;
        boolean isNull$28;
        boolean result$29;
        isNull$12 = accInput.isNullAt(1);
        field$12 = -1L;
        if (!isNull$12) {
            field$12 = accInput.getLong(1);
        }


        isNull$10 = agg0_count1IsNull || false;
        result$11 = -1L;
        if (!isNull$10) {
            result$11 = (long) (agg0_count1 + ((long) 1L));
        }
        agg0_count1 = result$11;
        agg0_count1IsNull = isNull$10;

        long result$16 = -1L;
        boolean isNull$16;
        if (isNull$12) {
            isNull$16 = agg1_sumIsNull;
            if (!isNull$16) {
                result$16 = agg1_sum;
            }
        } else {
            long result$15 = -1L;
            boolean isNull$15;
            if (agg1_sumIsNull) {
                isNull$15 = isNull$12;
                if (!isNull$15) {
                    result$15 = field$12;
                }
            } else {
                isNull$13 = agg1_sumIsNull || isNull$12;
                result$14 = -1L;
                if (!isNull$13) {
                    result$14 = (long) (agg1_sum + field$12);
                }

                isNull$15 = isNull$13;
                if (!isNull$15) {
                    result$15 = result$14;
                }
            }
            isNull$16 = isNull$15;
            if (!isNull$16) {
                result$16 = result$15;
            }
        }
        agg1_sum = result$16;
        ;
        agg1_sumIsNull = isNull$16;


        long result$19 = -1L;
        boolean isNull$19;
        if (isNull$12) {

            isNull$19 = agg2_sumIsNull;
            if (!isNull$19) {
                result$19 = agg2_sum;
            }
        } else {


            isNull$17 = agg2_sumIsNull || isNull$12;
            result$18 = -1L;
            if (!isNull$17) {

                result$18 = (long) (agg2_sum + field$12);

            }

            isNull$19 = isNull$17;
            if (!isNull$19) {
                result$19 = result$18;
            }
        }
        agg2_sum = result$19;
        ;
        agg2_sumIsNull = isNull$19;


        long result$22 = -1L;
        boolean isNull$22;
        if (isNull$12) {

            isNull$22 = agg2_countIsNull;
            if (!isNull$22) {
                result$22 = agg2_count;
            }
        } else {


            isNull$20 = agg2_countIsNull || false;
            result$21 = -1L;
            if (!isNull$20) {

                result$21 = (long) (agg2_count + ((long) 1L));

            }

            isNull$22 = isNull$20;
            if (!isNull$22) {
                result$22 = result$21;
            }
        }
        agg2_count = result$22;
        ;
        agg2_countIsNull = isNull$22;


        long result$27 = -1L;
        boolean isNull$27;
        if (isNull$12) {

            isNull$27 = agg3_minIsNull;
            if (!isNull$27) {
                result$27 = agg3_min;
            }
        } else {
            long result$26 = -1L;
            boolean isNull$26;
            if (agg3_minIsNull) {

                isNull$26 = isNull$12;
                if (!isNull$26) {
                    result$26 = field$12;
                }
            } else {
                isNull$23 = isNull$12 || agg3_minIsNull;
                result$24 = false;
                if (!isNull$23) {

                    result$24 = field$12 < agg3_min;

                }

                long result$25 = -1L;
                boolean isNull$25;
                if (result$24) {

                    isNull$25 = isNull$12;
                    if (!isNull$25) {
                        result$25 = field$12;
                    }
                } else {

                    isNull$25 = agg3_minIsNull;
                    if (!isNull$25) {
                        result$25 = agg3_min;
                    }
                }
                isNull$26 = isNull$25;
                if (!isNull$26) {
                    result$26 = result$25;
                }
            }
            isNull$27 = isNull$26;
            if (!isNull$27) {
                result$27 = result$26;
            }
        }
        agg3_min = result$27;
        ;
        agg3_minIsNull = isNull$27;


        long result$32 = -1L;
        boolean isNull$32;
        if (isNull$12) {

            isNull$32 = agg4_maxIsNull;
            if (!isNull$32) {
                result$32 = agg4_max;
            }
        } else {
            long result$31 = -1L;
            boolean isNull$31;
            if (agg4_maxIsNull) {

                isNull$31 = isNull$12;
                if (!isNull$31) {
                    result$31 = field$12;
                }
            } else {
                isNull$28 = isNull$12 || agg4_maxIsNull;
                result$29 = false;
                if (!isNull$28) {

                    result$29 = field$12 > agg4_max;

                }

                long result$30 = -1L;
                boolean isNull$30;
                if (result$29) {

                    isNull$30 = isNull$12;
                    if (!isNull$30) {
                        result$30 = field$12;
                    }
                } else {

                    isNull$30 = agg4_maxIsNull;
                    if (!isNull$30) {
                        result$30 = agg4_max;
                    }
                }
                isNull$31 = isNull$30;
                if (!isNull$31) {
                    result$31 = result$30;
                }
            }
            isNull$32 = isNull$31;
            if (!isNull$32) {
                result$32 = result$31;
            }
        }
        agg4_max = result$32;
        ;
        agg4_maxIsNull = isNull$32;


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

        long field$4;
        boolean isNull$4;
        long field$5;
        boolean isNull$5;
        long field$6;
        boolean isNull$6;
        long field$7;
        boolean isNull$7;
        long field$8;
        boolean isNull$8;
        long field$9;
        boolean isNull$9;
        isNull$8 = acc.isNullAt(4);
        field$8 = -1L;
        if (!isNull$8) {
            field$8 = acc.getLong(4);
        }
        isNull$4 = acc.isNullAt(0);
        field$4 = -1L;
        if (!isNull$4) {
            field$4 = acc.getLong(0);
        }
        isNull$5 = acc.isNullAt(1);
        field$5 = -1L;
        if (!isNull$5) {
            field$5 = acc.getLong(1);
        }
        isNull$7 = acc.isNullAt(3);
        field$7 = -1L;
        if (!isNull$7) {
            field$7 = acc.getLong(3);
        }
        isNull$9 = acc.isNullAt(5);
        field$9 = -1L;
        if (!isNull$9) {
            field$9 = acc.getLong(5);
        }
        isNull$6 = acc.isNullAt(2);
        field$6 = -1L;
        if (!isNull$6) {
            field$6 = acc.getLong(2);
        }

        agg0_count1 = field$4;
        ;
        agg0_count1IsNull = isNull$4;


        agg1_sum = field$5;
        ;
        agg1_sumIsNull = isNull$5;


        agg2_sum = field$6;
        ;
        agg2_sumIsNull = isNull$6;


        agg2_count = field$7;
        ;
        agg2_countIsNull = isNull$7;


        agg3_min = field$8;
        ;
        agg3_minIsNull = isNull$8;


        agg4_max = field$9;
        ;
        agg4_maxIsNull = isNull$9;


    }

    @Override
    public void resetAccumulators() throws Exception {


        agg0_count1 = ((long) 0L);
        agg0_count1IsNull = false;


        agg1_sum = ((long) -1L);
        agg1_sumIsNull = true;


        agg2_sum = ((long) 0L);
        agg2_sumIsNull = false;


        agg2_count = ((long) 0L);
        agg2_countIsNull = false;


        agg3_min = ((long) -1L);
        agg3_minIsNull = true;


        agg4_max = ((long) -1L);
        agg4_maxIsNull = true;


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {


        acc$3 = new org.apache.flink.table.data.GenericRowData(6);


        if (agg0_count1IsNull) {
            acc$3.setField(0, null);
        } else {
            acc$3.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            acc$3.setField(1, null);
        } else {
            acc$3.setField(1, agg1_sum);
        }


        if (agg2_sumIsNull) {
            acc$3.setField(2, null);
        } else {
            acc$3.setField(2, agg2_sum);
        }


        if (agg2_countIsNull) {
            acc$3.setField(3, null);
        } else {
            acc$3.setField(3, agg2_count);
        }


        if (agg3_minIsNull) {
            acc$3.setField(4, null);
        } else {
            acc$3.setField(4, agg3_min);
        }


        if (agg4_maxIsNull) {
            acc$3.setField(5, null);
        } else {
            acc$3.setField(5, agg4_max);
        }


        return acc$3;

    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {


        acc$2 = new org.apache.flink.table.data.GenericRowData(6);


        if (false) {
            acc$2.setField(0, null);
        } else {
            acc$2.setField(0, ((long) 0L));
        }


        if (true) {
            acc$2.setField(1, null);
        } else {
            acc$2.setField(1, ((long) -1L));
        }


        if (false) {
            acc$2.setField(2, null);
        } else {
            acc$2.setField(2, ((long) 0L));
        }


        if (false) {
            acc$2.setField(3, null);
        } else {
            acc$2.setField(3, ((long) 0L));
        }


        if (true) {
            acc$2.setField(4, null);
        } else {
            acc$2.setField(4, ((long) -1L));
        }


        if (true) {
            acc$2.setField(5, null);
        } else {
            acc$2.setField(5, ((long) -1L));
        }


        return acc$2;

    }

    @Override
    public org.apache.flink.table.data.RowData getValue() throws Exception {

        boolean isNull$33;
        boolean result$34;
        boolean isNull$35;
        long result$36;

        aggValue$38 = new org.apache.flink.table.data.GenericRowData(5);


        if (agg0_count1IsNull) {
            aggValue$38.setField(0, null);
        } else {
            aggValue$38.setField(0, agg0_count1);
        }


        if (agg1_sumIsNull) {
            aggValue$38.setField(1, null);
        } else {
            aggValue$38.setField(1, agg1_sum);
        }


        isNull$33 = agg2_countIsNull || false;
        result$34 = false;
        if (!isNull$33) {

            result$34 = agg2_count == ((long) 0L);

        }

        long result$37 = -1L;
        boolean isNull$37;
        if (result$34) {

            isNull$37 = true;
            if (!isNull$37) {
                result$37 = ((long) -1L);
            }
        } else {


            isNull$35 = agg2_sumIsNull || agg2_countIsNull;
            result$36 = -1L;
            if (!isNull$35) {

                result$36 = (long) (agg2_sum / agg2_count);

            }

            isNull$37 = isNull$35;
            if (!isNull$37) {
                result$37 = result$36;
            }
        }
        if (isNull$37) {
            aggValue$38.setField(2, null);
        } else {
            aggValue$38.setField(2, result$37);
        }


        if (agg3_minIsNull) {
            aggValue$38.setField(3, null);
        } else {
            aggValue$38.setField(3, agg3_min);
        }


        if (agg4_maxIsNull) {
            aggValue$38.setField(4, null);
        } else {
            aggValue$38.setField(4, agg4_max);
        }


        return aggValue$38;

    }

    @Override
    public void cleanup() throws Exception {


    }

    @Override
    public void close() throws Exception {

    }
}