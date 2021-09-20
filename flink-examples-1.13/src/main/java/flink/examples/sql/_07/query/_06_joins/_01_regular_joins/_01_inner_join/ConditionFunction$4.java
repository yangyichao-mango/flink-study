package flink.examples.sql._07.query._06_joins._01_regular_joins._01_inner_join;


public class ConditionFunction$4 extends org.apache.flink.api.common.functions.AbstractRichFunction
        implements org.apache.flink.table.runtime.generated.JoinCondition {


    public ConditionFunction$4(Object[] references) throws Exception {
    }


    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {

    }

    @Override
    public boolean apply(org.apache.flink.table.data.RowData in1, org.apache.flink.table.data.RowData in2) {


        return true;
    }

    @Override
    public void close() throws Exception {
        super.close();

    }
}