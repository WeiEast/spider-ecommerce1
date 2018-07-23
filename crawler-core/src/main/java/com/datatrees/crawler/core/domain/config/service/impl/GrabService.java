/**
 *
 */

package com.datatrees.crawler.core.domain.config.service.impl;

import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * String url = "https://ab.alipay.com/i/lianxi.htm";
 * String endUrl = "https://ab.alipay.com/i/lianxi.htm";
 * Map<String, Object> config = new HashMap<>();
 * config.put("css", new ArrayList<>());
 * config.put("usePCUA", true);
 * config.put("js", new ArrayList<>());
 * config.put("startUrl", Arrays.asList(url));
 * config.put("endUrl", Arrays.asList(endUrl));
 * AbstractProcessorContext context = PluginFactory.getProcessorContext();
 * Map<String, String> cookieMap = ProcessorContextUtil.getCookieMap(context);
 * Map<String, Object> httpConfig = new HashMap<>();
 * httpConfig.put("cookies", Arrays.asList(cookieMap));
 * httpConfig.put("proxy", "");
 * httpConfig.put("header", "");
 * httpConfig.put("responseData", Arrays.asList("html", "cookie"));
 * config.put("httpConfig", httpConfig);
 * config.put("client", "webview");
 * config.put("visible", false);
 * config.put("visitType", "url");
 * logger.info("config={}", GsonUtils.toJson(config));
 * return config;
 * Created by zhouxinghai on 2017/5/26
 */
@Path(".[@type='grab']")
@Tag("service")
public class GrabService extends AbstractService {

    private String  appName;

    /**
     * css注入
     */
    private String  css;

    /**
     * js注入
     */
    private String  js;

    /**
     * proxy
     */
    private String  proxy;

    /**
     * header
     */
    private String  header;

    /**
     * client
     */
    private String  client    = "webview";

    /**
     * visitType
     */
    private String  visitType = "visitType";

    private Boolean visible   = false;

    private Boolean usePCUA   = true;

    @Tag("app-name")
    public String getAppName() {
        return appName;
    }

    @Node("app-name/text()")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getUsePCUA() {
        return usePCUA;
    }

    public void setUsePCUA(Boolean usePCUA) {
        this.usePCUA = usePCUA;
    }
}
