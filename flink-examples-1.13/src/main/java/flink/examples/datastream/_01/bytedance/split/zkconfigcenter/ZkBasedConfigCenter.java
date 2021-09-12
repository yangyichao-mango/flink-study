package flink.examples.datastream._01.bytedance.split.zkconfigcenter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryOneTime;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import flink.examples.datastream._01.bytedance.split.model.DynamicProducerRule;


public class ZkBasedConfigCenter {

    private TreeCache treeCache;

    private CuratorFramework zkClient;

    private static class Factory {
        private static final ZkBasedConfigCenter INSTANCE = new ZkBasedConfigCenter();
    }

    public static ZkBasedConfigCenter getInstance() {
        return Factory.INSTANCE;
    }

    private ZkBasedConfigCenter() {
        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private ConcurrentMap<Long, DynamicProducerRule> map = new ConcurrentHashMap<>();

    public ConcurrentMap<Long, DynamicProducerRule> getMap() {
        return map;
    }


    private void setData() throws Exception {
        String path = "/kafka-config";
        zkClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryOneTime(1000));
        zkClient.start();

        zkClient.setData().forPath(path, ("{\n"
                + "  1: {\n"
                + "    \"condition\": \"1==1\",\n"
                + "    \"targetTopic\": \"tuzisir1\"\n"
                + "  },\n"
                + "  2: {\n"
                + "    \"condition\": \"1!=1\",\n"
                + "    \"targetTopic\": \"tuzisir2\"\n"
                + "  }\n"
                + "}").getBytes());
    }

    private void open() throws Exception {

        String path = "/kafka-config";

        zkClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryOneTime(1000));
        zkClient.start();
        // 启动时读取远程配置中心的配置信息

        String json = new String(zkClient.getData().forPath(path));

        this.update(json);

        treeCache = new TreeCache(zkClient, path);
        treeCache.start();
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                switch (treeCacheEvent.getType()) {
                    case NODE_UPDATED:
                        // 通知的内容：包含路径和值
                        byte[] data = treeCacheEvent.getData().getData();

                        String json = new String(data);

                        System.out.println("配置变化为了：" + json);

                        // 更新数据
                        update(json);
                        break;
                    default:

                }

            }
        });

    }

    public void close() {
        this.treeCache.close();
        this.zkClient.close();
    }

    private void update(String json) {

        Map<Long, DynamicProducerRule>
                result = getNewMap(json);

        Set<Long> needAddId = Sets.difference(result.keySet(), map.keySet()).immutableCopy();

        Set<Long> needDeleteId = Sets.difference(map.keySet(), result.keySet()).immutableCopy();

        needAddId.forEach(new Consumer<Long>() {
            @Override
            public void accept(Long id) {
                DynamicProducerRule dynamicProducerRule = result.get(id);
                dynamicProducerRule.init(id);
                map.put(id, dynamicProducerRule);
            }
        });

        needDeleteId.forEach(new Consumer<Long>() {
            @Override
            public void accept(Long id) {
                map.remove(id);
            }
        });
    }

    private Map<Long, DynamicProducerRule> getNewMap(String json) {

        Gson gson = new Gson();

        Map<String, DynamicProducerRule> newMap = null;

        Type type = new TypeToken<Map<String, DynamicProducerRule>>() {
        }.getType();

        newMap = gson.fromJson(json, type);

        Map<Long, DynamicProducerRule> result = new HashMap<>();

        Optional.ofNullable(newMap)
                .ifPresent(new Consumer<Map<String, DynamicProducerRule>>() {
                    @Override
                    public void accept(Map<String, DynamicProducerRule> stringDynamicProducerRuleMap) {
                        stringDynamicProducerRuleMap.forEach(new BiConsumer<String, DynamicProducerRule>() {
                            @Override
                            public void accept(String s, DynamicProducerRule dynamicProducerRule) {
                                result.put(Long.parseLong(s), dynamicProducerRule);
                            }
                        });
                    }
                });


        return result;

    }

}
