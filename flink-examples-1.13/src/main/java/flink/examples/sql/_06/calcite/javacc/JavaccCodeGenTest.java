package flink.examples.sql._06.calcite.javacc;



public class JavaccCodeGenTest {

    public static void main(String[] args) throws Exception {
//       version();
       javacc();
    }

    private static void version() throws Exception {
        org.javacc.parser.Main.main(new String[] {"-version"});
    }

    private static void javacc() throws Exception {

        String path = ClassLoader.getSystemResources("Simple1.jj").nextElement().getPath();

        org.javacc.parser.Main.main(new String[] {path});
    }

}
