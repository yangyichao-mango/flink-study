package flink.examples.sql._09.udf._03_advanced_type_inference;

import java.time.LocalDate;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.types.Row;

public class AdvancedFunctionsExample {

    public static void main(String[] args) throws Exception {
        // setup the environment
        final EnvironmentSettings settings =
                EnvironmentSettings.newInstance().inBatchMode().build();
        final TableEnvironment env = TableEnvironment.create(settings);

        // execute different kinds of functions
        executeLastDatedValueFunction(env);
        executeInternalRowMergerFunction(env);
    }

    /**
     * Aggregates data by name and returns the latest non-null {@code item_count} value with its
     * corresponding {@code order_date}.
     */
    private static void executeLastDatedValueFunction(TableEnvironment env) {
        // create a table with example data
        final Table customers =
                env.fromValues(
                        DataTypes.of("ROW<name STRING, order_date DATE, item_count INT>"),
                        Row.of("Guillermo Smith", LocalDate.parse("2020-12-01"), 3),
                        Row.of("Guillermo Smith", LocalDate.parse("2020-12-05"), 5),
                        Row.of("Valeria Mendoza", LocalDate.parse("2020-03-23"), 4),
                        Row.of("Valeria Mendoza", LocalDate.parse("2020-06-02"), 10),
                        Row.of("Leann Holloway", LocalDate.parse("2020-05-26"), 9),
                        Row.of("Leann Holloway", LocalDate.parse("2020-05-27"), null),
                        Row.of("Brandy Sanders", LocalDate.parse("2020-10-14"), 1),
                        Row.of("John Turner", LocalDate.parse("2020-10-02"), 12),
                        Row.of("Ellen Ortega", LocalDate.parse("2020-06-18"), 100));
        env.createTemporaryView("customers", customers);

        // register and execute the function
        env.createTemporarySystemFunction("LastDatedValueFunction", LastDatedValueFunction.class);
        env.executeSql(
                "SELECT name, LastDatedValueFunction(item_count, order_date) "
                        + "FROM customers GROUP BY name")
                .print();

        // clean up
        env.dropTemporaryView("customers");
    }

    /** Merges two rows as efficient as possible using internal data structures. */
    private static void executeInternalRowMergerFunction(TableEnvironment env) {
        // create a table with example data
        final Table customers =
                env.fromValues(
                        DataTypes.of(
                                "ROW<name STRING, data1 ROW<birth_date DATE>, data2 ROW<city STRING, phone STRING>>"),
                        Row.of(
                                "Guillermo Smith",
                                Row.of(LocalDate.parse("1992-12-12")),
                                Row.of("New Jersey", "816-443-8010")),
                        Row.of(
                                "Valeria Mendoza",
                                Row.of(LocalDate.parse("1970-03-28")),
                                Row.of("Los Angeles", "928-264-9662")),
                        Row.of(
                                "Leann Holloway",
                                Row.of(LocalDate.parse("1989-05-21")),
                                Row.of("Eugene", "614-889-6038")));
        env.createTemporaryView("customers", customers);

        // register and execute the function
        env.createTemporarySystemFunction(
                "InternalRowMergerFunction", InternalRowMergerFunction.class);
        env.executeSql("SELECT name, InternalRowMergerFunction(data1, data2) FROM customers")
                .print();

        // clean up
        env.dropTemporaryView("customers");
    }

}
