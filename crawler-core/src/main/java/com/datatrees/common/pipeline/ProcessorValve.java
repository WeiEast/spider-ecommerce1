package com.datatrees.common.pipeline;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;

/**
 * @author Jerry
 * @since 20:37 2018/5/14
 */
public abstract class ProcessorValve extends ValveBase implements Processor {

    @Override
    public final void invoke(@Nonnull Request request, @Nonnull Response response) throws InvokeException, ResultEmptyException {
        if (!isSkipped(request, response)) {
            try {
                preProcess(request, response);
                process(request, response);
                postProcess(request, response);
            } catch (ResultEmptyException | InvokeException e) {
                throw e;
            } catch (Exception e) {
                if (ignoreException(e)) {
                    logger.error("Error invoking processor valve!", e);
                } else {
                    throw new ProcessingException("Error to invoke processor valve!", e);
                }
            }
        } else {
            triggerAfterSkipped(request, response);
        }

        logger.debug("processor output: {}", response.getOutPut());

        Valve next = getNext();
        if (next != null && !isEnd(request, response)) {
            next.invoke(request, response);
        }
    }

    protected boolean isSkipped(@Nonnull Request request, @Nonnull Response response) {
        return false;
    }

    protected void triggerAfterSkipped(@Nonnull Request request, @Nonnull Response response) { }

    protected boolean isEnd(@Nonnull Request request, @Nonnull Response response) {
        return false;
    }

    protected void preProcess(@Nonnull Request request, @Nonnull Response response) throws Exception { }

    protected void postProcess(@Nonnull Request request, @Nonnull Response response) throws Exception { }

    protected boolean ignoreException(@Nonnull Exception e) {
        return false;
    }

}
