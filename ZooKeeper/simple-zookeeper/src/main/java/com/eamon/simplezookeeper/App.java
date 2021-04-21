package com.eamon.simplezookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eamonzzz
 * @date 2021-03-23 23:03
 */
public class App {
    public static void main(String[] args) throws Exception {
        // zk 是由session概念的，没有连接池的概念
        //watch：观察，回调
        //watch的注册只发生在 读 类型的调用，比如 get exist等


        //String connectString = "10.211.55.11:2181,10.211.55.12:2181,10.211.55.13:2181,10.211.55.14:2181";
        String connectString = "10.211.55.29:2181,10.211.55.30:2181,10.211.55.31:2181,10.211.55.32:2181";

        final CountDownLatch cdl = new CountDownLatch(1);
        //第一类：new zk 时候，传入的watch，这个watch，session级别的，跟path 、node没有关系。
        ZooKeeper zk = new ZooKeeper(connectString, 3000, watchedEvent -> {
            System.out.println("New ZK Event Type: "+watchedEvent.getType().name());
            // watch 回调方法
            String path = watchedEvent.getPath();
            Watcher.Event.KeeperState state = watchedEvent.getState();
            Watcher.Event.EventType type = watchedEvent.getType();
            switch (state) {
                case Unknown:
                    break;
                case Disconnected:
                    break;
                case NoSyncConnected:
                    break;
                case SyncConnected:
                    System.out.println("New ZK SyncConnected");
                    cdl.countDown();
                    break;
                case AuthFailed:
                    break;
                case ConnectedReadOnly:
                    break;
                case SaslAuthenticated:
                    break;
                case Expired:
                    break;
                default:
                    break;
            }

            switch (type) {
                case None:
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    break;
                case NodeDataChanged:
                    break;
                case NodeChildrenChanged:
                    break;
                default:
                    break;
            }

            System.out.println("ZK watchedEvent: " + type.name() + " - " + state.name());
        });

        cdl.await();

        ZooKeeper.States zkState = zk.getState();
        switch (zkState) {
            case CONNECTING:
                System.out.println("connecting...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("connected...");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
            default:
                break;
        }


        zk.create("/eamon", "demo01".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        AtomicInteger atomicInteger = new AtomicInteger(0);

        Stat stat = new Stat();
        byte[] data = zk.getData("/eamon", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                // 当 /eamon 节点变化时，本watch会被调用（一次性的，若要继续监听，则需重复注册）
                System.out.println("zk get data: " + watchedEvent.toString());
                atomicInteger.getAndAdd(1);


                // 此时一般的逻辑是  会再次去拿最新的 节点值，然后重新将 watch 注册进去，这样数据每次更改时都可以被watch到
                try {
                    byte[] data1 = zk.getData("/eamon", this, stat);
                    System.out.println("data changed: "+new String(data1));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, stat);

        System.out.println("data : " + new String(data));


        // 使用 setData 触发回调
        Stat stat1 = zk.setData("/eamon", "eamon-02".getBytes(), stat.getVersion());
        System.out.println(atomicInteger.get());

        // 再次设置值时 会触发回调吗？不会，因为 watch 是一次性注册，若想每次都触发，则需要每次都重新注册
        Stat stat2 = zk.setData("/eamon", "eamon-03".getBytes(), stat1.getVersion());
        System.out.println(atomicInteger.get());


        System.out.println("last version: "+stat2.getVersion());



        System.out.println("-------async start----------");

        zk.getData("/eamon", true, (rc, path, ctx, data1, stat3) -> {
            System.out.println("-------async call back----------");
            System.out.println(ctx.toString());
            System.out.println(new String(data1));

        },"async");
        System.out.println("-------async over----------");

        Thread.sleep(10000);
    }
}
