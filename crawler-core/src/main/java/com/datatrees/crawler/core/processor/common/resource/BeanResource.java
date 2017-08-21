/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common.resource;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月10日 下午10:48:39
 */
public interface BeanResource extends Resource {

    public Object getBean(String beanName);

    public <T> T getBean(Class<T> beanType);

    public <T> T getBean(String beanName, Class<T> beanType);

    public boolean containsBean(String beanName);


}
