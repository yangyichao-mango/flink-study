package flink.examples.sql._05.format.formats.protobuf.row.typeutils;

import java.util.List;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.types.logical.ArrayType;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.BinaryType;
import org.apache.flink.table.types.logical.BooleanType;
import org.apache.flink.table.types.logical.DoubleType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.MapType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.types.logical.RowType.RowField;
import org.apache.flink.table.types.logical.VarCharType;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import com.google.common.collect.Lists;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import flink.examples.sql._05.format.formats.protobuf.row.ProtobufUtils;


/**
 * Converts an Protobuf schema into Flink's type information. It uses {@link RowTypeInfo} for representing
 * objects and converts Protobuf types into types that are compatible with Flink's Table & SQL API.
 *
 * <p>Note: Changes in this class need to be kept in sync with the corresponding runtime
 */
public class ProtobufSchemaConverter {

    private ProtobufSchemaConverter() {
        // private
    }

    /**
     * Converts an Protobuf class into a nested row structure with deterministic field order and data
     * types that are compatible with Flink's Table & SQL API.
     *
     * @param protobufClass Protobuf message that contains schema information
     * @return type information matching the schema
     */
    @SuppressWarnings("unchecked")
    public static <T extends Message> TypeInformation<Row> convertToTypeInfo(Class<T> protobufClass) {
        Preconditions.checkNotNull(protobufClass, "Protobuf specific message class must not be null.");
        // determine schema to retrieve deterministic field order
        final Descriptors.Descriptor descriptor = ProtobufUtils.getDescriptor(protobufClass);
        return (TypeInformation<Row>) convertToTypeInfo(descriptor);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> RowType convertToRowDataTypeInfo(Class<T> protobufClass) {
        Preconditions.checkNotNull(protobufClass, "Protobuf specific message class must not be null.");
        // determine schema to retrieve deterministic field order
        final Descriptors.Descriptor descriptor = ProtobufUtils.getDescriptor(protobufClass);
        return (RowType) convertToRowDataTypeInfo(descriptor);
    }

    /**
     * Converts an Protobuf descriptorBytes into a nested row structure with deterministic field order and data
     * types that are compatible with Flink's Table & SQL API.
     *
     * @param descriptorBytes Protobuf descriptorBytes
     * @return type information matching the schema
     */
    @SuppressWarnings("unchecked")
    public static <T extends Message> TypeInformation<Row> convertToTypeInfo(byte[] descriptorBytes) {
        Preconditions.checkNotNull(descriptorBytes, "Protobuf descriptorBytes must not be null.");
        // determine schema to retrieve deterministic field order
        final Descriptors.Descriptor descriptor = ProtobufUtils.getDescriptor(descriptorBytes);
        return (TypeInformation<Row>) convertToTypeInfo(descriptor);
    }

    public static LogicalType convertToRowDataTypeInfo(Descriptors.GenericDescriptor genericDescriptor) {


        if (genericDescriptor instanceof Descriptors.Descriptor) {

            Descriptors.Descriptor descriptor = ((Descriptors.Descriptor) genericDescriptor);

            List<FieldDescriptor> fieldDescriptors = descriptor.getFields();

            int size = fieldDescriptors.size();

            final LogicalType[] types = new LogicalType[size];
            final String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                final FieldDescriptor field = descriptor.getFields().get(i);
                types[i] = convertToRowDataTypeInfo(field);
                names[i] = field.getName();
            }

            if (descriptor.getOptions().getMapEntry()) {
                // map

                return new MapType(types[0], types[1]);
            } else {
                // message

                List<RowField> rowFields = Lists.newLinkedList();

                for (int i = 0; i < size; i++) {
                    rowFields.add(new RowField(names[i], types[i]));
                }

                return new RowType(rowFields);
            }

        } else if (genericDescriptor instanceof FieldDescriptor) {

            FieldDescriptor fieldDescriptor = ((FieldDescriptor) genericDescriptor);

            LogicalType logicalType = null;

            // field
            switch (fieldDescriptor.getType()) {
                case DOUBLE:
                    logicalType = new DoubleType();
                    break;
                case FLOAT:
                    logicalType = new FloatType();
                    break;
                case INT64:
                case UINT64:
                case FIXED64:
                case SFIXED64:
                case SINT64:
                    logicalType = new BigIntType();
                    break;
                case INT32:
                case FIXED32:
                case UINT32:
                case SFIXED32:
                case SINT32:
                    logicalType = new IntType();
                    break;
                case BOOL:
                    logicalType = new BooleanType();
                    break;
                case STRING:
                case ENUM:
                    logicalType = new VarCharType(Integer.MAX_VALUE);
                    break;
                case GROUP:
                case MESSAGE:
                    logicalType = convertToRowDataTypeInfo(fieldDescriptor.getMessageType());
                    break;
                case BYTES:
                    logicalType = new ArrayType(new BinaryType());
                    break;
            }

            if (fieldDescriptor.isRepeated() && !(logicalType instanceof MapType)) {
                return new ArrayType(logicalType);
            } else {
                return logicalType;
            }


        }

        throw new IllegalArgumentException("Unsupported Protobuf type '" + genericDescriptor.getName() + "'.");

    }

    public static TypeInformation<?> convertToTypeInfo(Descriptors.GenericDescriptor genericDescriptor) {


        if (genericDescriptor instanceof Descriptors.Descriptor) {

            Descriptors.Descriptor descriptor = ((Descriptors.Descriptor) genericDescriptor);

            List<FieldDescriptor> fieldDescriptors = descriptor.getFields();

            int size = fieldDescriptors.size();

            final TypeInformation<?>[] types = new TypeInformation<?>[size];
            final String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                final FieldDescriptor field = descriptor.getFields().get(i);
                types[i] = convertToTypeInfo(field);
                names[i] = field.getName();
            }

            if (descriptor.getOptions().getMapEntry()) {
                // map

                return Types.MAP(types[0], types[1]);
            } else {
                // message

                return Types.ROW_NAMED(names, types);
            }

        } else if (genericDescriptor instanceof FieldDescriptor) {

            FieldDescriptor fieldDescriptor = ((FieldDescriptor) genericDescriptor);

            TypeInformation<?> typeInformation = null;

            // field
            switch (fieldDescriptor.getType()) {
                case DOUBLE:
                    typeInformation = Types.DOUBLE;
                    break;
                case FLOAT:
                    typeInformation = Types.FLOAT;
                    break;
                case INT64:
                case UINT64:
                case FIXED64:
                case SFIXED64:
                case SINT64:
                    typeInformation = Types.LONG;
                    break;
                case INT32:
                case FIXED32:
                case UINT32:
                case SFIXED32:
                case SINT32:
                    typeInformation = Types.INT;
                    break;
                case BOOL:
                    typeInformation = Types.BOOLEAN;
                    break;
                case STRING:
                case ENUM:
                    typeInformation = Types.STRING;
                    break;
                case GROUP:
                case MESSAGE:
                    typeInformation = convertToTypeInfo(fieldDescriptor.getMessageType());
                    break;
                case BYTES:
                    typeInformation = Types.PRIMITIVE_ARRAY(Types.BYTE);
                    break;
            }

            if (fieldDescriptor.isRepeated() && !(typeInformation instanceof MapTypeInfo)) {
                return Types.LIST(typeInformation);
            } else {
                return typeInformation;
            }


        }

        throw new IllegalArgumentException("Unsupported Protobuf type '" + genericDescriptor.getName() + "'.");


    }


}
