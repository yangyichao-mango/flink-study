package flink.examples.sql._03.source_sink.abilities.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.RowData;

import com.google.common.collect.ImmutableMap;

import flink.examples.JacksonUtils;

public class Abilities_SourceFunction extends RichSourceFunction<RowData> {

    private DeserializationSchema<RowData> dser;

    private long limit;

    private volatile boolean isCancel;

    public Abilities_SourceFunction(DeserializationSchema<RowData> dser) {
        this.dser = dser;
    }

    public Abilities_SourceFunction(DeserializationSchema<RowData> dser, long limit) {
        this.dser = dser;
        this.limit = limit;
    }

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {
        int i = 0;
        while (!this.isCancel && i <= limit) {
            ctx.collect(this.dser.deserialize(
                    JacksonUtils.bean2Json(ImmutableMap.of(
                            "user_id", 1111L
                            , "name", "antigeneral"
                            , "flink_read_timestamp", System.currentTimeMillis() + "")).getBytes()
            ));
            Thread.sleep(1000);
            i++;
        }
    }

    @Override
    public void cancel() {
        this.isCancel = true;
    }
}
