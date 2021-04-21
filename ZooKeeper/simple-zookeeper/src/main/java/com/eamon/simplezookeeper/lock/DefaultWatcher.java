package com.eamon.simplezookeeper.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author eamonzzz
 * @date 2021-03-25 18:22
 */
public class DefaultWatcher implements Watcher {
    private CountDownLatch cdl;

    public void setCdl(CountDownLatch cdl) {
        this.cdl = cdl;
    }


    /**
     * 连接事件
     *
     * @param watchedEvent
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
