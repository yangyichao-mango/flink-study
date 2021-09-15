package flink.core.source;

import java.io.IOException;

import org.apache.flink.api.common.serialization.DeserializationSchema;

import com.google.protobuf.GeneratedMessageV3;

import flink.examples.datastream._04.keyed_co_process.protobuf.Source;
import lombok.SneakyThrows;

public class SourceFactory {

    public static <Message extends GeneratedMessageV3> DeserializationSchema<Message> getProtobufSer(Class<Message> clazz) {
        return null;
    }

    @SneakyThrows
    public static <Message extends GeneratedMessageV3> DeserializationSchema<Message> getProtobufDerse(Class<Message> clazz) {

        String code = TEMPLATE.replaceAll("\\$\\{ProtobufClassName}", clazz.getName())
                .replaceAll("\\$\\{SimpleProtobufName}", clazz.getSimpleName());

        String className = clazz.getSimpleName() + "_DeserializationSchema";

        Class<DeserializationSchema> deClass = JaninoUtils.genClass(className, code);

        return deClass.newInstance();
    }

    private static final String TEMPLATE =
                        "public class ${SimpleProtobufName}_DeserializationSchema extends org.apache.flink.api.common"
                      + ".serialization.AbstractDeserializationSchema<${ProtobufClassName}> {\n"
                      + "\n"
                      + "    public ${SimpleProtobufName}_DeserializationSchema() {\n"
                      + "        super(${ProtobufClassName}.class);\n"
                      + "    }\n"
                      + "\n"
                      + "    @Override\n"
                      + "    public ${ProtobufClassName} deserialize(byte[] message) throws java.io.IOException {\n"
                      + "        return ${ProtobufClassName}.parseFrom(message);\n"
                      + "    }\n"
                      + "}";

    public static void main(String[] args) throws IOException {
        System.out.println(SourceFactory.class.getName());
        System.out.println(SourceFactory.class.getCanonicalName());
        System.out.println(SourceFactory.class.getSimpleName());
        System.out.println(SourceFactory.class.getTypeName());

        DeserializationSchema<Source> ds = getProtobufDerse(Source.class);

        Source s = Source.newBuilder()
                .addNames("antigeneral")
                .build();

        Source s1 = ds.deserialize(s.toByteArray());

        System.out.println();
    }

}
