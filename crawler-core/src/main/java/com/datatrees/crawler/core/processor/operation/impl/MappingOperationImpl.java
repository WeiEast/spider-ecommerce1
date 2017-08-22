package com.datatrees.crawler.core.processor.operation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.CacheUtil;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.MappingOperation;
import com.datatrees.crawler.core.processor.operation.Operation;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:02
 */
public class MappingOperationImpl extends Operation {

    private static final Logger log = LoggerFactory.getLogger(MappingOperationImpl.class);
    private static String MAPPING_CACHE_KEY = "MAPPING_CACHE_KEY";

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Map> getCachedMapping() {
        Map mapping = (Map) CacheUtil.getInstance().getObject(MAPPING_CACHE_KEY);
        if (mapping == null) {
            synchronized (MAPPING_CACHE_KEY) {
                mapping = (Map) CacheUtil.getInstance().getObject(MAPPING_CACHE_KEY);
                if (mapping == null) {
                    mapping = new HashMap();
                    CacheUtil.getInstance().insertObject(MAPPING_CACHE_KEY, mapping);
                    String[] groupNames = PropertiesConfiguration.getInstance().get("mapping.group.names", "tradeStatus,tradeType").split(",");
                    for (String groupName : groupNames) {
                        try {
                            String groupMapJson = PropertiesConfiguration.getInstance().get("mapping.group." + groupName + ".json");
                            Map valueMap = null;
                            if (StringUtils.isNotBlank(groupMapJson)) {
                                valueMap = (Map) GsonUtils.fromJson(groupMapJson, Map.class);
                                if (valueMap != null) {
                                    mapping.put(groupName, valueMap);
                                } else {
                                    log.warn("format " + groupName + " json value failed with string: " + groupMapJson);
                                }
                            }
                        } catch (Exception e) {
                            log.error("format " + groupName + " json value error " + e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return mapping;
    }


    @Override
    public void process(Request request, Response response) throws Exception {
        String result = null;
        MappingOperation operation = (MappingOperation) getOperation();
        try {
            String original = getInput(request, response);
            Map<String, Map> cachedMapping = getCachedMapping();
            String groupName = operation.getGroupName();
            if (original != null && groupName != null && cachedMapping != null && cachedMapping.get(groupName) != null
                    && cachedMapping.get(groupName).get(original) != null) {
                result = cachedMapping.get(groupName).get(original) + "";
            }
        } catch (Exception e) {
            log.error("mapping error! " + operation + "exception :" + e.getMessage());
            result = null;
        }
        if (log.isDebugEnabled()) {
            log.debug(operation + " result:" + result);
        }

        response.setOutPut(result);
    }
}
