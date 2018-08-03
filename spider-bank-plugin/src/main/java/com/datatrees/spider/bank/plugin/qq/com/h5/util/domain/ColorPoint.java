package com.datatrees.spider.bank.plugin.qq.com.h5.util.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ColorPoint implements Serializable {

    private List<RelativeColorPoint> children = new ArrayList<>();

    private int                      absoluteX;

    private int                      absoluteY;

    private Color                    color;

    public ColorPoint() {
    }

    public ColorPoint(int absoluteX, int absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }

    public ColorPoint(int absoluteX, int absoluteY, Color color) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
        this.color = color;
    }

    public List<RelativeColorPoint> getChildren() {
        return children;
    }

    public void setChildren(List<RelativeColorPoint> children) {
        this.children = children;
    }

    public int getAbsoluteX() {
        return absoluteX;
    }

    public void setAbsoluteX(int absoluteX) {
        this.absoluteX = absoluteX;
    }

    public int getAbsoluteY() {
        return absoluteY;
    }

    public void setAbsoluteY(int absoluteY) {
        this.absoluteY = absoluteY;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    //@JSONField(serialize = false)
    //public int sum() {
    //    int sum = value + children.stream().mapToInt(e -> e.getColor()).sum();
    //    return sum;
    //}

    @JSONField(serialize = false)
    public int getLeftPotion() {
        int v = children.stream().mapToInt(e -> e.getRelativeX()).min().getAsInt();
        return Math.abs(v);
    }

    @JSONField(serialize = false)
    public int getRightPotion() {
        int v = children.stream().mapToInt(e -> e.getRelativeX()).max().getAsInt();
        return Math.abs(v);
    }

    @JSONField(serialize = false)
    public int getTopPotion() {
        int v = children.stream().mapToInt(e -> e.getRelativeY()).max().getAsInt();
        return Math.abs(v);
    }

    @JSONField(serialize = false)
    public int getButtomPotion() {
        int v = children.stream().mapToInt(e -> e.getRelativeY()).min().getAsInt();
        return Math.abs(v);
    }

    public boolean isNear(ColorPoint root, int precision) {
        boolean m = color.isNear(root.getColor(), precision) && children.size() == root.getChildren().size();

        for (int i = 0; i < children.size(); i++) {
            if (!m) {
                return m;
            }
            m = children.get(i).getColor().isNear(root.getChildren().get(i).getColor(), precision);
        }
        return m;
    }

    public int gap(ColorPoint root) {

        if (children.size() != root.getChildren().size()) {
            return Integer.MAX_VALUE;
        }
        int m = color.gap(root.getColor());

        for (int i = 0; i < children.size(); i++) {
            m += children.get(i).getColor().gap(root.getChildren().get(i).getColor());
        }
        return m;
    }

}