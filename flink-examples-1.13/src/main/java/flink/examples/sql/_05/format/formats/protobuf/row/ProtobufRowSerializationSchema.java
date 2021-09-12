package flink.examples.sql._05.format.formats.protobuf.row;


import static com.google.protobuf.Descriptors.FieldDescriptor.Type.ENUM;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;
import com.google.protobuf.WireFormat;

import flink.examples.sql._05.format.formats.protobuf.row.typeutils.ProtobufSchemaConverter;


/**
 * Serialization schema that serializes {@link Row} into Protobuf bytes.
 *
 * <p>Serializes objects that are represented in (nested) Flink rows. It support types that
 * are compatible with Flink's Table & SQL API.
 *
 * <p>Note: Changes in this class need to be kept in sync with the corresponding runtime
 * class {@link ProtobufRowDeserializationSchema} and schema converter {@link flink.formats.protobuf.typeutils.ProtobufSchemaConverter}.
 */
public class ProtobufRowSerializationSchema implements SerializationSchema<Row> {

    private static final long serialVersionUID = 2098447220136965L;

    /**
     * Used for time conversions from SQL types.
     */
    private static final TimeZone LOCAL_TZ = TimeZone.getDefault();

    /**
     * Protobuf message class for serialization. Might be null if message class is not available.
     */
    private Class<? extends Message> messageClazz;

    /**
     * DescriptorBytes for deserialization.
     */
    private byte[] descriptorBytes;

    /**
     * Type information describing the result type.
     */
    private transient RowTypeInfo typeInfo;

    private transient Descriptors.Descriptor descriptor;

    private transient Message defaultInstance;

    private transient SerializationRuntimeConverter serializationRuntimeConverter;

    @FunctionalInterface
    interface SerializationRuntimeConverter extends Serializable {
        Object convert(Object object);
    }

