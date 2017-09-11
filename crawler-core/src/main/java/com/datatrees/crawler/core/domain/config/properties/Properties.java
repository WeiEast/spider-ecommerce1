package com.datatrees.crawler.core.domain.config.properties;

import java.io.Serializable;

import com.datatrees.crawler.core.domain.config.properties.cookie.AbstractCookie;
import com.datatrees.crawler.core.domain.config.properties.cookie.BaseCookie;
import com.datatrees.crawler.core.domain.config.properties.cookie.CustomCookie;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 10, 2014 7:29:16 PM
 */
public class Properties implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1340916732791297838L;
    private Integer        waitIntervalMillis;
    private AbstractCookie cookie;
    private Proxy          proxy;
    private String         encoding;
    private UnicodeMode    unicodeMode;
    private String         taskRegion;
    private Integer        maxRetryCount;
    private Boolean        duplicateRemoval;
    private String         httpClientType;
    private Boolean        redirectUriEscaped;
    private Boolean        allowCircularRedirects;
    private Integer        captchaCode;

    @Tag("captcha-code")
    public Integer getCaptchaCode() {
        return captchaCode;
    }

    @Node("captcha-code/text()")
    public void setCaptchaCode(Integer captchaCode) {
        this.captchaCode = captchaCode;
    }

    @Tag("redirect-uri-escaped")
    public Boolean getRedirectUriEscaped() {
        return redirectUriEscaped;
    }

    @Node("redirect-uri-escaped/text()")
    public void setRedirectUriEscaped(Boolean redirectUriEscaped) {
        this.redirectUriEscaped = redirectUriEscaped;
    }

    @Tag("allow-circular-redirects")
    public Boolean getAllowCircularRedirects() {
        return allowCircularRedirects;
    }

    @Node("allow-circular-redirects/text()")
    public void setAllowCircularRedirects(Boolean allowCircularRedirects) {
        this.allowCircularRedirects = allowCircularRedirects;
    }

    @Tag("http-client-type")
    public String getHttpClientType() {
        return httpClientType;
    }

    @Node("http-client-type/text()")
    public void setHttpClientType(String httpClientType) {
        this.httpClientType = httpClientType;
    }

    /**
     * @return the duplicateRemoval
     */
    @Tag("duplicate-removal")
    public Boolean getDuplicateRemoval() {
        return duplicateRemoval == null ? true : duplicateRemoval;
    }

    /**
     * @param duplicateRemoval the duplicateRemoval to set
     */
    @Node("duplicate-removal/text()")
    public void setDuplicateRemoval(Boolean duplicateRemoval) {
        this.duplicateRemoval = duplicateRemoval;
    }

    @Tag("wait-interval")
    public Integer getWaitIntervalMillis() {
        return waitIntervalMillis;
    }

    @Node("wait-interval/text()")
    public void setWaitIntervalMillis(Integer waitIntervalMillis) {
        this.waitIntervalMillis = waitIntervalMillis;
    }

    @Tag("proxy")
    public Proxy getProxy() {
        return proxy;
    }

    @Node("proxy")
    public void setProxy(Proxy proxy) {
        if (proxy != null && StringUtils.isBlank(proxy.getProxy())) {
            this.proxy = null;
        } else {
            this.proxy = proxy;
        }
    }

    @Tag("cookie")
    public AbstractCookie getCookie() {
        return cookie;
    }

    @Node(value = "cookie", types = {CustomCookie.class, BaseCookie.class})
    public void setCookie(AbstractCookie cookie) {
        this.cookie = cookie;
    }

    @Tag("encoding")
    public String getEncoding() {
        return encoding;
    }

    @Node("encoding/text()")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Tag("unicode-mode")
    public UnicodeMode getUnicodeMode() {
        return unicodeMode;
    }

    @Node("unicode-mode/text()")
    public void setUnicodeMode(String unicodeMode) {
        this.unicodeMode = UnicodeMode.getUnicodeMode(unicodeMode);
    }

    @Tag("task-region")
    public String getTaskRegion() {
        return taskRegion;
    }

    @Node("task-region/text()")
    public void setTaskRegion(String taskRegion) {
        this.taskRegion = taskRegion;
    }

    //
    //
    // /**
    // * @return the saveNextPageNum
    // */
    // @Tag("save-next-page-num")
    // public Boolean getSaveNextPageNum() {
    // return saveNextPageNum == null ? false : saveNextPageNum;
    // }
    //
    // /**
    // * @param saveNextPageNum the saveNextPageNum to set
    // */
    // @Node("save-next-page-num/text()")
    // public void setSaveNextPageNum(Boolean saveNextPageNum) {
    // this.saveNextPageNum = saveNextPageNum;
    // }

    @Tag("max-retry-count")
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    @Node("max-retry-count/text()")
    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    // @Tag("rate-limit")
    // public Integer getRateLimit() {
    // return rateLimit;
    // }
    //
    // @Node("rate-limit/text()")
    // public void setRateLimit(Integer rateLimit) {
    // this.rateLimit = rateLimit;
    // }

}
