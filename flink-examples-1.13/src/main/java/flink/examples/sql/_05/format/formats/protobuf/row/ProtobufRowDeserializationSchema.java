package flink.examples.sql._05.format.formats.protobuf.row;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;

import flink.examples.sql._05.format.formats.protobuf.row.typeutils.ProtobufSchemaConverter;


public class ProtobufRowDeserializationSchema extends AbstractDeserializationSchema<Row> {
    /**
     * Used for time conversions into SQL types.
     */
    private static final TimeZone LOCAL_TZ = TimeZone.getDefault();

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
    private transient RowTypeInfo typeInfo;

    /**
     * Protobuf defaultInstance for descriptor
     */
    private transient Message defaultInstance;

    private transient DeserializationRuntimeConverter deserializationRuntimeConverter;

    @FunctionalInterface
    interface DeserializationRuntimeConverter extends Serializable {
        Object convert(Object object);
    }

    /**
     * Creates a Protobuf deserialization descriptor for the given message class. Having the
     * concrete Protobuf message class might improve performance.
     *
     * @param messageClazz Protobuf message class used to deserialize Protobuf's message to Flink's row
     */
    public ProtobufRowDeserializationSchema(Class<? extends GeneratedMessageV3> messageClazz) {
        Preconditions.checkNotNull(messageClazz, "Protobuf message class must not be null.");
        this.messageClazz = messageClazz;
        this.descriptorBytes = null;
        this.descriptor = ProtobufUtils.getDescriptor(messageClazz);
        this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(messageClazz);
        this.defaultInstance = ProtobufUtils.getDefaultInstance(messageClazz);
        this.deserializationRuntimeConverter = this.createRowConverter(this.descriptor, this.typeInfo);
    }