    /**
     * Creates an Protobuf serialization schema for the given message class.
     *
     * @param messageClazz Protobuf message class used to serialize Flink's row to Protobuf's message
     */
    public ProtobufRowSerializationSchema(Class<? extends GeneratedMessageV3> messageClazz) {
        Preconditions.checkNotNull(messageClazz, "Protobuf message class must not be null.");
        this.messageClazz = messageClazz;
        this.descriptorBytes = null;
        this.descriptor = ProtobufUtils.getDescriptor(this.messageClazz);
        this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.messageClazz);
        this.defaultInstance = ProtobufUtils.getDefaultInstance(this.messageClazz);
        this.serializationRuntimeConverter = this.createRowConverter(this.descriptor, this.typeInfo);
    }

    /**
     * Creates an Protobuf serialization schema for the given descriptorBytes.
     *
     * @param descriptorBytes descriptorBytes used to serialize Flink's row to Protobuf's message
     */
    public ProtobufRowSerializationSchema(byte[] descriptorBytes) {
        Preconditions.checkNotNull(descriptorBytes, "Protobuf message class must not be null.");
        this.messageClazz = null;
        this.descriptorBytes = descriptorBytes;
        this.descriptor = ProtobufUtils.getDescriptor(this.descriptorBytes);
        this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(descriptorBytes);
        this.defaultInstance = ProtobufUtils.getDefaultInstance(descriptorBytes);
        this.serializationRuntimeConverter = this.createRowConverter(this.descriptor, this.typeInfo);
    }

    @Override
    public byte[] serialize(Row row) {
        try {
            // convert to message
            Message message = (Message) this.serializationRuntimeConverter.convert(row);
            return message.toByteArray();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to serialize row.", e);
        }
    }

    private SerializationRuntimeConverter createRowConverter(Descriptors.Descriptor descriptor, RowTypeInfo rowTypeInfo) {
        final FieldDescriptor[] fieldDescriptors =
                descriptor.getFields().toArray(new FieldDescriptor[0]);
        final TypeInformation<?>[] fieldTypeInfos = rowTypeInfo.getFieldTypes();

        final int length = fieldDescriptors.length;

        final SerializationRuntimeConverter[] serializationRuntimeConverters = new SerializationRuntimeConverter[length];

        for (int i = 0; i < length; ++i) {
            final FieldDescriptor fieldDescriptor = fieldDescriptors[i];
            final TypeInformation<?> fieldTypeInfo = fieldTypeInfos[i];
            serializationRuntimeConverters[i] = createConverter(fieldDescriptor, fieldTypeInfo);
        }

        return (Object o) -> {
            Row row = (Row) o;
            final DynamicMessage.Builder dynamicMessageBuilder = DynamicMessage.newBuilder(descriptor);
            for (int i = 0; i < length; i++) {
                Object fieldO = row.getField(i);
                dynamicMessageBuilder.setField(fieldDescriptors[i], serializationRuntimeConverters[i].convert(fieldO));
            }
            return dynamicMessageBuilder.build();
        };
    }

    private SerializationRuntimeConverter createListConverter(TypeInformation<?> info) {
        if (info instanceof ListTypeInfo) {
            // list

            return (Object o) -> {
                List<Object> results = new ArrayList<>(((List<?>) o).size());
                for (Object fieldO : ((List<?>) o)) {
                    if (fieldO instanceof Date) {
                        results.add(this.convertFromDate((Date) fieldO));
                    } else if (fieldO instanceof Time) {
                        results.add(this.convertFromTime((Time) fieldO));
                    } else if (fieldO instanceof Timestamp) {
                        results.add(convertFromTimestamp((Timestamp) fieldO));
                    } else {
                        results.add(fieldO);
                    }
                }
                return results;
            };
        } else {

            return (Object o) -> {
                if (o instanceof Date) {
                    return this.convertFromDate((Date) o);
                } else if (o instanceof Time) {
                    return this.convertFromTime((Time) o);
                } else if (o instanceof Timestamp) {
                    return convertFromTimestamp((Timestamp) o);
                } else {
                    return o;
                }
            };
        }
    }

    @SuppressWarnings("unchecked")
    private SerializationRuntimeConverter createConverter(Descriptors.GenericDescriptor genericDescriptor, TypeInformation<?> info) {

        if (genericDescriptor instanceof Descriptors.Descriptor) {

            return createRowConverter((Descriptors.Descriptor) genericDescriptor, (RowTypeInfo) info);

        } else if (genericDescriptor instanceof FieldDescriptor) {

            FieldDescriptor fieldDescriptor = ((FieldDescriptor) genericDescriptor);

            // field
            switch (fieldDescriptor.getType()) {
                case INT32:
                case FIXED32:
                case UINT32:
                case SFIXED32:
                case SINT32:
                case INT64:
                case UINT64:
                case FIXED64:
                case SFIXED64:
                case SINT64:
                case DOUBLE:
                case FLOAT:
                case BOOL:
                    // check for logical type
                    return createListConverter(info);
                case STRING:
                case ENUM:
                    if (info instanceof ListTypeInfo) {
                        // list
                        return (Object o) -> new ArrayList<>((List<?>) o)
                                .stream()
                                .map((Object fieldO) -> convertFromEnum(fieldDescriptor, fieldO))
                                .collect(Collectors.toList());
                    } else {
                        return (Object o) -> convertFromEnum(fieldDescriptor, o);
                    }
                case GROUP:
                case MESSAGE:
                    if (info instanceof ListTypeInfo) {
                        // list
                        TypeInformation<?> elementTypeInfo = ((ListTypeInfo) info).getElementTypeInfo();
                        Descriptors.Descriptor elementDescriptor = fieldDescriptor.getMessageType();

                        SerializationRuntimeConverter elementConverter = this.createConverter(elementDescriptor, elementTypeInfo);

                        return (Object o) -> ((List) o)
                                .stream()
                                .map(elementConverter::convert)
                                .collect(Collectors.toList());

                    } else if (info instanceof MapTypeInfo) {
                        // map

                        final Descriptors.Descriptor messageType = fieldDescriptor.getMessageType();
                        final WireFormat.FieldType keyFieldType = fieldDescriptor.getMessageType().getFields().get(0).getLiteType();
                        final WireFormat.FieldType valueFieldType = fieldDescriptor.getMessageType().getFields().get(1).getLiteType();
                        final FieldDescriptor valueFieldDescriptor = fieldDescriptor.getMessageType().getFields().get(1);
                        final TypeInformation<?> valueTypeInfo = ((MapTypeInfo) info).getValueTypeInfo();

                        SerializationRuntimeConverter valueConverter = createConverter(valueFieldDescriptor, valueTypeInfo);

                        return (Object o) -> {
                            final List<MapEntry<?, ?>> pbMapEntries = new ArrayList<>(((Map<?, ?>) o).size());
                            for (Map.Entry<?, ?> mapEntry : ((Map<?, ?>) o).entrySet()) {
                                pbMapEntries.add(MapEntry.newDefaultInstance(
                                        messageType
                                        , keyFieldType
                                        , mapEntry.getKey()
                                        , valueFieldType
                                        , valueConverter.convert(mapEntry.getValue())));
                            }
                            return pbMapEntries;
                        };
                    } else if (info instanceof RowTypeInfo) {
                        // row
                        return createRowConverter(fieldDescriptor.getMessageType(), (RowTypeInfo) info);
                    }
                    throw new IllegalStateException("Message expected but was: ");
                case BYTES:
                    // check for logical type

                    return (Object o) -> {
                        if (o instanceof BigDecimal) {
                            return convertFromDecimal((BigDecimal) o);
                        }
                        return o;
                    };
            }
        }
        throw new RuntimeException("error");
    }

    private byte[] convertFromDecimal(BigDecimal decimal) {
        // byte array must contain the two's-complement representation of the
        // unscaled integer value in big-endian byte order
        return decimal.unscaledValue().toByteArray();
    }

    private int convertFromDate(Date date) {
        final long time = date.getTime();
        final long converted = time + (long) LOCAL_TZ.getOffset(time);
        return (int) (converted / 86400000L);
    }

    private int convertFromTime(Time date) {
        final long time = date.getTime();
        final long converted = time + (long) LOCAL_TZ.getOffset(time);
        return (int) (converted % 86400000L);
    }

    private long convertFromTimestamp(Timestamp date) {
        // adopted from Apache Calcite
        final long time = date.getTime();
        return time + (long) LOCAL_TZ.getOffset(time);
    }

    private Object convertFromEnum(FieldDescriptor fieldDescriptor, Object object) {
        if (ENUM == fieldDescriptor.getType()) {

            Descriptors.EnumDescriptor enumDescriptor = fieldDescriptor.getEnumType();

            Descriptors.EnumValueDescriptor enumValue = null;

            for (Descriptors.EnumValueDescriptor enumValueDescriptor : enumDescriptor.getValues()) {
                if (enumValueDescriptor.toString().equals(object)) {
                    enumValue = enumValueDescriptor;
                }
            }

            if (null != enumValue) {
                return enumValue;
            } else {
                throw new NoSuchElementException(String.format(fieldDescriptor.getFullName() + " enumValues has not such element [%s]", object));
            }
        } else {
            return object.toString();
        }
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        if (Objects.nonNull(this.messageClazz)) {
            outputStream.writeObject(this.messageClazz);
        } else {
            outputStream.writeObject(this.descriptorBytes);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {

        Object o = inputStream.readObject();

        if (o instanceof Class) {
            this.messageClazz = (Class<? extends Message>) o;
            this.descriptor = ProtobufUtils.getDescriptor(this.messageClazz);
            this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.messageClazz);
            this.defaultInstance = ProtobufUtils.getDefaultInstance(this.messageClazz);
        } else {
            this.descriptorBytes = (byte[]) o;
            this.descriptor = ProtobufUtils.getDescriptor(this.descriptorBytes);
            this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.descriptorBytes);
            this.defaultInstance = DynamicMessage.newBuilder(this.descriptor).getDefaultInstanceForType();
        }
        this.serializationRuntimeConverter = this.createConverter(this.descriptor, this.typeInfo);
    }
}
