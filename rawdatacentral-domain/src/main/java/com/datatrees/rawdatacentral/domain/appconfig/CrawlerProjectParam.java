package com.datatrees.rawdatacentral.domain.appconfig;

import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * User: yand
 * Date: 2018/4/18
 */
public class CrawlerProjectParam {

    /**
     * 业务类型 {WebsiteType}
     */
    private Byte            websiteType;
    /**
     * 具体业务
     */
    private List<ProjectParam> projects;

    public CrawlerProjectParam() {
    }

    public CrawlerProjectParam(Byte websiteType, List<ProjectParam> projects) {
        this.websiteType = websiteType;
        this.projects = projects;
    }

    public Byte getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(Byte websiteType) {
        this.websiteType = websiteType;
    }

    public List<ProjectParam> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectParam> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
