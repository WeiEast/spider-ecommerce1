package com.datatrees.rawdatacentral.api;

import java.util.List;

import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;

/**
 * 运营商分组和负载
 */
public interface WebsiteGroupServiceApi {

    /**
     * 查询可用版本数量
     */
    Integer enableCount(String groupCode);

    /**
     * 查询可用版本
     * @param groupCode
     * @return
     */
    List<WebsiteGroup> queryEnable(String groupCode);

    /**
     * 查询不可用版本
     * @param groupCode
     * @return
     */
    List<WebsiteGroup> queryDisable(String groupCode);
}
