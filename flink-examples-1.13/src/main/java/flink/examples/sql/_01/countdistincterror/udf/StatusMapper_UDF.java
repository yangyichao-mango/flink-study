package flink.examples.sql._01.countdistincterror.udf;

import org.apache.flink.table.functions.TableFunction;


public class StatusMapper_UDF extends TableFunction<String> {

    private int i = 0;

    public void eval(String status) throws InterruptedException {

        if (i == 6) {
            Thread.sleep(2000L);
        }

        if (i == 5) {
            collect("等级4");
        } else {
            if ("1".equals(status)) {
                collect("等级1");
            } else if ("2".equals(status)) {
                collect("等级2");
            } else if ("3".equals(status)) {
                collect("等级3");
            }
        }
        i++;
    }

}
