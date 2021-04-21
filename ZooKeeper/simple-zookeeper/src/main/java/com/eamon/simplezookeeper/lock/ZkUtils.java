package com.eamon.simplezookeeper.lock;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-25 18:20
 */
public class ZkUtils {
    private static final String CONNECT_STRING = "10.211.55.11:2181,10.211.55.12:2181,10.211.55.13:2181,10.211.55.14:2181/testLock";
    private static final DefaultWatcher DEFAULT_WATCHER = new DefaultWatcher();
    private static CountDownLatch cdl = new CountDownLatch(1);

    private static ZooKeeper zooKeeper;

    public static ZooKeeper connect() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(CONNECT_STRING, 3000, DEFAULT_WATCHER);
            DEFAULT_WATCHER.setCdl(cdl);
            cdl.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }
}
