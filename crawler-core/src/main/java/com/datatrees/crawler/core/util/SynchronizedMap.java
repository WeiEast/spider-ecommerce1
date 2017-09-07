/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月8日 上午11:47:35
 */
public class SynchronizedMap<K, V> implements Map<K, V> {

    private final Map<K, V>     map;
    private final ReadWriteLock readWriteLock;

    /**
     *
     */
    public SynchronizedMap() {
        super();
        map = new HashMap<K, V>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        readWriteLock.readLock().lock();
        try {
            return map.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        readWriteLock.readLock().lock();
        try {
            return map.isEmpty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        readWriteLock.readLock().lock();
        try {
            return map.containsKey(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        readWriteLock.readLock().lock();
        try {
            return map.containsValue(value);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        readWriteLock.readLock().lock();
        try {
            return map.get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        readWriteLock.writeLock().lock();
        try {
            return map.put(key, value);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public V remove(Object key) {
        readWriteLock.writeLock().lock();
        try {
            return map.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        readWriteLock.writeLock().lock();
        try {
            map.putAll(m);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        readWriteLock.writeLock().lock();
        try {
            map.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<K> keySet() {
        readWriteLock.readLock().lock();
        try {
            return map.keySet();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#values()
     */
    @Override
    public Collection<V> values() {
        readWriteLock.readLock().lock();
        try {
            return map.values();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        readWriteLock.readLock().lock();
        try {
            return map.entrySet();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

}
