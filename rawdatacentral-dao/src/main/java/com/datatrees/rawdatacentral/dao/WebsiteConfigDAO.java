package com.datatrees.rawdatacentral.dao;

import com.datatrees.rawdatacentral.domain.common.WebsiteConfig;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;

/**
 * Created by zhouxinghai on 2017/6/30.
 */
@Resource
public interface WebsiteConfigDAO {

    /**
     * 获取站点配置websiteId,websiteName只需一个就好
     * @param websiteId
     * @param websiteName
     * @return
     */
    WebsiteConfig getWebsiteConfig(@Param("websiteId") Integer websiteId, @Param("websiteName") String websiteName);

    /**
     * 更新website配置
     * @param websiteId
     * @param searchConfig
     * @param extractConfig
     */
    int updateWebsiteConf(@Param("websiteId") Integer websiteId, @Param("searchConfig") String searchConfig,
                          @Param("extractConfig") String extractConfig);

}
