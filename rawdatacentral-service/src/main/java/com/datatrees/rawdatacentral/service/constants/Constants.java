/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.service.constants;

import com.datatrees.common.conf.PropertiesConfiguration;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 下午5:26:35
 */
public interface Constants {

    public String WEBSITE_CONTEXT_ID_PREFIX   = "WEBSITE_CONTEXT_ID_PREFIX";
    public String WEBSITE_CONTEXT_NAME_PREFIX = "WEBSITE_CONTEXT_NAME_PREFIX";
    public String BANK_MAP_KEY                = "BANK_MAP_KEY";
    public String BANK_EMAIL_MAP_KEY          = "BANK_EMAIL_MAP_KEY";
    public String KEYWORD_MAP_KEY             = "KEYWORD_MAP_KEY";
    public String OPERATOR_MAP_KEY            = "OPERATOR_MAP_KEY";
    public String ECOMMERCE_MAP_KEY           = "ECOMMERCE_MAP_KEY";
    public String BANK_WEBSIYE_MAP_KEY        = "BANK_WEBSIYE_MAP_KEY";
    public String PROPERTIES_MAP_KEY          = "PROPERTIES_MAP_KEY";
    String GSON_TYPE                     = "GSON_TYPE";
    String SUBMIT_TO_GATEWAY_KEY         = "CLAWER_REQUEST_";
    String OBTAIN_FROM_GATEWAY_KEY       = "GATEWAY_RESPONSE_";
    int    REDIS_KEY_TIMEOUT             = PropertiesConfiguration.getInstance().getInt("rawdatacentral.redisKey.timeout", 600);
    int    GATEWAY_MAX_WAITTIME          = PropertiesConfiguration.getInstance().getInt("rawdatacentral.gateway.timeout", 90000);
    String DEFAULT_ENCODE_CHARSETNAME    = "UTF-8";
    int    PROXY_CALLBACK_CORE_POOL_SIZE = PropertiesConfiguration.getInstance().getInt("proxy.callback.coreSize", 1);
    int    PROXY_CALLBACK_MAX_POOL_SIZE  = PropertiesConfiguration.getInstance().getInt("proxy.callback.maxSize", 5);
    int    PROXY_CALLBACK_MAX_TASK_SIZE  = PropertiesConfiguration.getInstance().getInt("proxy.callback.maxtasknum", 100);

}
