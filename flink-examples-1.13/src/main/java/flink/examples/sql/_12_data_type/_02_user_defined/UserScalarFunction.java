package flink.examples.sql._12_data_type._02_user_defined;

import java.math.BigDecimal;

import org.apache.flink.table.functions.ScalarFunction;

public class UserScalarFunction extends ScalarFunction {

    // 1. 自定义数据类型作为输出参数
    public User eval(long i) {
        if (i > 0 && i <= 5) {
            User u = new User();
            u.age = (int) i;
            u.name = "name1";
            u.totalBalance = new BigDecimal(1.1d);
            return u;
        } else {
            User u = new User();
            u.age = (int) i;
            u.name = "name2";
            u.totalBalance = new BigDecimal(2.2d);
            return u;
        }
    }

    // 2. 自定义数据类型作为输入参数
    public String eval(User i) {
        if (i.age > 0 && i.age <= 5) {
            User u = new User();
            u.age = 1;
            u.name = "name1";
            u.totalBalance = new BigDecimal(1.1d);
            return u.name;
        } else {
            User u = new User();
            u.age = 2;
            u.name = "name2";
            u.totalBalance = new BigDecimal(2.2d);
            return u.name;
        }
    }

}
