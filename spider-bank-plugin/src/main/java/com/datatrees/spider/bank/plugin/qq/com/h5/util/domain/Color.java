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
