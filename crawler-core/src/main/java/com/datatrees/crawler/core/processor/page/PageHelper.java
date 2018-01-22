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

package com.datatrees.crawler.core.processor.page;

import java.util.List;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.page.Regexp;
import com.datatrees.crawler.core.domain.config.page.Replacement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 17:20 26/12/2017
 */
public final class PageHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageHelper.class);

    private PageHelper() {
    }

    public static String replaceText(String content, List<Replacement> replacements) {
        String result = content;

        if (CollectionUtils.isNotEmpty(replacements)) {
            for (Replacement rm : replacements) {
                result = result.replaceAll(rm.getFrom(), rm.getTo());
            }
        }

        return result;
    }

    public static String getTextByRegexp(String content, Regexp regexp) {
        if (regexp == null || StringUtils.isEmpty(regexp.getRegex())) {
            return content;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Extract content by regexp[pattern: {}, index: {}] <<< {}", regexp.getRegex(), regexp.getIndex(), content);
        }

        String result = PatternUtils.group(content, regexp.getRegex(), regexp.getIndex());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Extracted result >>> {}", result);
        }

        return result;
    }
}
