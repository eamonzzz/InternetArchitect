package com.eamon.simplezookeeper.configcenter01;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-25 09:44
 */
public class DefaultWatcher01 implements Watcher {

    private CountDownLatch cdl;

    public void setCdl(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    /**
     * zookeeper 创建连接时的 watcher
     *
     * @param watchedEvent 监听到的事件
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState state = watchedEvent.getState();
        switch (state) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                // 已连接，放行
                System.out.println("zookeeper 已连接。。。");
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
            case Closed:
                break;
            default:
                break;
        }
    }
}
