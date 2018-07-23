package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
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
public class MappingOperationImpl extends Operation<MappingOperation> {

    private static final LoadingCache<String, Map<String, String>> CACHE = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES)
            .softValues().initialCapacity(2).build(new CacheLoader<String, Map<String, String>>() {
                @Override
                public Map<String, String> load(String groupName) throws Exception {
                    String groupMapJson = PropertiesConfiguration.getInstance().get("mapping.group." + groupName + ".json");
                    if (StringUtils.isNotBlank(groupMapJson)) {
                        return GsonUtils.fromJson(groupMapJson, new TypeToken<Map<String, String>>() {}.getType());
                    }
                    return null;
                }
            });

    public MappingOperationImpl(@Nonnull MappingOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    private String getMappingValue(String group, String key) {
        Map<String, String> mapping = CACHE.getUnchecked(group);
        if (mapping != null) {
            return mapping.get(key);
        }

        return null;
    }

    @Override
    protected Object doOperation(@Nonnull MappingOperation operation, @Nonnull Object operatingData, @Nonnull Request request,
            @Nonnull Response response) throws Exception {
        String input = (String) operatingData;

        String result = null;
        try {
            if (StringUtils.isNotEmpty(operation.getGroupName())) {
                result = getMappingValue(operation.getGroupName(), input);
            }
        } catch (Exception e) {
            logger.error("Error mapping field value, group: {}, input: {}, error: {}", operation.getGroupName(), input, e.getMessage());
        }

        logger.debug("Mapping field value, group: {}, input: {}, output: {}", operation.getGroupName(), input, result);

        return result;
    }
}
