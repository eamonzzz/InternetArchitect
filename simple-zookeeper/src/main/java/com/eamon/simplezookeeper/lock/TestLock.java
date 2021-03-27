package com.eamon.simplezookeeper.lock;

import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;

/**
 * 使用 zookeeper 作为分布式锁
 *
 * @author eamonzzz
 * @date 2021-03-25 18:20
 */
public class TestLock {


    public static void main(String[] args) {
        ZooKeeper zk = ZkUtils.connect();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                WatcherCallback watcherCallback = new WatcherCallback();
                watcherCallback.setZooKeeper(zk);
                String threadName = Thread.currentThread().getName();
                watcherCallback.setThreadName(threadName);

                System.out.println(threadName);

                watcherCallback.tryLock();

                try {
                    Thread.sleep(100);
                    System.out.println("拿到锁了，开始干活：" + threadName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                watcherCallback.unLock();

            }).start();
        }

    }

}
