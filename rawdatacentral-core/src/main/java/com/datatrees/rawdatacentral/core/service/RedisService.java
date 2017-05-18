package com.datatrees.rawdatacentral.core.service;

import java.util.List;

/**
 * redis操作
 */
public interface RedisService {

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    public boolean saveString(final String key, final String value);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    public boolean saveListString(final String key, final List<String> value);
}
