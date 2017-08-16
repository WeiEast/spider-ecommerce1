package com.datatrees.rawdatacentral.common.utils;

import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.RedisService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by zhouxinghai on 2017/6/29.
 */
public class BeanFactoryUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> cls) {
        T bean = applicationContext.getBean(cls);
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
