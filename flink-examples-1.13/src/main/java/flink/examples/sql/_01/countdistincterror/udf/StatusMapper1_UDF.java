package flink.examples.sql._01.countdistincterror.udf;

import org.apache.flink.table.functions.ScalarFunction;


public class StatusMapper1_UDF extends ScalarFunction {

    private int i = 0;

    public String eval(String status) {

        if (i == 5) {
            i++;
            return "等级4";
        } else {
            i++;
            if ("1".equals(status)) {
                return "等级1";
            } else if ("2".equals(status)) {
                return "等级2";
            } else if ("3".equals(status)) {
                return "等级3";
            }
        }
        return "未知";
    }

}
