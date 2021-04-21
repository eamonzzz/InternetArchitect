package com.eamon.simplezookeeper.configcenter01;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-25 10:12
 */
public class WatcherCallback01 implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ZooKeeper zooKeeper;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String path = "/appConf01";
    private AppConf01 conf01;

    public AppConf01 getConf01() {
        return conf01;
    }

    public void setConf01(AppConf01 conf01) {
        this.conf01 = conf01;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void await() {
        zooKeeper.exists(path, this, this, "await");
        try {
            // 等待 watcher 回调来讲 countDownLatch 减下去
            System.out.println("阻塞等待" + countDownLatch.getCount());

            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * DataCallback
     *
     * @param rc
     * @param path
     * @param ctx
     * @param data
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            String dataStr = new String(data);

            conf01.setConf(dataStr);
            System.out.println("数据回调：" + ctx.toString() + " - " + dataStr);
            if (countDownLatch.getCount() > 0) {
                countDownLatch.countDown();
            }


        }
    }

    /**
     * StatCallback
     *
     * @param rc
     * @param path
     * @param ctx
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

        if (stat != null) {
            // 节点存在，则注册监听
            System.out.println("节点存在 StatCallback");
            zooKeeper.getData(path, this, this, "StatCallback");
        }
        // 节点不存在，则会在 await 处进行等待
    }

    /**
     * Watcher
     * 用于监听事件
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        // 监听的节点的事件类型
        Event.EventType type = watchedEvent.getType();
        // zk状态
        Event.KeeperState state = watchedEvent.getState();

        System.out.println(type);
        System.out.println(state);

        switch (type) {
            case None:
                break;
            case NodeCreated:
                // 节点被创建
                System.out.println("NodeCreated");
                zooKeeper.getData(path, this, this, "NodeCreated");
                break;
            case NodeDeleted:
                // 节点被删除
                countDownLatch = new CountDownLatch(1);
                conf01.setConf("");
                break;
            case NodeDataChanged:
                // 节点数据被修改
                zooKeeper.getData(path, this, this, "NodeDataChanged");
                break;
            case NodeChildrenChanged:
                // 节点的子节点被修改
                break;
            case DataWatchRemoved:
                // 数据的watcher被移除
                break;
            case ChildWatchRemoved:
                // 子节点的 watcher 被移除
                break;
            case PersistentWatchRemoved:
                // 持久化的watcher被移除
                break;
            default:
                break;
        }

        switch (state) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            case Closed:
                break;
            default:
                break;
        }

    }
}
