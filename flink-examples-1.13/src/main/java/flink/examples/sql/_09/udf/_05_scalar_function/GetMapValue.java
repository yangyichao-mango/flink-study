package flink.examples.sql._09.udf._05_scalar_function;

import java.util.Map;

import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.functions.ScalarFunction;

public class GetMapValue extends ScalarFunction {

    public String eval(@DataTypeHint("RAW") Object map, String key) {

        Map<String, Object> innerMap = (Map<String, Object>) map;
        try {
            Object obj = innerMap.get(key);
            if (obj != null) {
                return obj.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
