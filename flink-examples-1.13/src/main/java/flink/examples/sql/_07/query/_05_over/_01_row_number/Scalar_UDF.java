package flink.examples.sql._07.query._05_over._01_row_number;

import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;


public class Scalar_UDF extends ScalarFunction {

    @Override
    public void open(FunctionContext context) throws Exception {
        super.open(context);


    }

    public int eval(Long id, int remainder) {
        return (int) (id % remainder);
    }

}
