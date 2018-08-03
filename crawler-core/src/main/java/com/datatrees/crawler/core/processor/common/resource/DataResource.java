package com.datatrees.crawler.core.processor.common.resource;

import java.util.Map;

@Deprecated
public interface DataResource extends Resource {

    public Object getData(Map<String, Object> parameters);

    public boolean ttlSave(String key, String value, long timeOut);

    public boolean ttlPush(String key, String value, long timeOut);

    public boolean sendToQueue(Map<String, Object> parameters);

    public boolean clearData(Map<String, Object> parameters);

}
