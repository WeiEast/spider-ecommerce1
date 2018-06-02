package com.datatrees.crawler.core.processor.extractor;

import java.util.*;

/**
 * @author Jerry
 * @since 15:40 2018/5/15
 */
public class FieldExtractResultSet extends HashMap<String, FieldExtractResult> {

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
