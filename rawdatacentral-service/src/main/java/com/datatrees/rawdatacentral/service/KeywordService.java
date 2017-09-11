package com.datatrees.rawdatacentral.service;

import java.util.List;

import com.datatrees.rawdatacentral.domain.model.Keyword;

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
