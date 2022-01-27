package flink.examples.sql._12_data_type._02_user_defined;

import java.math.BigDecimal;

import org.apache.flink.table.annotation.DataTypeHint;

public class User {

    public int age;
    public String name;

    public @DataTypeHint("DECIMAL(10, 2)") BigDecimal totalBalance;
}
