package flink.examples.question.datastream._01.kryo_protobuf_no_more_bytes_left;

import java.lang.reflect.Method;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.protobuf.Message;
import com.sun.tools.javac.util.Assert;
import com.twitter.chill.protobuf.ProtobufSerializer;

import flink.examples.datastream._04.keyed_co_process.protobuf.Source;

public class KryoProtobufNoMoreBytesLeftTest {

    public static void main(String[] args) throws Exception {

        Source source = Source
                .newBuilder()
                .build();

        byte[] bytes = source.toByteArray();

        byte[] buffer = new byte[300];

        Kryo kryo = newKryo();

        Output output = new Output(buffer);

        // ser

        ProtobufSerializer protobufSerializer = new ProtobufSerializer();

        protobufSerializer.write(kryo, output, source);


        // deser

        Input input = new Input(buffer);

        Class<?> c = (Class<?>) Source.getDefaultInstance().getClass();

        Message m = protobufSerializer.read(kryo, input, (Class<Message>) c);

        testGetParse();

    }

    private static void testGetParse() throws Exception {

        ProtobufSerializerV2 protobufSerializerV2 = new ProtobufSerializerV2();

        Method m = protobufSerializerV2.getParse(Source.class);


        Source s = (Source) m.invoke(null, Source.newBuilder().setName("antigeneral").build().toByteArray());

        Assert.check("antigeneral".equals(s.getName()));
    }

    private static class ProtobufSerializerV2 extends ProtobufSerializer {
        @Override
        public Method getParse(Class cls) throws Exception {
            return super.getParse(cls);
        }
    }

    private static Kryo newKryo() {
        Kryo kryo = new Kryo();

        kryo.addDefaultSerializer(Source.class, ProtobufSerializerV2.class);

        return kryo;
    }

}
