package com.eamon.simplezookeeper.configcenter01;

import org.apache.zookeeper.ZooKeeper;

/**
 * @author eamonzzz
 * @date 2021-03-25 09:27
 */
public class TestConfig01 {

    public static void main(String[] args) {
        /**
         * 思路：
         * 1. 先获取连接
         * 2. 因为连接是异步的，所以需要创建一个计数器，在连接状态变为已连接时才让后序步骤执行
         * 3. 获取配置信息，并监听配置信息的状态
         * 3.1 如果一开始不存在配置信息，则添加监听事件然后程序进行阻塞等待
         * 3.2 如果一开始就有配置信息，则直接获取，同时注册监听事件（因为监听事件是一次性的，所以每次获取之后都要重新注册）
         * 4. 获取到配置信息后，进行后续的业务逻辑
         */

        ZooKeeper zk = ZkUtils01.connect();

        ZooKeeper.States state = zk.getState();
        System.out.println(state.isConnected());
        System.out.println("后续操作");

        WatcherCallback01 watcherCallback01 = new WatcherCallback01();
        watcherCallback01.setZooKeeper(zk);
        AppConf01 appConf01 = new AppConf01();
        watcherCallback01.setConf01(appConf01);

        watcherCallback01.await();

        while (true) {
            String conf = appConf01.getConf();

            if ("".equalsIgnoreCase(conf)) {
                System.out.println("数据丢失了，等待数据");
            } else {
                System.out.println("-- " + conf);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}
