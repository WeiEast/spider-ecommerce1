package com.datatrees.rawdatacentral.api;

import com.treefinance.proxy.domain.Proxy;

public interface ProxyService {

    /**
     * 获取代理
     * @param taskId      任务ID
     * @param websiteName 站点标识
     * @return 代理 {@link Proxy}
     */
    Proxy getProxy(Long taskId, String websiteName);

    /**
     * 通知wise-proxy释放代理
     * @param taskId 任务ID
     */
    void release(Long taskId);

    /**
     * 清理与任务绑定的代理
     * @param taskId 任务ID
     */
    void clear(Long taskId);
}
