package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;

/**
 * 运营商配置
 * Created by zhouxinghai on 2017/8/29
 */
public interface WebsiteOperatorService {

    /**
     * 获取运营商配置
     * @param websiteName
     * @return
     */
    WebsiteOperator getByWebsiteName(String websiteName);

    /**
     * 从老配置导入配置信息
     * @param config 自定义信息
     */
    void importWebsite(WebsiteOperator config);

    /**
     * 更新配置
     * @param config
     */
    void updateWebsite(WebsiteOperator config);

    /**
     * 从其他环境导入配置
     * @param websiteName
     * @param from        dev=192.168.5.15,test=rawdatacentral.saas.test.treefinance.com.cn
     *                    test=rawdatacentral.saas.test.treefinance.com.cn
     *                    zsc=rawdatacentral.approach.saas.treefinance.com.cn
     *                    yfb=rawdatecentral.yfb.saas.treefinance.com.cn
     */
    void importConfig(String websiteName, String from);

}
