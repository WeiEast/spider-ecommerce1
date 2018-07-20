package com.datatrees.spider.operator.domain.model;

import java.io.Serializable;

/**
 * 前端拉取运营商分组配置
 * @author zhouxinghai
 * @date 2018/4/23
 */
public class OperatorGroup implements Serializable {

    /**
     * 运营商类别代码
     */
    private String groupCode;

    /**
     * 运营商类别名称
     */
    private String groupName;

    /**
     * 排序
     */
    private int    sort;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
