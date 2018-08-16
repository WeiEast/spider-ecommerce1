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

package com.datatrees.spider.bank.plugin.qq.com.h5.util.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColorSelectConfig implements Serializable {

    private ColorSelect       root;

    private List<ColorSelect> children = new ArrayList<>();

    public ColorSelect getRoot() {
        return root;
    }

    public void setRoot(ColorSelect root) {
        this.root = root;
    }

    public List<ColorSelect> getChildren() {
        return children;
    }

    public void setChildren(List<ColorSelect> children) {
        this.children = children;
    }
}
