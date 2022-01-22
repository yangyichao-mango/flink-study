package flink.examples.sql._09.udf._03_advanced_type_inference;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.inference.InputTypeStrategies;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.types.Row;

import flink.examples.sql._09.udf._03_advanced_type_inference.LastDatedValueFunction.Accumulator;

public class LastDatedValueFunction<T>
        extends AggregateFunction<Row, Accumulator<T>> {

    // --------------------------------------------------------------------------------------------
    // Planning
    // --------------------------------------------------------------------------------------------

    /**
     * Declares the {@link TypeInference} of this function. It specifies:
     *
     * <ul>
     *   <li>which argument types are supported when calling this function,
     *   <li>which {@link DataType#getConversionClass()} should be used when calling the JVM method
     *       {@link #accumulate(Accumulator, Object, LocalDate)} during runtime,
     *   <li>a similar strategy how to derive an accumulator type,
     *   <li>and a similar strategy how to derive the output type.
     * </ul>
     */
    @Override
    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
        return TypeInference.newBuilder()
                // accept a signature (ANY, DATE) both with default conversion classes,
                // the input type strategy is mostly used to produce nicer validation exceptions
                // during planning, implementers can decide to skip it if they are fine with failing
                // at a later stage during code generation when the runtime method is checked
                .inputTypeStrategy(
                        InputTypeStrategies.sequence(
                                InputTypeStrategies.ANY,
                                InputTypeStrategies.explicit(DataTypes.DATE())))
                // let the accumulator data type depend on the first input argument
                .accumulatorTypeStrategy(
                        callContext -> {
                            final DataType argDataType = callContext.getArgumentDataTypes().get(0);
                            final DataType accDataType =
                                    DataTypes.STRUCTURED(
                                            Accumulator.class,
                                            DataTypes.FIELD("value", argDataType),
                                            DataTypes.FIELD("date", DataTypes.DATE()));
                            return Optional.of(accDataType);
                        })
                // let the output data type depend on the first input argument
                .outputTypeStrategy(
                        callContext -> {
                            final DataType argDataType = callContext.getArgumentDataTypes().get(0);
                            final DataType outputDataType =
                                    DataTypes.ROW(
                                            DataTypes.FIELD("value", argDataType),
                                            DataTypes.FIELD("date", DataTypes.DATE()));
                            return Optional.of(outputDataType);
                        })
                .build();
    }

    // --------------------------------------------------------------------------------------------
    // Runtime
    // --------------------------------------------------------------------------------------------

    /**
     * Generic accumulator for representing state. It will contain different kind of instances for
     * {@code value} depending on actual call in the query.
     */
    public static class Accumulator<T> {
        public T value;
        public LocalDate date;
    }

    @Override
    public Accumulator<T> createAccumulator() {
        return new Accumulator<>();
    }

    /**
     * Generic runtime function that will be called with different kind of instances for {@code
     * input} depending on actual call in the query.
     */
    public void accumulate(Accumulator<T> acc, T input, LocalDate date) {
        if (input != null && (acc.date == null || date.isAfter(acc.date))) {
            acc.value = input;
            acc.date = date;
        }
    }

    @Override
    public Row getValue(Accumulator<T> acc) {
        return Row.of(acc.value, acc.date);
    }
}