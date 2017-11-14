package com.datatrees.rawdatacentral.api;

/**
 * 运营商分组和负载
 */
public interface WebsiteGroupServiceApi {

    /**
     * 查询可用版本数量
     */
    Integer enableCount(String groupCode);
}
