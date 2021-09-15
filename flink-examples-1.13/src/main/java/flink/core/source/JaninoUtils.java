package flink.core.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.codehaus.janino.SimpleCompiler;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JaninoUtils {

    private static final SimpleCompiler COMPILER = new SimpleCompiler();

    static {
        COMPILER.setParentClassLoader(JaninoUtils.class.getClassLoader());
    }

    public static Class<DeserializationSchema> genClass(String className, String code) throws Exception {

        COMPILER.cook(code);

        System.out.println("生成的代码：\n" + code);

        return (Class<DeserializationSchema>) COMPILER.getClassLoader().loadClass(className);
    }

}
