package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.domain.model.Keyword;

import java.util.List;

/**
 * 搜索关键字
 * Created by zhouxinghai on 2017/6/27.
 */
public interface KeywordService {

    /**
     * 根据类型查找Keyword
     * @param websiteType websiteType
     * @return
     */
    public List<Keyword> queryByWebsiteType(Integer websiteType);
}
