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

package com.treefinance.crawler.framework.process.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.treefinance.crawler.framework.process.fields.FieldExtractResult;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 14:38 2018/8/1
 */
public class SegmentExtractObject extends HashMap<String, Object> implements ExtractObject {

    private static final String  TEMP_FIELD_NAME = "temp";
    private              String  name;
    private              String  resultClass;
    private              boolean flatField       = false;

    public SegmentExtractObject() {
    }

    public SegmentExtractObject(String segmentName, String resultClass) {
        this.name = segmentName;
        this.resultClass = resultClass;
    }

    private SegmentExtractObject(String name, String resultClass, boolean flatField) {
        this.name = name;
        if (StringUtils.isNotEmpty(resultClass)) {
            this.resultClass = resultClass;
        }
        this.flatField = flatField;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getResultClass() {
        return this.resultClass;
    }

    @Override
    public ExtractObject withFlatField(Object value) {
        ExtractObject extractObject = new SegmentExtractObject(this.name, this.resultClass, true);
        extractObject.put(this.name, value);
        return extractObject;
    }

    @Override
    public boolean isFlatField() {
        return flatField;
    }

    @Override
    public Object getFlatFieldValue() {
        return get(name);
    }

    @Override
    public boolean isValid(String name) {
        Object obj = get(name);

        if (obj == null) {
            return false;
        }

        return !(obj instanceof Iterable) || !IterableUtils.isEmpty((Iterable) obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setFieldExtractValue(String fieldName, Object fieldValue) {
        Object value;
        if (isFlatExtractObject(fieldValue)) {
            value = ((ExtractObject) fieldValue).getFlatFieldValue();
        } else {
            value = fieldValue;
        }

        if (value == null) {
            putIfAbsent(fieldName, null);
            return;
        }

        compute(fieldName, (key, oldValue) -> {
            if (oldValue == null) {
                return value;
            } else if (oldValue instanceof Collection) {
                merge((Collection) oldValue, value);

                return oldValue;
            } else {
                List<Object> newValue = new ArrayList<>();
                newValue.add(oldValue);

                merge(newValue, value);

                return newValue;
            }
        });
    }

    private boolean isFlatExtractObject(Object obj) {
        return obj instanceof ExtractObject && ((ExtractObject) obj).isFlatField();
    }

    private void merge(Collection<Object> newValue, Object value) {
        if (value instanceof Collection) {
            for (Object obj : (Collection) value) {
                if (isFlatExtractObject(obj)) {
                    newValue.add(((ExtractObject) obj).getFlatFieldValue());
                } else {
                    newValue.add(obj);
                }
            }
        } else {
            newValue.add(value);
        }
    }

    @Override
    public void setFieldExtractResult(FieldExtractResult fieldExtractResult) {
        String fieldName = fieldExtractResult.getFieldName();
        // skip the field named 'temp' or which result was null
        if (!fieldName.equalsIgnoreCase(TEMP_FIELD_NAME)) {
            Object result = fieldExtractResult.getResult();
            setFieldExtractValue(fieldName, result);
        }
    }

    @Override
    public void merge(ExtractObject extractObject) {
        putAll(extractObject);
    }

    @Override
    public Collection<ExtractObject> flatObjects() {
        List<ExtractObject> list = new ArrayList<>();

        Object child = remove(name);
        try {
            if (child instanceof ExtractObject) {
                SegmentExtractObject extractObject = new SegmentExtractObject(this.name, this.resultClass);
                extractObject.putAll((ExtractObject) child);
                extractObject.putAll(this);
                if (extractObject.isNotEmpty()) {
                    list.add(extractObject);
                }
            } else if (child instanceof Collection) {
                for (Object item : (Collection) child) {
                    if (item instanceof ExtractObject) {
                        SegmentExtractObject extractObject = new SegmentExtractObject(this.name, this.resultClass);
                        extractObject.putAll((ExtractObject) item);
                        extractObject.putAll(this);
                        if (extractObject.isNotEmpty()) {
                            list.add(extractObject);
                        }
                    }
                }
            } else if (isNotEmpty()) {
                list.add(this);
            }
        } finally {
            if (child != null) {
                put(name, child);
            }
        }

        return list;
    }

    @Override
    public void consumeWithFlatObjects(Consumer<ExtractObject> consumer) {
        Object child = remove(name);
        if (child instanceof ExtractObject) {
            try {
                SegmentExtractObject extractObject = new SegmentExtractObject(this.name, this.resultClass);
                extractObject.putAll((ExtractObject) child);
                extractObject.putAll(this);
                if (extractObject.isNotEmpty()) {
                    consumer.accept(extractObject);
                }
            } finally {
                put(name, child);
            }
        } else if (child instanceof Collection) {
            try {
                for (Object item : (Collection) child) {
                    if (item instanceof ExtractObject) {
                        SegmentExtractObject extractObject = new SegmentExtractObject(this.name, this.resultClass);
                        extractObject.putAll((ExtractObject) item);
                        extractObject.putAll(this);
                        if (extractObject.isNotEmpty()) {
                            consumer.accept(extractObject);
                        }
                    }
                }
            } finally {
                put(name, child);
            }
        } else {
            if (child != null) {
                put(name, child);
            }

            if (isNotEmpty()) {
                consumer.accept(this);
            }
        }
    }
}
