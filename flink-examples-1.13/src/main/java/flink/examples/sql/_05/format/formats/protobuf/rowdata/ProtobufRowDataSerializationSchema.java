package flink.examples.sql._05.format.formats.protobuf.rowdata;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.table.data.RowData;


public class ProtobufRowDataSerializationSchema implements SerializationSchema<RowData> {
    @Override
    public byte[] serialize(RowData element) {
        return new byte[0];
    }
}
