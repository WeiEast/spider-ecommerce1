package com.datatrees.rawdatacentral.core.common;

import com.datatrees.crawler.core.processor.common.resource.BeanResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author <A HREF="mailto:wang_cheng@treefinance.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 10, 2014 4:20:16 PM
 */
@Scope("singleton")
@Component
public class BeansFactory implements BeanFactoryAware, BeanResource {

    private static BeanFactory beanFactory = null;

    /**
     * @param servName
     * @return
     */
    public static Object getService(String servName) {
        return beanFactory.getBean(servName);
    }

    /**
     * @param servName
     * @param clazz
     * @return
     */
    public static Object getService(String servName, Class clazz) {
        return beanFactory.getBean(servName, clazz);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans
     * .factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.BeanResource#getBean(java.lang.String)
     */
    @Override
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.BeanResource#getBean(java.lang.Class)
     */
    @Override
    public <T> T getBean(Class<T> beanType) {
        return beanFactory.getBean(beanType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.BeanResource#getBean(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T> T getBean(String beanName, Class<T> beanType) {
        return beanFactory.getBean(beanName, beanType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.BeanResource#containsBean(java.lang.
     * String)
     */
    @Override
    public boolean containsBean(String beanName) {
        return beanFactory.containsBean(beanName);
    }
}
