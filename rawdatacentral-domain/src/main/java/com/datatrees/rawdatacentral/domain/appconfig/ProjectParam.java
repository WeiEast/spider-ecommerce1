package com.datatrees.rawdatacentral.domain.appconfig;

import com.alibaba.fastjson.JSON;

/**
 * User: yand
 * Date: 2018/4/18
 */
public class ProjectParam {

    /**
     * 爬取业务标识 例"huabei"
     */
    private String code;
    /**
     * 爬取业务名称 例"花呗"
     */
    private String name;
    /**
     * 爬取状态
     */
    private Byte   crawlerStatus;
    /**
     * 排序
     */
    private int    order;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getCrawlerStatus() {
        return crawlerStatus;
    }

    public void setCrawlerStatus(Byte crawlerStatus) {
        this.crawlerStatus = crawlerStatus;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
