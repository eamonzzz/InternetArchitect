package com.eamon.simplezookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author eamonzzz
 * @date 2021-03-23 23:03
 */
public class App {
    public static void main(String[] args) throws Exception {
        String connectString = "10.211.55.11:2181,10.211.55.12:2181,10.211.55.13:2181,10.211.55.14:2181";
        ZooKeeper zk = new ZooKeeper(connectString, 3000, watchedEvent -> {
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

        zk.create("/eamon", "demo01".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Stat stat = new Stat();
        byte[] data = zk.getData("/eamon", watchedEvent -> {
            Watcher.Event.EventType type = watchedEvent.getType();
            Watcher.Event.KeeperState state = watchedEvent.getState();

            System.out.println("zk get data: " + type + " " + state);
        }, stat);

        System.out.println("get data : "+new String(data));

        Thread.sleep(10000);
    }
}
