package com.treefinance.crawler.framework.process.operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Jerry
 * @since 16:04 2018/5/28
 */
public class OperationEntity implements Serializable {

    private Object                  source;

    private Object                  lastData;

    private Object                  data;

    private AbstractOperation       lastOperation;

    private List<AbstractOperation> undoOperations;

    private OperationEntity(Object data) {
        this.source = data;
        this.lastData = data;
        this.data = data;
    }

    public static OperationEntity wrap(Object content) {
        return new OperationEntity(content);
    }

    public Object getSource() {
        return source;
    }

    public Object getLastData() {
        return lastData;
    }

    public Object getData() {
        return data;
    }

    public void update(Object result, AbstractOperation operation) {
        this.lastData = this.data;
        this.data = result;
        this.lastOperation = operation;
    }

    public void update(AbstractOperation operation) {
        this.lastOperation = operation;
    }

    public boolean isEmpty() {
        return data == null;
    }

    public AbstractOperation getLastOperation() {
        return lastOperation;
    }

    public List<AbstractOperation> getUndoOperations() {
        if (undoOperations == null) {
            undoOperations = new ArrayList<>();
        }
        return undoOperations;
    }

    public <T extends AbstractOperation> void skip(T operation) {
        if (operation != null) getUndoOperations().add(operation);
    }

    @Override
    public String toString() {
        String msg = "Source: " + source + "\nLastData: " + lastData + "\nData: " + data + "\nLast operation: " + lastOperation;

        if (CollectionUtils.isNotEmpty(undoOperations)) {
            return msg + "\nSkipped operations: " + JSON.toJSONString(undoOperations);
        }

        return msg;
    }
}
