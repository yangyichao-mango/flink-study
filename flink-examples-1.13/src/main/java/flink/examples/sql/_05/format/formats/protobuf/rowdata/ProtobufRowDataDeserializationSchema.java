package flink.examples.sql._05.format.formats.protobuf.rowdata;

import static java.lang.String.format;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.util.Preconditions;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import flink.examples.sql._05.format.formats.protobuf.row.ProtobufUtils;
import flink.examples.sql._05.format.formats.protobuf.row.typeutils.ProtobufSchemaConverter;


public class ProtobufRowDataDeserializationSchema extends AbstractDeserializationSchema<RowData> {

    /**
     * Protobuf message class for deserialization. Might be null if message class is not available.
     */
    private Class<? extends Message> messageClazz;

    /**
     * Protobuf serialization descriptorBytes
     */
    private byte[] descriptorBytes;

    /**
     * Protobuf serialization descriptor.
     */
    private transient Descriptors.Descriptor descriptor;

    /**
     * Type information describing the result type.
     */
    private transient RowType protobufOriginalRowType;

    /** Flag indicating whether to ignore invalid fields/rows (default: throw an exception). */
    private final boolean ignoreParseErrors;

    /** TypeInformation of the produced {@link RowData}. */
    private RowType expectedResultType;

    /**
     * Protobuf defaultInstance for descriptor
     */
    private transient Message defaultInstance;

    private ProtobufToRowDataConverters.ProtobufToRowDataConverter runtimeConverter;

    /**
     * Creates a Protobuf deserialization descriptor for the given message class. Having the
     * concrete Protobuf message class might improve performance.
     *
     * @param messageClazz Protobuf message class used to deserialize Protobuf's message to Flink's row
     * @param ignoreParseErrors
     */
    public ProtobufRowDataDeserializationSchema(
            Class<? extends GeneratedMessageV3> messageClazz
            , boolean ignoreParseErrors
            , RowType expectedResultType) {
        this.ignoreParseErrors = ignoreParseErrors;
        Preconditions.checkNotNull(messageClazz, "Protobuf message class must not be null.");
        this.messageClazz = messageClazz;
        this.descriptorBytes = null;
        this.descriptor = ProtobufUtils.getDescriptor(messageClazz);
        this.defaultInstance = ProtobufUtils.getDefaultInstance(messageClazz);

        // protobuf 本身的 schema
        this.protobufOriginalRowType = (RowType) ProtobufSchemaConverter.convertToRowDataTypeInfo(messageClazz);

        this.expectedResultType = expectedResultType;

        this.runtimeConverter = new ProtobufToRowDataConverters(false)
                .createRowDataConverterByLogicalType(this.descriptor, this.expectedResultType);
    }

    /**
     * Creates a Protobuf deserialization descriptor for the given Protobuf descriptorBytes.
     *
     * @param descriptorBytes Protobuf descriptorBytes to deserialize Protobuf's message to Flink's row
     * @param ignoreParseErrors
     */
//    public ProtobufRowDataDeserializationSchema(
//            byte[] descriptorBytes
//            , boolean ignoreParseErrors
//            , RowType expectedResultType) {
//        this.ignoreParseErrors = ignoreParseErrors;
//        Preconditions.checkNotNull(descriptorBytes, "Protobuf descriptorBytes must not be null.");
//        this.messageClazz = null;
//        this.descriptorBytes = descriptorBytes;
//        this.descriptor = ProtobufUtils.getDescriptor(descriptorBytes);
////        this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.descriptor);
//        this.defaultInstance = DynamicMessage.newBuilder(this.descriptor).getDefaultInstanceForType();
////        this.runtimeConverter = new ProtobufToRowDataConverters(true)
////                .createRowDataConverter(this.descriptor, this.typeInfo, null);
//
//        this.expectedResultType = expectedResultType;
//    }

    @Override
    public RowData deserialize(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        try {

            Message message = this.defaultInstance
                    .newBuilderForType()
                    .mergeFrom(bytes)
                    .build();

            return (RowData) runtimeConverter.convert(message);
        } catch (Throwable t) {
            if (ignoreParseErrors) {
                return null;
            }
            throw new IOException(
                    format("Failed to deserialize Protobuf '%s'.", new String(bytes)), t);
        }
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        if (Objects.nonNull(this.messageClazz)) {
            outputStream.writeObject(this.messageClazz);
            outputStream.writeObject(this.expectedResultType);
        } else {
            outputStream.writeObject(this.descriptorBytes);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {

        Object o = inputStream.readObject();

        this.expectedResultType = (RowType) inputStream.readObject();

        if (o instanceof Class) {
            this.messageClazz = (Class<? extends Message>) o;
            this.descriptor = ProtobufUtils.getDescriptor(messageClazz);
            this.defaultInstance = ProtobufUtils.getDefaultInstance(messageClazz);

            this.protobufOriginalRowType = (RowType) ProtobufSchemaConverter.convertToRowDataTypeInfo(messageClazz);

            this.runtimeConverter = new ProtobufToRowDataConverters(false)
                    .createRowDataConverterByDescriptor(this.descriptor, this.expectedResultType);
        } else {
//            this.descriptorBytes = (byte[]) o;
//            this.descriptor = ProtobufUtils.getDescriptor(this.descriptorBytes);
//            this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.descriptor);
//            this.defaultInstance = DynamicMessage.newBuilder(this.descriptor).getDefaultInstanceForType();
        }
    }
}
