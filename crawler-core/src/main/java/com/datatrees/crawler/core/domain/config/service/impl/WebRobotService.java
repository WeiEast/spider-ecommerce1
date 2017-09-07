/**
 *
 */

package com.datatrees.crawler.core.domain.config.service.impl;

import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 7:25:55 PM
 */
@Path(".[@type='webrobot']")
@Tag("service")
public class WebRobotService extends AbstractService {

    private String  browserType;
    private String  pageLoadPattern;
    private Integer pageLoadTimeOut;

    @Tag("browser-type")
    public String getBrowserType() {
        return browserType;
    }

    @Node("browser-type/text()")
    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }

    @Tag("page-load-pattern")
    public String getPageLoadPattern() {
        return pageLoadPattern;
    }

    @Node("page-load-pattern/text()")
    public void setPageLoadPattern(String pageLoadPattern) {
        this.pageLoadPattern = pageLoadPattern;
    }

    @Tag("page-load-timeout")
    public Integer getPageLoadTimeOut() {
        return pageLoadTimeOut;
    }

    @Node("page-load-timeout/text()")
    public void setPageLoadTimeOut(Integer pageLoadTimeOut) {
        this.pageLoadTimeOut = pageLoadTimeOut;
    }

}
