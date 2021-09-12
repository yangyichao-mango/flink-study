package flink.examples.sql._05.format.formats.protobuf.rowdata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.flink.table.data.GenericArrayData;
import org.apache.flink.table.data.GenericMapData;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.types.logical.ArrayType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.MapType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.types.logical.TimeType;
import org.apache.flink.table.types.logical.VarCharType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;


public class ProtobufToRowDataConverters implements Serializable {

    /**
     * Used for time conversions into SQL types.
     */
    private static final TimeZone LOCAL_TZ = TimeZone.getDefault();

    private final boolean isDynamicMessage;

    public ProtobufToRowDataConverters(boolean isDynamicMessage) {
        this.isDynamicMessage = isDynamicMessage;
    }


    @FunctionalInterface
    public interface ProtobufToRowDataConverter extends Serializable {
        Object convert(Object object);
    }

    public ProtobufToRowDataConverter createRowDataConverterByLogicalType(
            Descriptors.Descriptor descriptor
            , RowType rowType) {
        final FieldDescriptor[] fieldDescriptors =
                descriptor.getFields().toArray(new FieldDescriptor[0]);

        List<LogicalType> fieldLogicalTypes = rowType.getChildren();

        final int length = fieldDescriptors.length;

        final ProtobufToRowDataConverter[] runtimeConverters = new ProtobufToRowDataConverter[length];

        for (int i = 0; i < length; i++) {
            final FieldDescriptor fieldDescriptor = fieldDescriptors[i];
            final LogicalType fieldLogicalType = fieldLogicalTypes.get(i);
            runtimeConverters[i] = createConverterByLogicalType(fieldDescriptor, fieldLogicalType);
        }

        return (Object o) -> {
            Message message = (Message) o;
            final GenericRowData genericRowData = new GenericRowData(length);
            for (int i = 0; i < length; i++) {
                Object fieldO = message.getField(fieldDescriptors[i]);
                genericRowData.setField(i, runtimeConverters[i].convert(fieldO));
            }
            return genericRowData;
        };
    }

    @SuppressWarnings("unchecked")
    private ProtobufToRowDataConverter createConverterByLogicalType(Descriptors.GenericDescriptor genericDescriptor, LogicalType info) {
        // we perform the conversion based on descriptor information but enriched with pre-computed
        // type information where useful (i.e., for list)

        if (info instanceof RowType) {
            return createRowDataConverterByDescriptor((Descriptors.Descriptor) genericDescriptor, (RowType) info);
        } else {

            FieldDescriptor fieldDescriptor = ((FieldDescriptor) genericDescriptor);

            switch (info.getTypeRoot()) {
                case CHAR:
                case VARCHAR:
                case BOOLEAN:
                case DECIMAL:
                case TINYINT:
                case SMALLINT:
                case INTEGER:
                case BIGINT:
                case FLOAT:
                case DOUBLE:
                    if (info instanceof ArrayType) {
                        // list
                        LogicalType elementLogicalType = ((ArrayType) info).getElementType();

                        return createArrayConverter(elementLogicalType);
                    } else {
                        return createObjectConverter(info);
                    }
                case ARRAY:
                case MULTISET:
                    // list
                    LogicalType elementLogicalType = ((ArrayType) info).getElementType();

                    if (fieldDescriptor.getJavaType() != JavaType.MESSAGE) {
                        // list
                        return createArrayConverter(elementLogicalType);
                    }

                    Descriptors.Descriptor elementDescriptor = fieldDescriptor.getMessageType();

                    ProtobufToRowDataConverter elementConverter = this.createConverterByDescriptor(elementDescriptor, elementLogicalType);

                    return (Object o) -> new GenericArrayData(((List) o)
                            .stream()
                            .map(elementConverter::convert)
                            .toArray());
                case ROW:
                    // row
                    return createRowDataConverterByDescriptor(((FieldDescriptor) genericDescriptor).getMessageType(), (RowType) info);
                case MAP:
                    // map
                    final MapType mapTypeInfo = (MapType) info;

                    // todo map's key only support string
                    final ProtobufToRowDataConverter keyConverter = Object::toString;

                    final FieldDescriptor keyFieldDescriptor =
                            fieldDescriptor.getMessageType().getFields().get(0);

                    final FieldDescriptor valueFieldDescriptor =
                            fieldDescriptor.getMessageType().getFields().get(1);

                    final LogicalType valueTypeInfo =
                            mapTypeInfo.getValueType();

                    final ProtobufToRowDataConverter valueConverter =
                            createConverterByDescriptor(valueFieldDescriptor, valueTypeInfo);

                    if (this.isDynamicMessage) {

                        return (Object o) -> {
                            final List<DynamicMessage> dynamicMessages = (List<DynamicMessage>) o;

                            final Map<StringData, Object> convertedMap = new HashMap<>(dynamicMessages.size());

                            dynamicMessages.forEach((DynamicMessage dynamicMessage) -> {
                                convertedMap.put(
                                        StringData.fromString((String) keyConverter.convert(dynamicMessage.getField(keyFieldDescriptor)))
                                        , valueConverter.convert(dynamicMessage.getField(valueFieldDescriptor)));
                            });

                            return new GenericMapData(convertedMap);
                        };

                    } else {

                        return (Object o) -> {
                            final List<MapEntry> mapEntryList = (List<MapEntry>) o;
                            final Map<StringData, Object> convertedMap = new HashMap<>(mapEntryList.size());
                            mapEntryList.forEach((MapEntry message) -> {
                                convertedMap.put(
                                        StringData.fromString((String) keyConverter.convert(message.getKey()))
                                        , valueConverter.convert(message.getValue()));
                            });

                            return new GenericMapData(convertedMap);
                        };
                    }
                case BINARY:
                case VARBINARY:
                    return (Object o) -> {
                        final byte[] bytes = ((ByteString) o).toByteArray();
                        if (info instanceof DecimalType) {
                            return convertToDecimal(bytes);
                        }
                        return bytes;
                    };
            }
        }

        throw new IllegalArgumentException("Unsupported Protobuf type '" + genericDescriptor.getName() + "'.");

    }

