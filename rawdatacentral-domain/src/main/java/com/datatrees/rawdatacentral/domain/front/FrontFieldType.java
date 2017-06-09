/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.domain.front;

import java.util.Map;

/**
 *
 * @author  <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since   2017年6月9日 下午2:42:51 
 */
public enum FrontFieldType {
    USERNAME,PASSWORD,RANDOM,CODE;//用户名、密码、短信验证码、图片验证码
    private static Map<String,FrontField> frontFieldTypeMap;
}
