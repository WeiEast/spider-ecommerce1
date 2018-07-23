package com.datatrees.crawler.core.processor.common;

import com.datatrees.crawler.core.processor.common.resource.BeanResource;

public class BeanResourceFactory implements BeanResource {

    private static BeanResourceFactory factory = null;

    private        BeanResource        beanResource;

    private BeanResourceFactory() {}

    public static BeanResourceFactory getInstance() {
        if (factory == null) {
            synchronized (BeanResourceFactory.class) {
                if (factory == null) {
                    factory = new BeanResourceFactory();
                }
            }
        }
        return factory;
    }

    @Override
    public Object getBean(String beanName) {
        if (beanResource != null) {
            return beanResource.getBean(beanName);
        } else {
            return null;
        }
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        if (beanResource != null) {
            return beanResource.getBean(beanType);
        } else {
            return null;
        }
    }

    @Override
    public <T> T getBean(String beanName, Class<T> beanType) {
        if (beanResource != null) {
            return beanResource.getBean(beanName, beanType);
        } else {
            return null;

        }
    }

    @Override
    public boolean containsBean(String beanName) {
        if (beanResource != null) {
            return beanResource.containsBean(beanName);
        } else {
            return false;
        }
    }

    /**
     * @return the beanResource
     */
    public BeanResource getBeanResource() {
        return beanResource;
    }

    /**
     * @param beanResource the beanResource to set
     */
    public void setBeanResource(BeanResource beanResource) {
        this.beanResource = beanResource;
    }

}