    public ProtobufToRowDataConverter createRowDataConverterByDescriptor(
            Descriptors.Descriptor descriptor
            , RowType rowType) {
        final FieldDescriptor[] fieldDescriptors =
                descriptor.getFields().toArray(new FieldDescriptor[0]);
//        final TypeInformation<?>[] fieldTypeInfos = rowTypeInfo.getFieldTypes();

        List<LogicalType> fieldLogicalTypes = rowType.getChildren();

        final int length = fieldDescriptors.length;

        final ProtobufToRowDataConverter[] runtimeConverters = new ProtobufToRowDataConverter[length];

        for (int i = 0; i < length; i++) {
            final FieldDescriptor fieldDescriptor = fieldDescriptors[i];
            final LogicalType fieldLogicalType = fieldLogicalTypes.get(i);
            runtimeConverters[i] = createConverterByDescriptor(fieldDescriptor, fieldLogicalType);
        }

        return (Object o) -> {
            Message message = (Message) o;
            final GenericRowData genericRowData = new GenericRowData(length);
            for (int i = 0; i < length; i++) {
                Object fieldO = message.getField(fieldDescriptors[i]);
                genericRowData.setField(i, runtimeConverters[i].convert(fieldO));
            }
            return genericRowData;
        };
    }

    @SuppressWarnings("unchecked")
    private ProtobufToRowDataConverter createConverterByDescriptor(Descriptors.GenericDescriptor genericDescriptor, LogicalType info) {
        // we perform the conversion based on descriptor information but enriched with pre-computed
        // type information where useful (i.e., for list)

        if (genericDescriptor instanceof Descriptors.Descriptor) {

            return createRowDataConverterByDescriptor((Descriptors.Descriptor) genericDescriptor, (RowType) info);

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
                    if (info instanceof ArrayType) {
                        // list
                        LogicalType elementLogicalType = ((ArrayType) info).getElementType();

                        return createArrayConverter(elementLogicalType);
                    } else {
                        return createObjectConverter(info);
                    }
                case ENUM:
                    if (info instanceof ArrayType) {

                        // list
                        return (Object o) -> new GenericArrayData(((List) o)
                                .stream()
                                .map(Object::toString)
                                .toArray());
                    } else {
                        return Object::toString;
                    }
                case GROUP:
                case MESSAGE:
                    if (info instanceof ArrayType) {
                        // list
                        LogicalType elementLogicalType = ((ArrayType) info).getElementType();
                        Descriptors.Descriptor elementDescriptor = fieldDescriptor.getMessageType();

                        ProtobufToRowDataConverter elementConverter = this.createConverterByDescriptor(elementDescriptor, elementLogicalType);

                        return (Object o) -> new GenericArrayData(((List) o)
                                .stream()
                                .map(elementConverter::convert)
                                .toArray());

                    } else if (info instanceof MapType) {
                        // map
                        final MapType mapTypeInfo = (MapType) info;

                        // todo map's key only support string
                        final ProtobufToRowDataConverter keyConverter = Object::toString;

                        final FieldDescriptor keyFieldDescriptor =
                                fieldDescriptor.getMessageType().getFields().get(0);

                        final FieldDescriptor valueFieldDescriptor =
                                fieldDescriptor.getMessageType().getFields().get(1);

                        final LogicalType valueTypeInfo =
                                mapTypeInfo.getValueType();

                        final ProtobufToRowDataConverter valueConverter =
                                createConverterByDescriptor(valueFieldDescriptor, valueTypeInfo);

                        if (this.isDynamicMessage) {

                            return (Object o) -> {
                                final List<DynamicMessage> dynamicMessages = (List<DynamicMessage>) o;

                                final Map<StringData, Object> convertedMap = new HashMap<>(dynamicMessages.size());

                                dynamicMessages.forEach((DynamicMessage dynamicMessage) -> {
                                    convertedMap.put(
                                            StringData.fromString((String) keyConverter.convert(dynamicMessage.getField(keyFieldDescriptor)))
                                            , valueConverter.convert(dynamicMessage.getField(valueFieldDescriptor)));
                                });

                                return new GenericMapData(convertedMap);
                            };

                        } else {

                            return (Object o) -> {
                                final List<MapEntry> mapEntryList = (List<MapEntry>) o;
                                final Map<StringData, Object> convertedMap = new HashMap<>(mapEntryList.size());
                                mapEntryList.forEach((MapEntry message) -> {
                                    convertedMap.put(
                                            StringData.fromString((String) keyConverter.convert(message.getKey()))
                                            , valueConverter.convert(message.getValue()));
                                });

                                return new GenericMapData(convertedMap);
                            };
                        }
                    } else if (info instanceof RowType) {
                        // row
                        return createRowDataConverterByDescriptor(((FieldDescriptor) genericDescriptor).getMessageType(), (RowType) info);
                    }
                    throw new IllegalStateException("Message expected but was: ");
                case BYTES:

                    return (Object o) -> {
                        final byte[] bytes = ((ByteString) o).toByteArray();
                        if (info instanceof DecimalType) {
                            return convertToDecimal(bytes);
                        }
                        return bytes;
                    };
            }
        }

        throw new IllegalArgumentException("Unsupported Protobuf type '" + genericDescriptor.getName() + "'.");

    }

    @SuppressWarnings("unchecked")
    private ProtobufToRowDataConverter createArrayConverter(LogicalType info) {

        ProtobufToRowDataConverter elementConverter;

        if (info instanceof DateType) {

            elementConverter = this::convertToDate;

        } else if (info instanceof TimeType) {

            elementConverter = this::convertToTime;
        } else if (info instanceof VarCharType) {
            elementConverter = this::convertToString;
        } else {

            elementConverter = (Object fieldO) -> (fieldO);
        }

        return (Object o) -> new GenericArrayData(((List) o)
                .stream()
                .map(elementConverter::convert)
                .toArray());
    }

    private StringData convertToString(Object filedO) {

        return StringData.fromString((String) filedO);
    }

    private ProtobufToRowDataConverter createObjectConverter(LogicalType info) {
        if (info instanceof DateType) {
            return this::convertToDate;
        } else if (info instanceof TimeType) {
            return this::convertToTime;
        } else if (info instanceof VarCharType) {
            return this::convertToString;
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

}
