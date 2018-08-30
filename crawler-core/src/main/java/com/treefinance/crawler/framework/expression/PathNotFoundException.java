package com.treefinance.crawler.framework.expression;

/**
 * @author Jerry
 * @since 19:02 2018/8/30
 */
class PathNotFoundException extends Exception {

    private String path;

    public PathNotFoundException(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
