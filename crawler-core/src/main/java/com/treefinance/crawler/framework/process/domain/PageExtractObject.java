package com.treefinance.crawler.framework.process.domain;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Jerry
 * @since 13:53 2018/8/2
 */
public class PageExtractObject extends SegmentExtractObject {

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    @Override
    public String getResultClass() {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    @Override
    public ExtractObject withFlatField(Object value) {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    @Override
    public boolean isFlatField() {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    @Override
    public Object getFlatFieldValue() {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    @Override
    public Collection<ExtractObject> flatObjects() {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    @Override
    public void consumeWithFlatObjects(Consumer<ExtractObject> consumer) {
        throw new UnsupportedOperationException("Unsupported method!");
    }

    public Object getSubExtractObject() {
        return get("subExtrat");
    }

    public Object getSubSeedObject(){
        return get("subSeed");
    }
}
