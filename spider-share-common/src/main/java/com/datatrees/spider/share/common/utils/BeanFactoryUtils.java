package com.datatrees.spider.share.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by zhouxinghai on 2017/6/29.
 */
public class BeanFactoryUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanFactoryUtils.applicationContext = applicationContext;
    }

}
