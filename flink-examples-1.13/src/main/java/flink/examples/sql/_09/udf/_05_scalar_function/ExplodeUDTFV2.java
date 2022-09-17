package flink.examples.sql._09.udf._05_scalar_function;

import org.apache.flink.table.functions.TableFunction;


public class ExplodeUDTFV2 extends TableFunction<String[]> {

    public void eval(String worlds) {

        collect(new String[]{ worlds, worlds + "111"});
        collect(new String[]{ worlds, worlds + "222"});
    }

}

