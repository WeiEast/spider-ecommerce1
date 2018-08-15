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

package com.treefinance.crawler.plugin.alipay.zmxy;

import java.util.List;
import java.util.Map;

import com.treefinance.toolkit.util.json.Jackson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从json文本中提取芝麻信用分
 * @author Jerry
 * @since 15:23 28/12/2017
 */
public final class PointExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PointExtractor.class);

    private PointExtractor() {
    }

    public static String extract(String content) {
        JsonResult jsonResult = Jackson.parse(content, JsonResult.class);
        if (jsonResult != null && "success".equals(jsonResult.getStat()) && CollectionUtils.isNotEmpty(jsonResult.getSmallCardList())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Zmxy parsed jsonResult : {}", jsonResult);
            }

            return getPoint(jsonResult);
        }

        return null;
    }

    private static String getPoint(JsonResult result) {
        String point = null;
        for (Card card : result.getSmallCardList()) {
            if ("zhima".equals(card.getBizCode())) {
                Map<String, String> pointMap = card.getPoint();
                if (pointMap != null) {
                    point = pointMap.get("content");
                }
                break;
            }
        }

        return point;
    }

    private static class JsonResult {

        private String     stat;
        private List<Card> smallCardList;

        public String getStat() {
            return stat;
        }

        public void setStat(String stat) {
            this.stat = stat;
        }

        public List<Card> getSmallCardList() {
            return smallCardList;
        }

        public void setSmallCardList(List<Card> smallCardList) {
            this.smallCardList = smallCardList;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("stat", stat).append("smallCardList", smallCardList).toString();
        }
    }

    private static class Card {

        private String              bizCode;
        private String              title;
        private Map<String, String> point;

        public String getBizCode() {
            return bizCode;
        }

        public void setBizCode(String bizCode) {
            this.bizCode = bizCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Map<String, String> getPoint() {
            return point;
        }

        public void setPoint(Map<String, String> point) {
            this.point = point;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("bizCode", bizCode).append("title", title).append("point", point).toString();
        }
    }
}
