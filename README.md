# 1.友情提示

> 1. 联系我：如要有问题咨询，请联系我（公众号：[`大数据羊说`](#32公众号)，备注来自`GitHub`）
> 2. 该仓库会持续更新 flink 教程福利干货，麻烦路过的各位亲给这个项目点个 `star`，太不易了，写了这么多，算是对我坚持下来的一种鼓励吧！

![在这里插入图片描述](https://raw.githubusercontent.com/yangyichao-mango/yangyichao-mango.github.io/master/1631459281928.png)

![Stargazers over time](https://starchart.cc/yangyichao-mango/flink-study.svg)

<br>
<p align="center">
    <a href="#32公众号" style="text-decoration:none;">
        <img src="https://img.shields.io/badge/WeChat-%E5%85%AC%E4%BC%97%E5%8F%B7-green" alt="公众号" />
    </a>
    <a href="https://www.zhihu.com/people/onemango" target="_blank" style="text-decoration:none;">
        <img src="https://img.shields.io/badge/zhihu-%E7%9F%A5%E4%B9%8E-blue" alt="知乎" />
    </a>
    <a href="https://juejin.cn/user/562562548382926" target="_blank" style="text-decoration:none;">
        <img src="https://img.shields.io/badge/juejin-%E6%8E%98%E9%87%91-blue" alt="掘金" />
    </a>
    <a href="https://blog.csdn.net/qq_34608620?spm=1001.2014.3001.5343&type=blog" target="_blank" style="text-decoration:none;">
        <img src="https://img.shields.io/badge/csdn-CSDN-red" alt="CSDN" />
    </a>
    <a href="https://home.51cto.com/space?uid=15322900" target="_blank" style="text-decoration:none;">
        <img src="https://img.shields.io/badge/51cto-51CT0%E5%8D%9A%E5%AE%A2-orange" alt="51CT0博客" />
        </a>
    <img src="https://img.shields.io/github/stars/yangyichao-mango/flink-study" alt="投稿">           
</p>

# 2.文章目录

> 以下列出的是作者对原创的一些文章和一些学习资源做了一个汇总，会持续更新！如果帮到了您，请点个star支持一下，谢谢！

## 2.1.flink sql

1. [公众号文章：踩坑记 | flink sql count 还有这种坑！](https://mp.weixin.qq.com/s/5XDkmuEIfHB_WsMHPeinkw)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_01/countdistincterror)
2. [公众号文章：实战 | flink sql 与微博热搜的碰撞！！！](https://mp.weixin.qq.com/s/GHLoWMBZxajA2nXPHhH8WA)
3. [公众号文章：flink sql 知其所以然（一）| source\sink 原理](https://mp.weixin.qq.com/s/xIXh8B_suAlKSp56aO5aEg)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_03/source_sink)
4. [公众号文章：flink sql 知其所以然（二）| 自定义 redis 数据维表（附源码）](https://mp.weixin.qq.com/s/b_zV_tGp5QJQjgnSaxNT_Q)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_03/source_sink)
5. [公众号文章：flink sql 知其所以然（三）| 自定义 redis 数据汇表（附源码）](https://mp.weixin.qq.com/s/7Fwey_AXNJ0jQZWfXvtNmw)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_03/source_sink)
6. [公众号文章：flink sql 知其所以然（四）| sql api 类型系统](https://mp.weixin.qq.com/s/aqDRWgr3Kim7lblx10JvtA)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_04/type)
7. [公众号文章：flink sql 知其所以然（五）| 自定义 protobuf format](https://mp.weixin.qq.com/s/STUC4trW-HA3cnrsqT-N6g)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_05/format/formats)
8. [公众号文章：flink sql 知其所以然（六）| flink sql 约会 calcite（看这篇就够了）](https://mp.weixin.qq.com/s/SxRKp368mYSKVmuduPoXFg)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_06/calcite)
9. [公众号文章：flink sql 知其所以然（七）：不会连最适合 flink sql 的 ETL 和 group agg 场景都没见过吧？](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query)
10. [公众号文章：flink sql 知其所以然（八）：flink sql tumble window 的奇妙解析之路](https://mp.weixin.qq.com/s/IRmt8dWmxAmbBh696akHdw)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_04_window)
11. [公众号文章：flink sql 知其所以然（九）：window tvf tumble window 的奇思妙解](https://mp.weixin.qq.com/s/QVuu5_N4lHo5gXlt1tdncw)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_04_window_agg/_01_tumble_window)
12. [公众号文章：flink sql 知其所以然（十）：大家都用 cumulate window 啦](https://mp.weixin.qq.com/s/IqAzjrQmcGmnxvHm1FAV5g)，[源码](https://github.com/yangyichao-mango/flink-study/blob/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_04_window_agg/_02_cumulate_window/CumulateWindowTest.java)
13. [公众号文章：flink sql 知其所以然（十一）：去重不仅仅有 count distinct 还有强大的 deduplication](https://mp.weixin.qq.com/s/VL6egD76B4J7IcpHShTq7Q)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_05_over/_01_row_number)
14. [公众号文章：flink sql 知其所以然（十二）：流 join 很难嘛？？？（上）](https://mp.weixin.qq.com/s/Z8QfKfhrX5KEnR-s7gRtsA)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_06_joins/_01_regular_joins)
15. [公众号文章：flink sql 知其所以然（十三）：流 join 很难嘛？？？（下）]()，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_06_joins/_02_interval_joins)
16. [公众号文章：flink sql 知其所以然（十四）：维表 join 的性能优化之路（上）附源码]()，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_06_joins/_04_lookup_join/_01_redis)
17. [公众号文章：flink sql 知其所以然（十五）：改了改源码，实现了个 batch lookup join（附源码）]()，[源码](https://github.com/yangyichao-mango/flink-study/blob/main/flink-examples-1.13/src/main/java/flink/examples/sql/_07/query/_06_joins/_04_lookup_join/_01_redis/RedisBatchLookupTest2.java)
18. [公众号文章：flink sql 知其所以然（十八）：在 flink 中怎么使用 hive udf？附源码]()，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/sql/_09/udf/_02_stream_hive_udf)

## 2.2.flink 实战

1. [公众号文章：揭秘字节跳动埋点数据实时动态处理引擎（附源码）](https://mp.weixin.qq.com/s/PoK0XOA9OHIDJezb1fLOMw)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/datastream/_01/bytedance/split)
2. [公众号文章：踩坑记| flink state 序列化 java enum 竟然岔劈了](https://mp.weixin.qq.com/s/YElwTL-wzo2UVVIsIH_9YA)，[源码](https://github.com/yangyichao-mango/flink-study/tree/main/flink-examples-1.13/src/main/java/flink/examples/datastream/_03/enums_state)
3. [公众号文章：flink idea 本地调试状态恢复](https://mp.weixin.qq.com/s/rLeKY_49q8rR9C_RmlTmhg)，[源码](https://github.com/yangyichao-mango/flink-study/blob/main/flink-examples-1.13/src/main/java/flink/examples/runtime/_04/statebackend/CancelAndRestoreWithCheckpointTest.java)

# 3.联系我

## 3.1.微信

有任何学习上的疑惑都欢迎添加作者的微信，一起学习，一起交流！

![在这里插入图片描述](https://raw.githubusercontent.com/yangyichao-mango/yangyichao-mango.github.io/master/1.png)

## 3.2.公众号

如果大家想要实时关注我更新的文章以及分享的干货的话，可以关注我的公众号：**大数据羊说**

![在这里插入图片描述](https://raw.githubusercontent.com/yangyichao-mango/yangyichao-mango.github.io/master/2.png)
