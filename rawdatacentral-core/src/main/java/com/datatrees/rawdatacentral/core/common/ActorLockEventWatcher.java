/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.common;

import com.datatrees.common.util.ThreadInterruptedUtil;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.common.zookeeper.watcher.AbstractLockerWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月23日 下午6:32:50
 */
public class ActorLockEventWatcher extends AbstractLockerWatcher {

    private static final Logger logger = LoggerFactory.getLogger(ActorLockEventWatcher.class);
    private Thread          actorThread;
    private ZooKeeperClient zookeeperClient;
    private String          root;

    /**
     * @param name
     */
    public ActorLockEventWatcher(String root, String name, Thread thread, ZooKeeperClient zookeeperClient) {
        super(root + "/" + name);
        this.root = root;
        actorThread = thread;
        this.zookeeperClient = zookeeperClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.zookeeper.Locker#await()
     */
    @Override
    public void await() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.zookeeper.watcher.AbstractLockerWatcher#detectLeader()
     */
    @Override
    protected void detectLeader() {
        try {
            String leader = super.getLastFollower();
            logger.debug("leader is path : " + leader);
            if (!childPath.equals(leader)) {
                logger.info(childPath + " lose lock from zookeeper with lastFollower: " + leader);
                if (actorThread != null && actorThread.isAlive()) {
                    logger.info(" interrupt thread={},threadId={},childPath={},root={}", actorThread, actorThread.getId(), childPath, root);
                    // actorThread.interrupt();
                    ThreadInterruptedUtil.setInterrupt(actorThread);
                    zookeeperClient.unregisterWatcher(this);
                }
            }
        } catch (Exception e) {
            logger.error("detectLeader error", e);
        }
    }

    @Override
    protected boolean releaseLeader() {
        try {
            String leader = super.getLastFollower();
            logger.debug("leader is path : " + leader);
            if (childPath.equals(leader)) {
                logger.info(childPath + " try to release lock from zookeeper delete path:" + path);
                this.deletePath(root, path, -1);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean unLock() {
        return releaseLeader();
    }

    public boolean cancel() {
        zookeeperClient.registerWatcher(this);
        if (this.init()) {
            zookeeperClient.unregisterWatcher(this);
            return true;
        }
        return false;
    }

}
