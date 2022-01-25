package flink.examples.sql._03.source_sink.abilities.sink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.connector.sink.abilities.SupportsOverwrite;
import org.apache.flink.table.connector.sink.abilities.SupportsPartitioning;
import org.apache.flink.table.connector.sink.abilities.SupportsWritingMetadata;
import org.apache.flink.table.types.DataType;

import com.google.common.collect.Maps;

import flink.examples.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Abilities_TableSink implements DynamicTableSink
        , SupportsOverwrite
        , SupportsPartitioning
        , SupportsWritingMetadata {

    private final DataType type;
    private final String printIdentifier;
    private final boolean stdErr;
    private final @Nullable
    Integer parallelism;
    private boolean overwrite = false;
    private Map<String, String> staticPartition;

    public Abilities_TableSink(
            DataType type, String printIdentifier, boolean stdErr, Integer parallelism) {
        this.type = type;
        this.printIdentifier = printIdentifier;
        this.stdErr = stdErr;
        this.parallelism = parallelism;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return requestedMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(DynamicTableSink.Context context) {
        DataStructureConverter converter = context.createDataStructureConverter(type);
        return SinkFunctionProvider.of(
                new Abilities_SinkFunction(converter, printIdentifier, stdErr), parallelism);
    }

    @Override
    public DynamicTableSink copy() {
        return new Abilities_TableSink(type, printIdentifier, stdErr, parallelism);
    }

    @Override
    public String asSummaryString() {
        return "Print to " + (stdErr ? "System.err" : "System.out");
    }

    @Override
    public void applyOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    @Override
    public void applyStaticPartition(Map<String, String> partition) {
        this.staticPartition = Maps.newHashMap(partition);
    }

    @Override
    public Map<String, DataType> listWritableMetadata() {
        return new HashMap<String, DataType>() {{
            put("test_metadata", DataTypes.BIGINT());
        }};
    }

    @Override
    public void applyWritableMetadata(List<String> metadataKeys, DataType consumedDataType) {
        log.info("metadataKeys:" + JacksonUtils.bean2Json(metadataKeys));
        log.info("consumedDataType:" + JacksonUtils.bean2Json(consumedDataType));
    }
}