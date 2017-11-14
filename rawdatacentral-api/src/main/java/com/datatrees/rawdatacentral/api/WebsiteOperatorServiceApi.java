package com.datatrees.rawdatacentral.api;

import java.util.List;

import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * website接口
 */
public interface WebsiteOperatorServiceApi {

    /**
     * 启用/停用配置
     * @param websiteName
     * @param enable
     */
    HttpResult<Boolean> updateEnable(String websiteName, Boolean enable);

    /**
     * 查询所有禁用版本
     * @return
     */
    HttpResult<List<WebsiteOperator>> queryDisable();

}


