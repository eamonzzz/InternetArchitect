package com.eamon.simplezookeeper.configcenter;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-24 22:46
 */
public class WatchCallback implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    Conf conf;
    ZooKeeper zooKeeper;

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public Conf getConf() {
        return conf;
    }

    public void setConf(Conf conf) {
        this.conf = conf;
    }

    CountDownLatch cdl = new CountDownLatch(1);

    public void await() {
        zooKeeper.exists("/appConf", this, this, "await exists");
        try {
            System.out.println("阻塞，等待 appConf 数据！");
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * watcher
     * <p>
     * Watcher是用于监听节点，session 状态的
     * <p>
     * 比如getData对数据节点a设置了watcher，那么当a的数据内容发生改变时，客户端会收到NodeDataChanged通知，然后进行watcher的回调。
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        //
        Event.EventType type = watchedEvent.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                // 结点创建时
                // 需要监听并获得返回值
                zooKeeper.getData("/appConf", this, this, "getAppConf NodeCreated");
                break;
            case NodeDeleted:
                // 结点删除时
                // 根据具体的 容忍性进行设置
                System.out.println("deleted");
                conf.setData("");
                cdl = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                // 结点数据改变时
                // 数据变化时也需要监听
                zooKeeper.getData("/appConf", this, this, "getAppConf NodeDataChanged");
                break;
            case NodeChildrenChanged:
                // 结点的子节点改变时
                break;
            default:
                break;
        }

    }

    /**
     * AsyncCallback.DataCallback
     * <p>
     * AsyncCallback是在以异步方式使用 ZooKeeper API 时，用于处理返回结果的（同步则不要此回调，直接可以获取到数据）
     *
     * @param rc   The return code or the result of the call
     * @param path The path that we passed to asynchronous calls.
     * @param ctx  Whatever context object that we passed to asynchronous calls.
     * @param data The data of the node.
     * @param stat Stat object of the node on given path.
     */
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            String dataStr = new String(data);
            conf.setData(dataStr);
            System.out.println("data callback: " + dataStr);
            // 已经获取到 appConf 的配置数据了，可以释放锁，进行下一步了
            cdl.countDown();
        }
    }

    /**
     * AsyncCallback.StatCallback
     * <p>
     * 此回调用于检索节点的状态
     *
     * @param rc   The return code or the result of the call.
     * @param path The path that we passed to asynchronous calls.
     * @param ctx  Whatever context object that we passed to asynchronous calls.
     * @param stat Stat object of the node on given path.
     */
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            // 如果节点存在，则对节点进行监听 watcher
            zooKeeper.getData("/appConf", this, this, "appConf exist processResult");
        }
    }
}
