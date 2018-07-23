package com.treefinance.crawler.framework.format;

import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 15:50 2018/5/14
 */
public abstract class AbstractFormatter<R> implements Formatter<R> {

    protected final Logger   logger = LoggerFactory.getLogger(getClass());

    private         Class<R> resultClass;

    public AbstractFormatter() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public AbstractFormatter(Class<R> resultClass) {
        if (resultClass != null) {
            this.resultClass = resultClass;
        } else {
            this.resultClass = (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
    }

    @Override
    public boolean supportResultType(Object value) {
        return resultClass.isInstance(value);
    }

}
