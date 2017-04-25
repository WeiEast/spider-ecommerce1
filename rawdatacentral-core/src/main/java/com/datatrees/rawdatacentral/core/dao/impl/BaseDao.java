package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseDao {
    @Resource
    protected SqlMapClientTemplate sqlMapClientTemplate;

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    protected <T> List<List<T>> splitCollections(Collection<T> collections, int size) {
        List<T> collectionList = new ArrayList<T>();
        collectionList.addAll(collections);
        if (collectionList.size() > size) {
            List<List<T>> resultList = new ArrayList<List<T>>(collections.size() / size + 1);
            int fromIndex = 0;
            final int maxIndex = collectionList.size();
            while (fromIndex < collectionList.size()) {
                int toIndex = fromIndex + size;
                toIndex = toIndex <= maxIndex ? toIndex : maxIndex;
                resultList.add(collectionList.subList(fromIndex, toIndex));
                fromIndex = toIndex;
            }

            return resultList;

        } else {
            return Arrays.asList(collectionList);
        }
    }
}
