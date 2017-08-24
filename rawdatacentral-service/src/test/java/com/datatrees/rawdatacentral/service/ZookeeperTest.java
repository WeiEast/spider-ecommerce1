package com.datatrees.rawdatacentral.service;

import org.apache.zookeeper.*;
import org.junit.Test;

public class ZookeeperTest {

    @Test
    public void testZookeeper() throws Exception {

        String connectString = "192.168.5.24:2181";
        // 创建一个与服务器的连接
        ZooKeeper zk = new ZooKeeper(connectString, 300 * 1000, new Watcher() {
            // 监控所有被触发的事件
            public void process(WatchedEvent event) {
                System.out.println("已经触发了" + event.getType() + "事件！");
            }
        });
        String path = zk.create("/testRootPath", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(path);
    }

}
