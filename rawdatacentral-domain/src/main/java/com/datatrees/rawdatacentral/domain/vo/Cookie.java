package com.datatrees.rawdatacentral.domain.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/19.
 */
public class Cookie implements Serializable {

    private String  domain;
    private String  path;
    private boolean secure;
    private int     version;
    private Date    expiryDate;
    private String  name;
    private String  value;
    private Map<String, String> attribs = new HashMap<>();

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getAttribs() {
        return attribs;
    }

    public void setAttribs(Map<String, String> attribs) {
        this.attribs = attribs;
    }
}
