package flink.examples.runtime._01.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;


public class FutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        ExecutorService executor = Executors.newFixedThreadPool(4);
        // 定义任务:
        Callable<String> task = new Task();
        // 提交任务并获得Future:
        Future<String> future = executor.submit(task);

        // 从Future获取异步执行返回的结果:

        String result = future.get();
        System.out.println(result);
        executor.shutdown();

    }

    private static class Task implements Callable<String> {
        public String call() throws Exception {
            Thread.sleep(1000);
            return "1";
        }
    }

}
