package flink.examples.sql._07.query._06_joins._04_lookup_join._01_redis;

import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * redis 安装：https://blog.csdn.net/realize_dream/article/details/106227622
 * redis java client：https://www.cnblogs.com/chenyanbin/p/12088796.html
 */
public class RedisDemo {

    public static void main(String[] args) {
        singleConnect();
        poolConnect();
        pipeline();
    }

    public static void singleConnect() {
        // jedis单实例连接
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String result = jedis.get("a");

        HashMap<String, Object> h = new HashMap<>();

        h.put("name", "namehhh");
        h.put("age", "3");

        String s = new Gson().toJson(h);

        jedis.set("a", s);

        System.out.println(result);
        jedis.close();
    }

    public static void poolConnect() {
        //jedis连接池
        JedisPool pool = new JedisPool("127.0.0.1", 6379);
        Jedis jedis = pool.getResource();
        String result = jedis.get("a");
        System.out.println(result);
        jedis.close();
        pool.close();
    }

    public static void pipeline() {
        //jedis连接池
        JedisPool pool = new JedisPool("127.0.0.1", 6379);
        Jedis jedis = pool.getResource();

        Pipeline pipeline = jedis.pipelined();

        long setStart = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.set("key_" + i, String.valueOf(i));
        }
        long setEnd = System.currentTimeMillis();
        System.out.println("非pipeline操作10000次字符串数据类型set写入，耗时：" + (setEnd - setStart) + "毫秒");

        long pipelineStart = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            pipeline.set("key_" + i, String.valueOf(i));
        }
        List<Object> l = pipeline.syncAndReturnAll();
        long pipelineEnd = System.currentTimeMillis();
        System.out.println("pipeline操作10000次字符串数据类型set写入，耗时：" + (pipelineEnd - pipelineStart) + "毫秒");


        String result = jedis.get("a");
        System.out.println(result);
        jedis.close();
        pool.close();
    }

}