    /**
     * Creates a Protobuf deserialization descriptor for the given Protobuf descriptorBytes.
     *
     * @param descriptorBytes Protobuf descriptorBytes to deserialize Protobuf's message to Flink's row
     */
    public ProtobufRowDeserializationSchema(byte[] descriptorBytes) {
        Preconditions.checkNotNull(descriptorBytes, "Protobuf descriptorBytes must not be null.");
        this.messageClazz = null;
        this.descriptorBytes = descriptorBytes;
        this.descriptor = ProtobufUtils.getDescriptor(descriptorBytes);
        this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.descriptor);
        this.defaultInstance = DynamicMessage.newBuilder(this.descriptor).getDefaultInstanceForType();
        this.deserializationRuntimeConverter = createRowConverter(this.descriptor, this.typeInfo);
    }

    @Override
    public Row deserialize(byte[] bytes) throws IOException {
        try {
            Message message = this.defaultInstance
                    .newBuilderForType()
                    .mergeFrom(bytes)
                    .build();
            return (Row) this.deserializationRuntimeConverter.convert(message);
        } catch (Exception e) {
            throw new IOException("Failed to deserialize Protobuf message.", e);
        }
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return this.typeInfo;
    }

    // --------------------------------------------------------------------------------------------

    private DeserializationRuntimeConverter createRowConverter(
            Descriptors.Descriptor descriptor, RowTypeInfo rowTypeInfo) {
        final FieldDescriptor[] fieldDescriptors =
                descriptor.getFields().toArray(new FieldDescriptor[0]);
        final TypeInformation<?>[] fieldTypeInfos = rowTypeInfo.getFieldTypes();

        final int length = fieldDescriptors.length;

        final DeserializationRuntimeConverter[] deserializationRuntimeConverters = new DeserializationRuntimeConverter[length];

        for (int i = 0; i < length; i++) {
            final FieldDescriptor fieldDescriptor = fieldDescriptors[i];
            final TypeInformation<?> fieldTypeInfo = fieldTypeInfos[i];
            deserializationRuntimeConverters[i] = createConverter(fieldDescriptor, fieldTypeInfo);
        }

        return (Object o) -> {
            Message message = (Message) o;
            final Row row = new Row(length);
            for (int i = 0; i < length; i++) {
                Object fieldO = message.getField(fieldDescriptors[i]);
                row.setField(i, deserializationRuntimeConverters[i].convert(fieldO));
            }
            return row;
        };
    }

    @SuppressWarnings("unchecked")
    private DeserializationRuntimeConverter createConverter(Descriptors.GenericDescriptor genericDescriptor, TypeInformation<?> info) {
        // we perform the conversion based on descriptor information but enriched with pre-computed
        // type information where useful (i.e., for list)

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
                case STRING:
                    if (info instanceof ListTypeInfo) {
                        // list
                        TypeInformation<?> elementTypeInfo = ((ListTypeInfo) info).getElementTypeInfo();

                        return this.createListConverter(elementTypeInfo);
                    } else {
                        return this.createObjectConverter(info);
                    }
                case ENUM:
                    if (info instanceof ListTypeInfo) {
                        // list
                        return (Object o) -> ((List) o)
                                .stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    } else {
                        return Object::toString;
                    }
                case GROUP:
                case MESSAGE:
                    if (info instanceof ListTypeInfo) {
                        // list
                        TypeInformation<?> elementTypeInfo = ((ListTypeInfo) info).getElementTypeInfo();
                        Descriptors.Descriptor elementDescriptor = fieldDescriptor.getMessageType();

                        DeserializationRuntimeConverter elementConverter = this.createConverter(elementDescriptor, elementTypeInfo);

                        return (Object o) -> ((List) o)
                                .stream()
                                .map(elementConverter::convert)
                                .collect(Collectors.toList());

                    } else if (info instanceof MapTypeInfo) {
                        // map
                        final MapTypeInfo<?, ?> mapTypeInfo = (MapTypeInfo<?, ?>) info;

                        boolean isDynamicMessage = false;

                        if (this.messageClazz == null) {
                            isDynamicMessage = true;
                        }

                        // todo map's key only support string
                        final DeserializationRuntimeConverter keyConverter = Object::toString;

                        final FieldDescriptor keyFieldDescriptor =
                                fieldDescriptor.getMessageType().getFields().get(0);

                        final FieldDescriptor valueFieldDescriptor =
                                fieldDescriptor.getMessageType().getFields().get(1);

                        final TypeInformation<?> valueTypeInfo =
                                mapTypeInfo.getValueTypeInfo();

                        final DeserializationRuntimeConverter valueConverter =
                                createConverter(valueFieldDescriptor, valueTypeInfo);

                        if (isDynamicMessage) {

                            return (Object o) -> {
                                final List<DynamicMessage> dynamicMessages = (List<DynamicMessage>) o;

                                final Map<String, Object> convertedMap = new HashMap<>(dynamicMessages.size());

                                dynamicMessages.forEach((DynamicMessage dynamicMessage) -> {
                                    convertedMap.put(
                                            (String) keyConverter.convert(dynamicMessage.getField(keyFieldDescriptor))
                                            , valueConverter.convert(dynamicMessage.getField(valueFieldDescriptor)));
                                });

                                return convertedMap;
                            };

                        } else {

                            return (Object o) -> {
                                final List<MapEntry> mapEntryList = (List<MapEntry>) o;
                                final Map<String, Object> convertedMap = new HashMap<>(mapEntryList.size());
                                mapEntryList.forEach((MapEntry message) -> {
                                    convertedMap.put(
                                            (String) keyConverter.convert(message.getKey())
                                            , valueConverter.convert(message.getValue()));
                                });

                                return convertedMap;
                            };
                        }
                    } else if (info instanceof RowTypeInfo) {
                        // row
                        return createRowConverter(((FieldDescriptor) genericDescriptor).getMessageType(), (RowTypeInfo) info);
                    }
                    throw new IllegalStateException("Message expected but was: ");
                case BYTES:

                    return (Object o) -> {
                        final byte[] bytes = ((ByteString) o).toByteArray();
                        if (Types.BIG_DEC == info) {
                            return convertToDecimal(bytes);
                        }
                        return bytes;
                    };
            }
        }

        throw new IllegalArgumentException("Unsupported Protobuf type '" + genericDescriptor.getName() + "'.");

    }

    @SuppressWarnings("unchecked")
    private DeserializationRuntimeConverter createListConverter(TypeInformation<?> info) {

        DeserializationRuntimeConverter elementConverter;

        if (Types.SQL_DATE == info) {

            elementConverter = this::convertToDate;

        } else if (Types.SQL_TIME == info) {

            elementConverter = this::convertToTime;
        } else {

            elementConverter = (Object fieldO) -> (fieldO);
        }

        return (Object o) -> ((List) o)
                .stream()
                .map(elementConverter::convert)
                .collect(Collectors.toList());
    }

    private DeserializationRuntimeConverter createObjectConverter(TypeInformation<?> info) {
        if (Types.SQL_DATE == info) {
            return this::convertToDate;
        } else if (Types.SQL_TIME == info) {
            return this::convertToTime;
        } else {
            return (Object o) -> o;
        }
    }

    // --------------------------------------------------------------------------------------------

    private BigDecimal convertToDecimal(byte[] bytes) {
        return new BigDecimal(new BigInteger(bytes));
    }

    private Date convertToDate(Object object) {
        final long millis;
        if (object instanceof Integer) {
            final Integer value = (Integer) object;
            // adopted from Apache Calcite
            final long t = (long) value * 86400000L;
            millis = t - (long) LOCAL_TZ.getOffset(t);
        } else {
            // use 'provided' Joda time
            final LocalDate value = (LocalDate) object;
            millis = value.toDate().getTime();
        }
        return new Date(millis);
    }

    private Time convertToTime(Object object) {
        final long millis;
        if (object instanceof Integer) {
            millis = (Integer) object;
        } else {
            // use 'provided' Joda time
            final LocalTime value = (LocalTime) object;
            millis = value.get(DateTimeFieldType.millisOfDay());
        }
        return new Time(millis - LOCAL_TZ.getOffset(millis));
    }

    private Timestamp convertToTimestamp(Object object) {
        final long millis;
        if (object instanceof Long) {
            millis = (Long) object;
        } else {
            // use 'provided' Joda time
            final DateTime value = (DateTime) object;
            millis = value.toDate().getTime();
        }
        return new Timestamp(millis - LOCAL_TZ.getOffset(millis));
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
            this.typeInfo = (RowTypeInfo) ProtobufSchemaConverter.convertToTypeInfo(this.descriptor);
            this.defaultInstance = DynamicMessage.newBuilder(this.descriptor).getDefaultInstanceForType();
        }
        this.deserializationRuntimeConverter = this.createConverter(this.descriptor, this.typeInfo);
    }
}
