/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.core.model;

import com.datatrees.rawdatacentral.api.model.WebsiteConf;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月7日 下午3:54:23
 */
public class Website extends com.datatrees.crawler.core.domain.Website {

    /**
     *
     */
    private static final long serialVersionUID = -8902564827416468057L;
    private WebsiteConf websiteConf;

    /**
     * @return the websiteConf
     */
    public WebsiteConf getWebsiteConf() {
        return websiteConf;
    }

    /**
     * @param websiteConf the websiteConf to set
     */
    public void setWebsiteConf(WebsiteConf websiteConf) {
        this.websiteConf = websiteConf;
    }

}

