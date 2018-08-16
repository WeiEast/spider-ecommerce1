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

package com.treefinance.crawler.framework.context;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.properties.Properties;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.domain.config.service.impl.TaskHttpService;
import com.treefinance.crawler.framework.extension.manager.PluginManager;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 13:54 25/01/2018
 */
public abstract class ProcessContext implements SpiderContext {

    protected final Logger                      logger = LoggerFactory.getLogger(getClass());

    private final   Website                     website;

    private final   Map<String, AbstractPlugin> pluginMetadataMap;

    private         PluginManager               pluginManager;

    public ProcessContext(@Nonnull final Website website) {
        Preconditions.notNull("website", website);
        this.website = website;
        this.pluginMetadataMap = new ConcurrentHashMap<>();
    }

    public Website getWebsite() {
        return website;
    }

    public String getWebsiteName() {
        return website.getWebsiteName();
    }

    public String getWebsiteType() {
        return website.getWebsiteType();
    }

    @Override
    public Map<String, AbstractPlugin> getPluginMetadataMap() {
        return pluginMetadataMap;
    }

    @Override
    public void registerPlugins(@Nonnull List<AbstractPlugin> pluginMetadataList) {
        if (CollectionUtils.isNotEmpty(pluginMetadataList)) {
            for (AbstractPlugin pluginMetadata : pluginMetadataList) {
                if (pluginMetadata != null) {
                    registerPlugin(pluginMetadata);
                }
            }
        }
    }

    @Override
    public void registerPlugin(@Nonnull AbstractPlugin pluginMetadata) {
        Objects.requireNonNull(pluginMetadata);
        if (StringUtils.isEmpty(pluginMetadata.getId())) {
            logger.warn("Failed to register plugin metadata >>> The plugin id is missing!");
            return;
        }

        pluginMetadataMap.put(pluginMetadata.getId(), pluginMetadata);
    }

    @Override
    public AbstractPlugin getPluginMetadataById(@Nonnull String pluginId) {
        return pluginMetadataMap.get(pluginId);
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public AbstractService getDefaultService() {
        AbstractService service = null;
        if (null != website && null != website.getSearchConfig() && null != website.getSearchConfig().getProperties()) {
            Properties properties = website.getSearchConfig().getProperties();
            Boolean useTaskHttp = properties.getUseTaskHttp();
            if (BooleanUtils.isTrue(useTaskHttp)) {
                service = new TaskHttpService();
                service.setServiceType("task_http");
            }
        }
        return service;
    }

}
