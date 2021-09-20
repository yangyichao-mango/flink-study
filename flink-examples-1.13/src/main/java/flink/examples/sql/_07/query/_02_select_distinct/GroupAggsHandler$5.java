package flink.examples.sql._07.query._02_select_distinct;


public final class GroupAggsHandler$5 implements org.apache.flink.table.runtime.generated.AggsHandleFunction {

    org.apache.flink.table.data.GenericRowData acc$2 = new org.apache.flink.table.data.GenericRowData(0);
    org.apache.flink.table.data.GenericRowData acc$3 = new org.apache.flink.table.data.GenericRowData(0);
    org.apache.flink.table.data.GenericRowData aggValue$4 = new org.apache.flink.table.data.GenericRowData(0);

    private org.apache.flink.table.runtime.dataview.StateDataViewStore store;

    public GroupAggsHandler$5(Object[] references) throws Exception {

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


    }

    @Override
    public void resetAccumulators() throws Exception {


    }

    @Override
    public org.apache.flink.table.data.RowData getAccumulators() throws Exception {
        acc$3 = new org.apache.flink.table.data.GenericRowData(0);
        return acc$3;
    }

    @Override
    public org.apache.flink.table.data.RowData createAccumulators() throws Exception {
        acc$2 = new org.apache.flink.table.data.GenericRowData(0);
        return acc$2;
    }

    @Override
    public org.apache.flink.table.data.RowData getValue() throws Exception {
        aggValue$4 = new org.apache.flink.table.data.GenericRowData(0);
        return aggValue$4;
    }

    @Override
    public void cleanup() throws Exception {


    }

    @Override
    public void close() throws Exception {

    }
}