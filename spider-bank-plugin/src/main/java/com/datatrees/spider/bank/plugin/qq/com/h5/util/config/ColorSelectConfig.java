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
