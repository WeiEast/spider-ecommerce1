package com.datatrees.rawdatacentral.plugin.common.qq.com.h5.util.domain;

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
