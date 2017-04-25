/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.datatrees.common.util.CacheUtil;
import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.core.dao.KeywordDao;
import com.datatrees.rawdatacentral.core.model.Keyword;
import com.datatrees.rawdatacentral.core.service.KeywordService;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 上午11:06:38
 */
@Service
public class KeywordServiceImpl implements KeywordService {

    @Resource
    private KeywordDao KeywordDao;

    /*
     * (non-Javadoc)
     * 
     * @see KeywordService#getKeywordByType(java.lang.String)
     */
    @Override
    public List<Keyword> getKeywordByType(String type) {
        Map<String, List<Keyword>> keywordMap = (Map<String, List<Keyword>>) CacheUtil.INSTANCE.getObject(Constants.KEYWORD_MAP_KEY);
        if (keywordMap == null) {
            List<Keyword> keywordList = KeywordDao.getAllKeyword();
            keywordMap = new HashMap<String, List<Keyword>>();
            if (CollectionUtils.isNotEmpty(keywordList)) {
                for (Keyword keyword : keywordList) {
                    List<Keyword> typeKeywordList = keywordMap.get(keyword.getKeywordType());
                    if (typeKeywordList == null) {
                        typeKeywordList = new ArrayList<Keyword>();
                        keywordMap.put(keyword.getKeywordType(), typeKeywordList);
                    }
                    typeKeywordList.add(keyword);
                }
            }
            CacheUtil.INSTANCE.insertObject(Constants.KEYWORD_MAP_KEY, keywordMap);
        }
        return keywordMap.get(type);
    }

}
