package flink.examples.sql._03.source_sink.abilities.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.table.data.RowData;

import com.google.common.collect.ImmutableMap;

import flink.examples.JacksonUtils;

public class Abilities_SourceFunction extends RichSourceFunction<RowData> {

    private DeserializationSchema<RowData> dser;

    private long limit = -1;

    private volatile boolean isCancel = false;

    private boolean enableSourceWatermark = false;

    public Abilities_SourceFunction(DeserializationSchema<RowData> dser) {
        this.dser = dser;
    }

    public Abilities_SourceFunction(DeserializationSchema<RowData> dser, long limit) {
        this.dser = dser;
        this.limit = limit;
    }

    public Abilities_SourceFunction(DeserializationSchema<RowData> dser, boolean enableSourceWatermark) {
        this.dser = dser;
        this.enableSourceWatermark = enableSourceWatermark;
    }

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {
        int i = 0;
        while (!this.isCancel) {

            long currentTimeMills = System.currentTimeMillis();

            ctx.collect(this.dser.deserialize(
                    JacksonUtils.bean2Json(ImmutableMap.of(
                            "user_id", 11111L + i
                            , "name", "antigeneral"
                            , "flink_read_timestamp", currentTimeMills + "")).getBytes()
            ));
            Thread.sleep(1000);
            i++;

            if (limit >= 0 && i > limit) {
                this.isCancel = true;
            }

            if (enableSourceWatermark) {
                ctx.emitWatermark(new Watermark(currentTimeMills));
            }
        }
    }

    @Override
    public void cancel() {
        this.isCancel = true;
    }
}
