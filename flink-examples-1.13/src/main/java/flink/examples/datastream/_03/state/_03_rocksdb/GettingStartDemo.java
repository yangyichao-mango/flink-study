package flink.examples.datastream._03.state._03_rocksdb;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class GettingStartDemo {

    // 因为RocksDB是由C++编写的，在Java中使用首先需要加载Native库
    static {
        // Loads the necessary library files.
        // Calling this method twice will have no effect.
        // By default the method extracts the shared library for loading at
        // java.io.tmpdir, however, you can override this temporary location by
        // setting the environment variable ROCKSDB_SHAREDLIB_DIR.
        // 默认这个方法会加压一个共享库到java.io.tmpdir
        RocksDB.loadLibrary();
    }

    public static void main(String[] args) throws RocksDBException {
        // 1. 打开数据库
        // 1.1 创建数据库配置
        Options dbOpt = new Options();
        // 1.2 配置当数据库不存在时自动创建
        dbOpt.setCreateIfMissing(true);
        // 1.3 打开数据库。因为RocksDB默认是保存在本地磁盘，所以需要指定位置
        RocksDB rdb = RocksDB.open(dbOpt, "./data/rocksdb");
        // 2. 写入数据
        // 2.1 RocksDB都是以字节流的方式写入数据库中，所以我们需要将字符串转换为字节流再写入。这点类似于HBase
        byte[] key = "zhangsan".getBytes();
        byte[] value = "20".getBytes();
        // 2.2 调用put方法写入数据
        rdb.put(key, value);
        System.out.println("写入数据到RocksDB完成！");
        // 3. 调用delete方法读取数据
        System.out.println("从RocksDB读取key = " + new String(key) + "的value为" + new String(rdb.get(key)));
        // 4. 移除数据
        rdb.delete(key);
        // 关闭资源
        rdb.close();
        dbOpt.close();
    }

}
