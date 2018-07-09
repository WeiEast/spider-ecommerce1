package com.datatrees.common.pipeline;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 00:12 2018/5/24
 */
public abstract class ProcessorInvokerAdapter implements Processor, Invoker {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void invoke(@Nonnull Request request, @Nonnull Response response) throws InvokeException, ResultEmptyException {
        try {
            preProcess(request, response);
            process(request, response);
            postProcess(request, response);
        } catch (ResultEmptyException | InvokeException e) {
            throw e;
        } catch (Exception e) {
            throw new ProcessingException("Error invoking processor.", e);
        }
    }

    protected void preProcess(@Nonnull Request request, @Nonnull Response response) throws Exception {}

    protected void postProcess(@Nonnull Request request, @Nonnull Response response) throws Exception {}

}
