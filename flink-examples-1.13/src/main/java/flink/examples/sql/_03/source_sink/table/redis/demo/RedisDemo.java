package flink.examples.sql._03.source_sink.table.redis.demo;

import java.util.HashMap;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis 安装：https://blog.csdn.net/realize_dream/article/details/106227622
 * redis java client：https://www.cnblogs.com/chenyanbin/p/12088796.html
 */
public class RedisDemo {

    public static void main(String[] args) {
        singleConnect();
        poolConnect();
    }

    public static void singleConnect() {
        // jedis单实例连接
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String result = jedis.get("a");

        HashMap<String, Object> h = new HashMap<>();

        h.put("name", "namehhh");
        h.put("name1", "namehhh111");
        h.put("score", 3L);

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

}
