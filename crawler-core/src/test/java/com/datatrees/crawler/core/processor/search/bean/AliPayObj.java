package com.datatrees.crawler.core.processor.search.bean;

public class AliPayObj {
    private String url;
    private String date;
    private String desc;
    private String amount;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public String toString(){
        return "[url:"+url+",date:"+date+",desc:"+desc+",amount:"+amount+"]";
    }
    

}
