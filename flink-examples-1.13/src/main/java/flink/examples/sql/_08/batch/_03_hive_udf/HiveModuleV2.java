package flink.examples.sql._08.batch._03_hive_udf;

import static org.apache.flink.util.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.table.catalog.hive.client.HiveShim;
import org.apache.flink.table.catalog.hive.client.HiveShimLoader;
import org.apache.flink.table.catalog.hive.factories.HiveFunctionDefinitionFactory;
import org.apache.flink.table.functions.FunctionDefinition;
import org.apache.flink.table.module.Module;
import org.apache.flink.table.module.hive.udf.generic.GenericUDFLegacyGroupingID;
import org.apache.flink.table.module.hive.udf.generic.HiveGenericUDFGrouping;
import org.apache.flink.util.StringUtils;
import org.apache.hadoop.hive.ql.exec.FunctionInfo;

public class HiveModuleV2 implements Module {


    // a set of functions that shouldn't be overridden by HiveModule
    @VisibleForTesting
    static final Set<String> BUILT_IN_FUNC_BLACKLIST =
            Collections.unmodifiableSet(
                    new HashSet<>(
                            Arrays.asList(
                                    "count",
                                    "cume_dist",
                                    "current_date",
                                    "current_timestamp",
                                    "dense_rank",
                                    "first_value",
                                    "lag",
                                    "last_value",
                                    "lead",
                                    "ntile",
                                    "rank",
                                    "row_number",
                                    "hop",
                                    "hop_end",
                                    "hop_proctime",
                                    "hop_rowtime",
                                    "hop_start",
                                    "percent_rank",
                                    "session",
                                    "session_end",
                                    "session_proctime",
                                    "session_rowtime",
                                    "session_start",
                                    "tumble",
                                    "tumble_end",
                                    "tumble_proctime",
                                    "tumble_rowtime",
                                    "tumble_start")));

    private final HiveFunctionDefinitionFactory factory;
    private final String hiveVersion;
    private final HiveShim hiveShim;
    private Set<String> functionNames;

    public HiveModuleV2() {
        this(HiveShimLoader.getHiveVersion());
    }

    public HiveModuleV2(String hiveVersion) {
        checkArgument(
                !StringUtils.isNullOrWhitespaceOnly(hiveVersion), "hiveVersion cannot be null");

        this.hiveVersion = hiveVersion;
        this.hiveShim = HiveShimLoader.loadHiveShim(hiveVersion);
        this.factory = new HiveFunctionDefinitionFactory(hiveShim);
        this.functionNames = new HashSet<>();
        this.map = new HashMap<>();
    }

    @Override
    public Set<String> listFunctions() {
        // lazy initialize
        if (functionNames.isEmpty()) {
            functionNames = hiveShim.listBuiltInFunctions();
            functionNames.removeAll(BUILT_IN_FUNC_BLACKLIST);
            functionNames.add("grouping");
            functionNames.add(GenericUDFLegacyGroupingID.NAME);
            functionNames.addAll(map.keySet());
        }
        return functionNames;
    }

    @Override
    public Optional<FunctionDefinition> getFunctionDefinition(String name) {
        if (BUILT_IN_FUNC_BLACKLIST.contains(name)) {
            return Optional.empty();
        }
        // We override Hive's grouping function. Refer to the implementation for more details.
        if (name.equalsIgnoreCase("grouping")) {
            return Optional.of(
                    factory.createFunctionDefinitionFromHiveFunction(
                            name, HiveGenericUDFGrouping.class.getName()));
        }

        // this function is used to generate legacy GROUPING__ID value for old hive versions
        if (name.equalsIgnoreCase(GenericUDFLegacyGroupingID.NAME)) {
            return Optional.of(
                    factory.createFunctionDefinitionFromHiveFunction(
                            name, GenericUDFLegacyGroupingID.class.getName()));
        }

        Optional<FunctionInfo> info = hiveShim.getBuiltInFunctionInfo(name);

        if (info.isPresent()) {
            return info.map(
                    functionInfo ->
                            factory.createFunctionDefinitionFromHiveFunction(
                                    name, functionInfo.getFunctionClass().getName()));
        } else {
            return Optional.ofNullable(this.map.get(name))
                    .map(hiveUDFClassName -> factory.createFunctionDefinitionFromHiveFunction(name, hiveUDFClassName));
        }
    }

    public String getHiveVersion() {
        return hiveVersion;
    }

    private final Map<String, String> map;

    public void registryHiveUDF(String hiveUDFName, String hiveUDFClassName) {
        this.map.put(hiveUDFName, hiveUDFClassName);
    }
}
