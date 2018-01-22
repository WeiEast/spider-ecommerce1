package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.MappingOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:02
 */
public class MappingOperationImpl extends Operation {

    private static final LoadingCache<String, Map<String, String>> CACHE = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().initialCapacity(2).build(new CacheLoader<String, Map<String, String>>() {
        @Override
        public Map<String, String> load(String groupName) throws Exception {
            String groupMapJson = PropertiesConfiguration.getInstance().get("mapping.group." + groupName + ".json");
            if (StringUtils.isNotBlank(groupMapJson)) {
                return GsonUtils.fromJson(groupMapJson, new TypeToken<Map<String, String>>() {}.getType());
            }
            return null;
        }
    });

    private String getMappingValue(String group, String key) {
        Map<String, String> mapping = CACHE.getUnchecked(group);
        if (mapping != null) {
            return mapping.get(key);
        }

        return null;
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        String input = getInput(request, response);
        MappingOperation operation = (MappingOperation) getOperation();

        String result = null;
        try {
            if (input != null && StringUtils.isNotEmpty(operation.getGroupName())) {
                result = getMappingValue(operation.getGroupName(), input);
            }
        } catch (Exception e) {
            logger.error("Error mapping field value, group: {}, input: {}, error: {}", operation.getGroupName(), input, e.getMessage());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Mapping field value, group: {}, input: {}, output: {}", operation.getGroupName(), input, result);
        }

        response.setOutPut(result);
    }
}
