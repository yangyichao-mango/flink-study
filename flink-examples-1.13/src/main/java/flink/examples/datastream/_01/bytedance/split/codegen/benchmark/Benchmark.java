package flink.examples.datastream._01.bytedance.split.codegen.benchmark;

import org.codehaus.groovy.control.CompilerConfiguration;

import flink.examples.datastream._01.bytedance.split.model.ClientLogSource;
import flink.examples.datastream._01.bytedance.split.model.DynamicProducerRule;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Benchmark {

    private static void benchmarkForJava() {
        ClientLogSource s = ClientLogSource.builder().id(1).build();

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 50000000; i++) {
            boolean b = String.valueOf(s.getId()).equals("1");
        }

        long end2 = System.currentTimeMillis();

        System.out.println("java:" + (end2 - start2) + " ms");
    }

    public static void benchmarkForGroovyClassLoader() {

        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        // 设置该GroovyClassLoader的父ClassLoader为当前线程的加载器(默认)
        GroovyClassLoader groovyClassLoader =
                new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);

        String groovyCode = "class demo_002 {\n"
                + "    boolean eval(flink.examples.datastream._01.bytedance.split.model.SourceModel sourceModel) {\n"
                + "        return String.valueOf(sourceModel.getId()).equals(\"1\");\n"
                + "    }\n"
                + "}";
        try {
            // 获得GroovyShell_2加载后的class
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyCode);
            // 获得GroovyShell_2的实例
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

            ClientLogSource s = ClientLogSource.builder().id(1).build();

            long start1 = System.currentTimeMillis();

            for (int i = 0; i < 50000000; i++) {
                Object methodResult = groovyObject.invokeMethod("eval", s);
            }

            long end1 = System.currentTimeMillis();

            System.out.println("groovy:" + (end1 - start1) + " ms");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void benchmarkForJanino() {

        String condition = "String.valueOf(sourceModel.getId()).equals(\"1\")";

        DynamicProducerRule dynamicProducerRule = DynamicProducerRule
                .builder()
                .condition(condition)
                .targetTopic("t")
                .build();

        dynamicProducerRule.init(1L);

        ClientLogSource s = ClientLogSource.builder().id(1).build();

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 50000000; i++) {
            boolean b = dynamicProducerRule.eval(s);
        }

        long end2 = System.currentTimeMillis();

        System.out.println("janino:" + (end2 - start2) + " ms");
    }

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 10; i++) {
            benchmarkForJava();

            // janino
            benchmarkForJanino();

            // groovy classloader
            benchmarkForGroovyClassLoader();

            System.out.println();
        }
    }

}
