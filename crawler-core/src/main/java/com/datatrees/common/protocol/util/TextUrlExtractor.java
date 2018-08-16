/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
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

package com.datatrees.common.protocol.util;

import java.util.ArrayList;
import java.util.List;

import com.datatrees.common.util.PatternUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:52:48 AM
 */
public class TextUrlExtractor {

    public static List<String> extractor(String data, String regex, int index) {
        List<String> result = new ArrayList<String>();
        if (StringUtils.isNotEmpty(data) && StringUtils.isNotEmpty(regex)) {
            result.addAll(PatternUtils.getContents(data, regex, index));
        }
        return result;
    }

    public static List<String> extractor(List<String> data, String regex, int index) {
        StringBuilder sb = new StringBuilder();
        if (data != null && data.size() > 0) {
            for (String split : data) {
                sb.append(split);
            }
        }
        return extractor(sb.toString(), regex, index);
    }

}
