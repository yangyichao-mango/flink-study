package flink.examples.sql._09.udf._03_advanced_type_inference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.utils.JoinedRowData;
import org.apache.flink.table.functions.FunctionDefinition;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.inference.ArgumentCount;
import org.apache.flink.table.types.inference.CallContext;
import org.apache.flink.table.types.inference.ConstantArgumentCount;
import org.apache.flink.table.types.inference.InputTypeStrategy;
import org.apache.flink.table.types.inference.Signature;
import org.apache.flink.table.types.inference.Signature.Argument;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.table.types.logical.LogicalTypeRoot;

public class InternalRowMergerFunction extends ScalarFunction {

    // --------------------------------------------------------------------------------------------
    // Planning
    // --------------------------------------------------------------------------------------------

    @Override
    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
        return TypeInference.newBuilder()
                // accept a signature (ROW, ROW) with arbitrary field types but
                // with internal conversion classes
                .inputTypeStrategy(
                        new InputTypeStrategy() {
                            @Override
                            public ArgumentCount getArgumentCount() {
                                // the argument count is checked before input types are inferred
                                return ConstantArgumentCount.of(2);
                            }

                            @Override
                            public Optional<List<DataType>> inferInputTypes(
                                    CallContext callContext, boolean throwOnFailure) {
                                final List<DataType> args = callContext.getArgumentDataTypes();
                                final DataType arg0 = args.get(0);
                                final DataType arg1 = args.get(1);
                                // perform some basic validation based on the logical type
                                if (arg0.getLogicalType().getTypeRoot() != LogicalTypeRoot.ROW
                                        || arg1.getLogicalType().getTypeRoot()
                                        != LogicalTypeRoot.ROW) {
                                    if (throwOnFailure) {
                                        throw callContext.newValidationError(
                                                "Two row arguments expected.");
                                    }
                                    return Optional.empty();
                                }
                                // keep the original logical type but express that both arguments
                                // should use internal data structures
                                return Optional.of(
                                        Arrays.asList(
                                                arg0.bridgedTo(RowData.class),
                                                arg1.bridgedTo(RowData.class)));
                            }

                            @Override
                            public List<Signature> getExpectedSignatures(
                                    FunctionDefinition definition) {
                                // this helps in printing nice error messages
                                return Collections.singletonList(
                                        Signature.of(Argument.of("ROW"), Argument.of("ROW")));
                            }
                        })
                .outputTypeStrategy(
                        callContext -> {
                            // merge fields and give them a unique name
                            final List<DataType> args = callContext.getArgumentDataTypes();
                            final List<DataType> allFieldDataTypes = new ArrayList<>();
                            allFieldDataTypes.addAll(args.get(0).getChildren());
                            allFieldDataTypes.addAll(args.get(1).getChildren());
                            final DataTypes.Field[] fields =
                                    IntStream.range(0, allFieldDataTypes.size())
                                            .mapToObj(
                                                    i ->
                                                            DataTypes.FIELD(
                                                                    "f" + i,
                                                                    allFieldDataTypes.get(i)))
                                            .toArray(DataTypes.Field[]::new);
                            // create a new row with the merged fields and express that the return
                            // type will use an internal data structure
                            return Optional.of(DataTypes.ROW(fields).bridgedTo(RowData.class));
                        })
                .build();
    }

    // --------------------------------------------------------------------------------------------
    // Runtime
    // --------------------------------------------------------------------------------------------

    public RowData eval(RowData r1, RowData r2) {
        return new JoinedRowData(r1, r2);
    }
}