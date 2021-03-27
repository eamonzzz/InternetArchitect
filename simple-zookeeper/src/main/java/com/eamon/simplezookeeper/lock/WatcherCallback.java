package com.eamon.simplezookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-25 18:20
 */
public class WatcherCallback implements Watcher, AsyncCallback.StringCallback, AsyncCallback.StatCallback, AsyncCallback.Children2Callback {

    private ZooKeeper zooKeeper;
    CountDownLatch cdl = new CountDownLatch(1);

    private String threadName;

    private String lockName;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void tryLock() {
        try {
            Stat exists = zooKeeper.exists("/" + threadName, false);
            if (exists == null) {
                System.out.println(threadName+ "还没有拿到锁");
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zooKeeper.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, threadName + "Lock");
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zooKeeper.delete(lockName, -1);
            System.out.println("unLock: " + lockName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    /**
     * Watcher Callback
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();
        String path = watchedEvent.getPath();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                System.out.println(threadName + " -- "+path +" deleted。。。");
                zooKeeper.getChildren("/", false, this, "Delete");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
            default:
                break;
        }
    }

    /**
     * StatCallback
     *
     * @param rc   result code
     * @param path
     * @param ctx
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

        System.out.println("StatCallback: " + path + ctx.toString());
    }

    /**
     * StringCallback
     *
     * @param rc
     * @param path
     * @param ctx
     * @param name 创建的znode的名称。 成功后，名称和路径通常相等，除非已创建顺序节点。
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name != null) {
            // 创建成功后，要确定自己是不是最小的那个，如果是，说明我获得了锁，如果不是，就要监听上一个节点
            System.out.println("ThreadName: " + threadName + " -- LockName: " + name);
            this.lockName = name;
            // 这里不需要watch，因为在这里如果使用watch，会监听父节点下的所有的节点，这样会很浪费，没必要，我们只需要watch自己的上一个节点即可
            zooKeeper.getChildren("/", false, this, "StringCallback#getChildren");
        }
    }

    /**
     * Children2Callback
     *
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        // 一定可以看到自己前面的节点
        // 先排序
        Collections.sort(children);
        // 因为 lockName 是带 "/" 的  而 children 里面的元素是不带"/"的，所以需要截取
        String substring = lockName.substring(1);
        // 拿到自己节点的索引位置
        int idx = children.indexOf(substring);

        if (idx == 0) {
            // 如果是第一个，就 countdown 好让业务执行
            System.out.println(substring + " is the first");
            try {
                // 这里设置数据，是将已经获得锁的线程设置进去，一方面是增加业务处理的时间，避免并发跑的太快，后序的线程还没执行时程序就结束
                // 另一方面，设置进去之后，可以在 tryLock 中去获得已经执行过的线程，这里可以根据实际情况是否需要"重入锁"
                zooKeeper.setData("/", threadName.getBytes(), -1);
                cdl.countDown();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 如果不是第一个，就监听自己前面的节点，看是否被删除，如果被删除，就去抢锁
            zooKeeper.exists("/" + children.get(idx - 1), this, this, " not the first");
        }
    }
}
