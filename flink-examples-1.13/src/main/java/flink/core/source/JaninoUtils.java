package flink.core.source;

import org.codehaus.janino.SimpleCompiler;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JaninoUtils {

    private static final SimpleCompiler COMPILER = new SimpleCompiler();

    static {
        COMPILER.setParentClassLoader(JaninoUtils.class.getClassLoader());
    }

    public static <T> Class<T> genClass(String className, String code, Class<T> clazz) throws Exception {

        COMPILER.cook(code);

        System.out.println("生成的代码：\n" + code);

        return (Class<T>) COMPILER.getClassLoader().loadClass(className);
    }

}
