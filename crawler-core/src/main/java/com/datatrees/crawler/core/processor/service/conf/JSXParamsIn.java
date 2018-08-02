package com.datatrees.crawler.core.processor.service.conf;

import com.google.gson.annotations.SerializedName;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0 2012-04-19
 * @since 1.0
 */
public class JSXParamsIn {

    private String  cookie;

    @SerializedName("http_proxy")
    private String  httpProxy;

    @SerializedName("last_modified_time")
    private String  lastModifiedTime;

    @SerializedName("page_id")
    private int     page_id                    = 1;

    @SerializedName("waiting_time")
    private int     waitingTime                = 30;

    @SerializedName("page_url")
    private String  pageUrl;

    private String  referer;

    @SerializedName("video_url_pattern")
    private String  videoUrlPattern;

    @SerializedName("video_return_type")
    private String  videoReturnType;

    @SerializedName("to_save_image")
    private boolean toSaveImage                = false;

    @SerializedName("to_trigger_onclick")
    private boolean toTriggerOnclick           = false;

    @SerializedName("anchor_on")
    private boolean anchorOn                   = true;

    @SerializedName("in_iframe")
    private boolean inIframe                   = false;

    @SerializedName("to_load_image")
    private boolean toLoadImage                = false;

    @SerializedName("to_return_page")
    private boolean toReturnPage               = true;

    @SerializedName("page_load_timeout")
    private int     pageLoadTimeout            = 60 * 5; // page load time out

    @SerializedName("page_script_timeout")
    private int     pageScriptTimeout          = 60; // page js load time out

    // net export page load timeout
    @SerializedName("netexport_pageLoaded_timeout")
    private int     netexportPageLoadedTimeout = 60 * 5; //

    // click elements
    @SerializedName("click_element")
    private String  clickElement               = ""; //

    // need return all request content ??
    @SerializedName("to_return_stream")
    private boolean toReturnStream             = false;

    // need net export feature ??
    @SerializedName("netexport_enable")
    private boolean netexportEnable            = false;

    // export firefox type now just support firefox
    @SerializedName("browser_type")
    private String  browserType                = "firefox";

    @SerializedName("js_sleep_time")
    private int     jsSleepTime                = 0;

    @SerializedName("js_wait_time")
    private int     jsWaitTime                 = 0;

    @SerializedName("netexport_total_timeout")
    private int     netexportTotalTimeout      = 60000;

    // executor javascript
    @SerializedName("js_content")
    private String  jsContent                  = "";

    public static JSXParamsIn create() {
        return new JSXParamsIn();
    }

    public String getCookie() {
        return cookie;
    }

    public JSXParamsIn setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public String getHttpProxy() {
        return httpProxy;
    }

