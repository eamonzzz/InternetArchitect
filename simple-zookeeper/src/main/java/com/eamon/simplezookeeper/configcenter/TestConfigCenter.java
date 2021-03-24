package com.eamon.simplezookeeper.configcenter;

import org.apache.zookeeper.ZooKeeper;

/**
 * 使用zookeeper作为配置中心使用案例
 *
 * @author eamonzzz
 * @date 2021-03-24 22:31
 */
public class TestConfigCenter {


    public static void main(String[] args) {


        // 1. 连接
        // 会等待连接成功后再执行后序的步骤
        ZooKeeper zooKeeper = ZkUtils.connect();

        // 2. 判断 是否有配置数据，如果没有，则进行等待
        WatchCallback watchCallback = new WatchCallback();
        watchCallback.setZooKeeper(zooKeeper);
        Conf conf = new Conf();
        watchCallback.setConf(conf);

        // 判断 是否有配置数据，如果没有，则进行等待，有了就放行
        watchCallback.await();

        // 当有数据后，执行下面的步骤

        while (true) {
            String data = conf.getData();
            if ("".equalsIgnoreCase(data) || null == data) {
                System.out.println("配置数据丢了。。。");
                watchCallback.await();
            } else {
                System.out.println("配置数据为：" + data);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
