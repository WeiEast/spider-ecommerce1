package com.datatrees.common.pipeline;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @since 00:58 2018/5/15
 */
public interface Processor {

    void process(@Nonnull Request request, @Nonnull Response response) throws Exception;

}