    public JSXParamsIn setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
        return this;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public JSXParamsIn setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
        return this;
    }

    public int getPage_id() {
        return page_id;
    }

    public JSXParamsIn setPage_id(int page_id) {
        this.page_id = page_id;
        return this;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public JSXParamsIn setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
        return this;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public JSXParamsIn setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public String getReferer() {
        return referer;
    }

    public JSXParamsIn setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    public String getVideoUrlPattern() {
        return videoUrlPattern;
    }

    public JSXParamsIn setVideoUrlPattern(String videoUrlPattern) {
        this.videoUrlPattern = videoUrlPattern;
        return this;
    }

    public String getVideoReturnType() {
        return videoReturnType;
    }

    public JSXParamsIn setVideoReturnType(String videoReturnType) {
        this.videoReturnType = videoReturnType;
        return this;
    }

    public boolean isToSaveImage() {
        return toSaveImage;
    }

    public JSXParamsIn setToSaveImage(boolean toSaveImage) {
        this.toSaveImage = toSaveImage;
        return this;
    }

    public boolean isToTriggerOnclick() {
        return toTriggerOnclick;
    }

    public JSXParamsIn setToTriggerOnclick(boolean toTriggerOnclick) {
        this.toTriggerOnclick = toTriggerOnclick;
        return this;
    }

    public boolean isAnchorOn() {
        return anchorOn;
    }

    public JSXParamsIn setAnchorOn(boolean anchorOn) {
        this.anchorOn = anchorOn;
        return this;
    }

    public boolean isInIframe() {
        return inIframe;
    }

    public JSXParamsIn setInIframe(boolean inIframe) {
        this.inIframe = inIframe;
        return this;
    }

    public boolean isToLoadImage() {
        return toLoadImage;
    }

    public JSXParamsIn setToLoadImage(boolean toLoadImage) {
        this.toLoadImage = toLoadImage;
        return this;
    }

    public boolean isToReturnPage() {
        return toReturnPage;
    }

    public JSXParamsIn setToReturnPage(boolean toReturnPage) {
        this.toReturnPage = toReturnPage;
        return this;
    }

    public int getPageLoadTimeout() {
        return pageLoadTimeout;
    }

    public JSXParamsIn setPageLoadTimeout(int pageLoadTimeout) {
        this.pageLoadTimeout = pageLoadTimeout;
        return this;
    }

    public int getPageScriptTimeout() {
        return pageScriptTimeout;
    }

    public JSXParamsIn setPageScriptTimeout(int pageScriptTimeout) {
        this.pageScriptTimeout = pageScriptTimeout;
        return this;
    }

    public int getNetexportPageLoadedTimeout() {
        return netexportPageLoadedTimeout;
    }

    public JSXParamsIn setNetexportPageLoadedTimeout(int netexportPageLoadedTimeout) {
        this.netexportPageLoadedTimeout = netexportPageLoadedTimeout;
        return this;
    }

    public String getClickElement() {
        return clickElement;
    }

    public JSXParamsIn setClickElement(String clickElement) {
        this.clickElement = clickElement;
        return this;
    }

    public boolean isToReturnStream() {
        return toReturnStream;
    }

    public JSXParamsIn setToReturnStream(boolean toReturnStream) {
        this.toReturnStream = toReturnStream;
        return this;
    }

    public boolean isNetexportEnable() {
        return netexportEnable;
    }

    public JSXParamsIn setNetexportEnable(boolean netexportEnable) {
        this.netexportEnable = netexportEnable;
        return this;
    }

    public String getBrowserType() {
        return browserType;
    }

    public JSXParamsIn setBrowserType(String browserType) {
        this.browserType = browserType;
        return this;
    }

    public int getJsSleepTime() {
        return jsSleepTime;
    }

    public JSXParamsIn setJsSleepTime(int jsSleepTime) {
        this.jsSleepTime = jsSleepTime;
        return this;
    }

    public int getJsWaitTime() {
        return jsWaitTime;
    }

    public JSXParamsIn setJsWaitTime(int jsWaitTime) {
        this.jsWaitTime = jsWaitTime;
        return this;
    }

    public String getJsContent() {
        return jsContent;
    }

    public JSXParamsIn setJsContent(String jsContent) {
        this.jsContent = jsContent;
        return this;
    }

    public int getNetexportTotalTimeout() {
        return netexportTotalTimeout;
    }

    public JSXParamsIn setNetexportTotalTimeout(int netexportTotalTimeout) {
        this.netexportTotalTimeout = netexportTotalTimeout;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (netexportEnable ? 1231 : 1237);
        result = prime * result + netexportPageLoadedTimeout;
        result = prime * result + pageLoadTimeout;
        result = prime * result + pageScriptTimeout;
        result = prime * result + (toLoadImage ? 1231 : 1237);
        result = prime * result + waitingTime;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JSXParamsIn other = (JSXParamsIn) obj;
        if (netexportEnable != other.netexportEnable) return false;
        if (netexportPageLoadedTimeout != other.netexportPageLoadedTimeout) return false;
        if (pageLoadTimeout != other.pageLoadTimeout) return false;
        if (pageScriptTimeout != other.pageScriptTimeout) return false;
        if (toLoadImage != other.toLoadImage) return false;
        if (waitingTime != other.waitingTime) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ParamsIn [cookie=" + cookie + ", http_proxy=" + httpProxy + ", last_modified_time=" + lastModifiedTime + ", page_id=" + page_id +
                ", waiting_time=" + waitingTime + ", page_url=" + pageUrl + ", referer=" + referer + ", video_url_pattern=" + videoUrlPattern +
                ", video_return_type=" + videoReturnType + ", to_save_image=" + toSaveImage + ", to_trigger_onclick=" + toTriggerOnclick +
                ", anchor_on=" + anchorOn + ", in_iframe=" + inIframe + ", to_load_image=" + toLoadImage + ", to_return_page=" + toReturnPage +
                ", page_load_timeout=" + pageLoadTimeout + ", page_script_timeout=" + pageScriptTimeout + ", netexport_pageLoaded_timeout=" +
                netexportPageLoadedTimeout + ", click_element=" + clickElement + ", to_return_stream=" + toReturnStream + ", netexport_enable=" +
                netexportEnable + ", browser_type=" + browserType + "]";
    }
}
