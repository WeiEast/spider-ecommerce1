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

package com.datatrees.spider.bank.plugin.qq.com.h5.util.domain;

public class RelativeColorPoint extends ColorPoint {

    private int        relativeX;

    private int        relativeY;

    private Color      color;

    private ColorPoint parent;

    public RelativeColorPoint() {
    }

    public RelativeColorPoint(ColorPoint current, ColorPoint parent) {
        super.setAbsoluteX(current.getAbsoluteX());
        super.setAbsoluteY(current.getAbsoluteY());
        this.relativeX = current.getAbsoluteX() - parent.getAbsoluteX();
        this.relativeY = current.getAbsoluteY() - parent.getAbsoluteY();
        this.color = current.getColor();
        this.parent = parent;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public ColorPoint getParent() {
        return parent;
    }

    public void setParent(ColorPoint parent) {
        this.parent = parent;
    }
}
