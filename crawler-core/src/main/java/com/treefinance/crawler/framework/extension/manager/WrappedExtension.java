package com.treefinance.crawler.framework.extension.manager;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;

/**
 * @author Jerry
 * @since 10:49 23/01/2018
 */
public class WrappedExtension<T> {

    private final AbstractPlugin metadata;
    private       T              extension;

    public WrappedExtension(AbstractPlugin metadata) {
        this.metadata = metadata;
    }

    public WrappedExtension(AbstractPlugin metadata, T extension) {
        this.metadata = metadata;
        this.extension = extension;
    }

    public AbstractPlugin getMetadata() {
        return metadata;
    }

    public T getExtension() {
        return extension;
    }

    public void setExtension(T extension) {
        this.extension = extension;
    }
}
