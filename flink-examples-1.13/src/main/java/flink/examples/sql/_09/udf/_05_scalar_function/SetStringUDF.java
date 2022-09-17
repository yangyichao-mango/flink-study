package flink.examples.sql._09.udf._05_scalar_function;

import java.util.Set;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.functions.ScalarFunction;

import com.google.common.collect.Sets;


public class SetStringUDF extends ScalarFunction {

    @DataTypeHint("RAW")
    public Object eval(String input) {
        return Sets.newHashSet(input, input + "_1", input + "_2");
    }

    @Override
    public TypeInformation<?> getResultType(Class<?>[] signature) {
        return TypeInformation.of(new TypeHint<Set<String>>() {
        });
    }

}
