package flink.examples.datastream._01.bytedance.split.codegen;

import org.codehaus.janino.SimpleCompiler;

import flink.examples.datastream._01.bytedance.split.model.Evaluable;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JaninoUtils {

    private static final SimpleCompiler COMPILER = new SimpleCompiler();

    static {
        COMPILER.setParentClassLoader(JaninoUtils.class.getClassLoader());
    }

    public static Class<Evaluable> genCodeAndGetClazz(Long id, String topic, String condition) throws Exception {

        String className = "CodeGen_" + topic + "_" + id;

        String code = "import org.apache.commons.lang3.ArrayUtils;\n"
                + "\n"
                + "public class " + className + " implements flink.examples.datastream._01.bytedance.split.model.Evaluable {\n"
                + "    \n"
                + "    @Override\n"
                + "    public boolean eval(flink.examples.datastream._01.bytedance.split.model.ClientLogSource clientLogSource) {\n"
                + "        \n"
                + "        return " + condition + ";\n"
                + "    }\n"
                + "}\n";

        COMPILER.cook(code);

        System.out.println("生成的代码：\n" + code);

        return (Class<Evaluable>) COMPILER.getClassLoader().loadClass(className);
    }

    public static void main(String[] args) throws Exception {
        Class<Evaluable> c = genCodeAndGetClazz(1L, "topic", "1==1");

        System.out.println(1);
    }

}
