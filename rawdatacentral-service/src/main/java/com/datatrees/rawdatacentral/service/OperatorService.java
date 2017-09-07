package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.domain.model.Operator;

/**
 * 运营商目录
 * Created by zhouxinghai on 2017/6/27.
 */
public interface OperatorService {

    /**
     * @param websiteId
     * @return
     */
    public Operator getByWebsiteId(Integer websiteId);
}
