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

package com.datatrees.crawler.core.processor.operation;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;

/**
 * @author Jerry
 * @since 15:37 05/12/2017
 */
public final class OperationHelper {

    private OperationHelper() {
    }

    public static String getStringInput(Request request, Response response) {
        Object input = getInput(request, response);

        return (String) input;
    }

    public static Object getInput(Request request, Response response) {
        Object result = response.getOutPut();
        if (result == null) {
            result = request.getInput();
        }
        return result;
    }
}
