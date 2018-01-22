/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
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

package com.treefinance.crawler.framework.extension;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.PluginPhase;
import com.datatrees.crawler.core.domain.config.plugin.PluginType;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Jerry
 * @since 10:12 17/01/2018
 */
public class PluginFile implements Serializable {

    private AbstractPlugin metadata;
    private File           file;
    private boolean        reload;

    public PluginFile() {
    }

    public PluginFile(AbstractPlugin metadata, File file) {
        this.metadata = Objects.requireNonNull(metadata);
        this.file = Objects.requireNonNull(file);
    }

    public AbstractPlugin getMetadata() {
        return metadata;
    }

    public void setMetadata(AbstractPlugin metadata) {
        this.metadata = metadata;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public PluginType getType() {
        return getMetadata().getType();
    }

    public PluginPhase getPhase() {
        return getMetadata().getPhase();
    }

    public String getExtraConfig() {
        return getMetadata().getExtraConfig();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("plugin", metadata.getId()).append("file", file.getAbsolutePath()).append("reload", reload).toString();
    }
}
