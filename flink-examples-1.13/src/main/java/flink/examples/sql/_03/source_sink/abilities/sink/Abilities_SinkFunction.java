package flink.examples.sql._03.source_sink.abilities.sink;

import org.apache.flink.api.common.functions.util.PrintSinkOutputWriter;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.table.connector.sink.DynamicTableSink.DataStructureConverter;
import org.apache.flink.table.data.RowData;

public class Abilities_SinkFunction extends RichSinkFunction<RowData> {

    private static final long serialVersionUID = 1L;

    private final DataStructureConverter converter;
    private final PrintSinkOutputWriter<String> writer;

    public Abilities_SinkFunction(
            DataStructureConverter converter, String printIdentifier, boolean stdErr) {
        this.converter = converter;
        this.writer = new PrintSinkOutputWriter<>(printIdentifier, stdErr);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        StreamingRuntimeContext context = (StreamingRuntimeContext) getRuntimeContext();
        writer.open(context.getIndexOfThisSubtask(), context.getNumberOfParallelSubtasks());
    }

    @Override
    public void invoke(RowData value, SinkFunction.Context context) {
        Object data = converter.toExternal(value);
        assert data != null;
        writer.write(data.toString());
    }
}