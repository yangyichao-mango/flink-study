package flink.examples.sql._05.format.formats.utils;

public class MoreRunnables {


    public static <EXCEPTION extends Throwable> void throwing(ThrowableRunable<EXCEPTION> throwableRunable) {
        try {
            throwableRunable.run();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
