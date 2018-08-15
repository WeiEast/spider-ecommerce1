/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.framework.context.pipeline;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;

/**
 * @author Jerry
 * @since 20:37 2018/5/14
 */
public abstract class ProcessorValve extends ValveBase implements Processor {

    @Override
    public final void invoke(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws InvokeException, ResultEmptyException {
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

    protected boolean isSkipped(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        return false;
    }

    protected void triggerAfterSkipped(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) { }

    protected boolean isEnd(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        return false;
    }

    protected void preProcess(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception { }

    protected void postProcess(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception { }

    protected boolean ignoreException(@Nonnull Exception e) {
        return false;
    }

}
