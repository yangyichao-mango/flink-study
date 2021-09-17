package flink.examples.sql._03.source_sink.table.user_defined;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.RowData;

import com.google.common.collect.ImmutableMap;

import flink.examples.JacksonUtils;

public class UserDefinedSource extends RichSourceFunction<RowData> {

    private DeserializationSchema<RowData> dser;

    private volatile boolean isCancel;

    public UserDefinedSource(DeserializationSchema<RowData> dser) {
        this.dser = dser;
    }

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {
        while (!this.isCancel) {
            ctx.collect(this.dser.deserialize(
                    JacksonUtils.bean2Json(ImmutableMap.of("user_id", 1111L, "name", "antigeneral")).getBytes()
            ));
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        this.isCancel = true;
    }
}
