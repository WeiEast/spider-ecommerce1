package com.datatrees.rawdatacentral.api;

import com.treefinance.proxy.domain.Proxy;

public interface ProxyService {

    /**
     * 获取代理
     * @param taskId
     * @param websiteName
     * @return
     */
    Proxy getProxy(Long taskId, String websiteName);

    /**
     * 释放代理
     * @param taskId
     */
    void release(Long taskId);

}
