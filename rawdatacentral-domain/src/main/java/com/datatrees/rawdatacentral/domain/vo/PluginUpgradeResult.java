package com.datatrees.rawdatacentral.domain.vo;

import java.io.File;

/**
 * 插件更新结果
 * Created by zhouxinghai on 2017/7/7.
 */
public class PluginUpgradeResult {

    /**
     * 插件文件
     */
    private File    file;
    /**
     * 是否需要重新加载
     */
    private Boolean forceReload;
    /**
     * 文件版本
     */
    private String  version;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Boolean getForceReload() {
        return forceReload;
    }

    public void setForceReload(Boolean forceReload) {
        this.forceReload = forceReload;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
