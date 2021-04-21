package com.eamon.simplezookeeper.configcenter;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-24 22:36
 */
public class ZkUtils {
    private static final String CONNECT_STRING = "10.211.55.11:2181,10.211.55.12:2181,10.211.55.13:2181,10.211.55.14:2181";
    private static final DefaultWatcher DEFAULT_WATCHER = new DefaultWatcher();

    private static ZooKeeper zooKeeper;

    private static CountDownLatch cdl = new CountDownLatch(1);

    /**
     * 创建连接
     *
     * @return ZooKeeper
     */
    public static ZooKeeper connect() {
        try {
            zooKeeper = new ZooKeeper(CONNECT_STRING, 3000, DEFAULT_WATCHER);
            // 设置 CountDownLatch 是为了让连接的状态为 SyncConnected 时才执行后序的逻辑
            DEFAULT_WATCHER.setCdl(cdl);
            cdl.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }

    /**
     * 关闭
     */
    public static void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
