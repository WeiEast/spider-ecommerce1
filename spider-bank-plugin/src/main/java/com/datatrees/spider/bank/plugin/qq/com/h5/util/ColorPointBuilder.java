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

package com.datatrees.spider.bank.plugin.qq.com.h5.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.spider.bank.plugin.qq.com.h5.util.config.ColorSelect;
import com.datatrees.spider.bank.plugin.qq.com.h5.util.config.ColorSelectConfig;
import com.datatrees.spider.bank.plugin.qq.com.h5.util.domain.Color;
import com.datatrees.spider.bank.plugin.qq.com.h5.util.domain.ColorPoint;
import com.datatrees.spider.bank.plugin.qq.com.h5.util.domain.RelativeColorPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorPointBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ColorPointBuilder.class);

    public static ColorPoint getSearchColorPoint(Color[][] rgbs, String config) {
        ColorSelectConfig selectConfig = JSON.parseObject(config, new TypeReference<ColorSelectConfig>() {});
        ColorPoint root = getColorPoint(rgbs, selectConfig.getRoot());
        for (ColorSelect childSelect : selectConfig.getChildren()) {
            ColorPoint child = getColorPoint(rgbs, childSelect);
            if (null != child) {
                root.getChildren().add(new RelativeColorPoint(child, root));
            }
        }
        return root;
    }

    public static void fillChildren(Color[][] rgbs, ColorPoint root, List<RelativeColorPoint> children) {
        for (RelativeColorPoint relative : children) {
            ColorPoint child = getColorPoint(rgbs, root, relative);
            if (null != child) {
                root.getChildren().add(new RelativeColorPoint(child, root));
            }
        }
    }

    //public static void fillChildren(int[][] rgbs, List<RelativeColorPoint> children, int absoluteX, int absoluteY) {
    //    ColorPoint root = getColorPoint(rgbs, absoluteX, absoluteY);
    //    for (RelativeColorPoint relative : children) {
    //        ColorPoint child = getColorPoint(rgbs, root, relative);
    //        if (null != child) {
    //            root.getChildren().add(new RelativeColorPoint(child, root));
    //        }
    //    }
    //}

    public static ColorPoint getColorPoint(Color[][] rgbs, ColorSelect select) {
        int with = rgbs.length;
        int height = rgbs[1].length;
        int absoluteX = (int) Math.ceil(with * select.getPercentageX());
        int absoluteY = (int) Math.ceil(height * select.getPercentageY());
        return getColorPoint(rgbs, absoluteX, absoluteY);
    }

    public static ColorPoint getColorPoint(Color[][] rgbs, ColorPoint root, RelativeColorPoint relative) {
        int absoluteX = root.getAbsoluteX() + relative.getRelativeX();
        int absoluteY = root.getAbsoluteY() + relative.getRelativeY();
        return getColorPoint(rgbs, absoluteX, absoluteY);
    }

    public static ColorPoint getColorPoint(Color[][] rgbs, int absoluteX, int absoluteY) {
        int with = rgbs.length;
        int height = rgbs[1].length;
        if (absoluteX < 0 || absoluteX > with || absoluteY < 0 || absoluteY > height) {
            logger.error("invalid position,with={},height={},absoluteX={},absoluteY={}", with, height, absoluteX, absoluteY);
            return null;
        }
        Color value = rgbs[absoluteX - 1][absoluteY - 1];
        ColorPoint point = new ColorPoint(absoluteX, absoluteY, value);
        return point;
    }

}
