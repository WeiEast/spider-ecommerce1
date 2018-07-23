package com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean;

import java.io.Serializable;

/**
 * post body对应的Bean
 * Created by guimeichao on 18/3/28.
 */
public class RequestBean implements Serializable {

    private String ak;

    private String cid;

    private String city;

    private String ctid;

    private String cv;

    private String en;

    private String imei;

    private String nt;

    private String prov;

    private Object reqBody;

    private String sb;

    private String sn;

    private String sp;

    private String st;

    private String sv;

    private String f4306t;

    private String tel;

    private String xc;

    private String xk;

    public String getCtid() {
        return this.ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid;
    }

    public String getAk() {
        return this.ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEn() {
        return this.en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getT() {
        return this.f4306t;
    }

    public void setT(String t) {
        this.f4306t = t;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getCv() {
        return this.cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getSt() {
        return this.st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getSv() {
        return this.sv;
    }

    public void setSv(String sv) {
        this.sv = sv;
    }

    public String getSp() {
        return this.sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getXk() {
        return this.xk;
    }

    public void setXk(String xk) {
        this.xk = xk;
    }

    public String getXc() {
        return this.xc;
    }

    public void setXc(String xc) {
        this.xc = xc;
    }

    public Object getReqBody() {
        return this.reqBody;
    }

    public void setReqBody(Object reqBody) {
        this.reqBody = reqBody;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSb() {
        return this.sb;
    }

    public void setSb(String sb) {
        this.sb = sb;
    }

    public String getNt() {
        return this.nt;
    }

    public void setNt(String nt) {
        this.nt = nt;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getProv() {
        return this.prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}