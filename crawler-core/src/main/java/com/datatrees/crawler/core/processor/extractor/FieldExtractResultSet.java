package com.datatrees.crawler.core.processor.extractor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry
 * @since 15:40 2018/5/15
 */
public class FieldExtractResultSet extends HashMap<String, FieldExtractResult> {

    public boolean isNotEmptyResult(String name) {
        FieldExtractResult obj = get(name);

        if (obj == null) return false;

        return obj.isNotEmpty();
    }
    
    public Map<String, Object> resultMap(){
        if (this.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> resultMap = new HashMap<>();
        forEach((id, wrapper) -> {
            Object result = wrapper.getResult();
            if (result != null) {
                resultMap.put(id, result);
            }
        });

        return Collections.unmodifiableMap(resultMap);
    }
}
