package flink.examples.sql._08.batch;

import java.util.regex.Pattern;

public class Utils {

    public static String format(String sql) {

        // https://blog.csdn.net/qq_21383435/article/details/82286132

        Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/|#.*?$|");
        return p.matcher(sql).replaceAll("$1");
    }

}
