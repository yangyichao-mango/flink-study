package flink.examples.sql._09.udf._05_scalar_function;

import java.util.Set;

import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.functions.TableFunction;


public class ExplodeUDTF extends TableFunction<String> {

    public void eval(@DataTypeHint("RAW") Object test) {

        Set<String> test1 = (Set<String>) test;

        for (String t : test1) {
            collect(t);
        }
    }

}

