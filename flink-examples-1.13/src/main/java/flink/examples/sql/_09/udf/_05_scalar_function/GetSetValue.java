package flink.examples.sql._09.udf._05_scalar_function;

import java.util.Set;

import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.functions.ScalarFunction;

public class GetSetValue extends ScalarFunction {

    public String eval(@DataTypeHint("RAW") Object set) {

        Set<String> s = (Set<String>) set;

        return s.iterator().next();
    }
}
