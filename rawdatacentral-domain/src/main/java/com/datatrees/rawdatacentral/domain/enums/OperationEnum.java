package com.datatrees.rawdatacentral.domain.enums;

/**
 * 操作类型
 * Created by zhouxinghai on 2017/4/25.
 */
public enum OperationEnum {

    LOGIN("login", "登录"),
    SEARCH("search", "抓取页面"),
    EXTRACT("extract", "解析页面"),
    CRAWLER("crawler", "爬取");

    /**
     * 操作代码
     */
    private String code;

    /**
     * 操作名称
     */
    private String name;

    OperationEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "OperationEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
