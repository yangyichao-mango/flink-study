package flink.examples.runtime._01.future;

import java.util.concurrent.CompletableFuture;


public class CompletableFuture_ThenComposeAsync_Test2 {

    public static void main(String[] args) throws Exception {
        // 第一个任务:
        CompletableFuture<String> cfQuery = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油");
        });
        // cfQuery成功后继续执行下一个任务:
        CompletableFuture<String> cfFetch = cfQuery.thenComposeAsync((code) -> {
            return CompletableFuture.supplyAsync(() -> fetchPrice(code));
        });
        // cfFetch成功后打印结果:
        cfFetch.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(2000);
    }

    static String queryCode(String name) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return name;
    }

    static String fetchPrice(String code) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return code + "：" + 5 + Math.random() * 20;
    }

}
