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

public class Color {

    private int red;

    private int green;

    private int blue;

    private int alpha = 255;

    public Color() {
    }

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public boolean isNear(Color othor, int precision) {
        boolean m = Math.abs(red - othor.getRed()) <= precision && Math.abs(blue - othor.getBlue()) <= precision &&
                Math.abs(green - othor.getGreen()) <= precision;
        return m;
    }

    public int gap(Color othor) {
        return Math.abs(red - othor.getRed()) + Math.abs(blue - othor.getBlue()) + Math.abs(green - othor.getGreen());
    }

    @Override
    public String toString() {
        return red + "," + green + "," + blue;
    }
}
