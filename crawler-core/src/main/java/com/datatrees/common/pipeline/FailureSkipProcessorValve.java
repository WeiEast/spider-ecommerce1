package com.datatrees.common.pipeline;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @since 21:25 2018/5/23
 */
public abstract class FailureSkipProcessorValve extends ProcessorValve {

    @Override
    protected boolean ignoreException(@Nonnull Exception e) {
        return true;
    }
}
