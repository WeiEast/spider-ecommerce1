package com.datatrees.rawdatacentral.core.model;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.core.model.result.AbstractExtractResult;

/**
 * Created by wuminlang on 15/7/28.
 */
public class SubmitMessage {
    private ExtractMessage extractMessage;
    private Map extractResultMap;
    private AbstractExtractResult result;

    private Map<String, String> submitkeyResult = new HashMap<String, String>();

    /**
     * @return the result
     */
    public AbstractExtractResult getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(AbstractExtractResult result) {
        this.result = result;
    }

    public ExtractMessage getExtractMessage() {
        return extractMessage;
    }

    public void setExtractMessage(ExtractMessage extractMessage) {
        this.extractMessage = extractMessage;
    }

    public Map getExtractResultMap() {
        return extractResultMap;
    }

    public void setExtractResultMap(Map extractResultMap) {
        this.extractResultMap = extractResultMap;
    }

    /**
     * @return the submitkeyResult
     */
    public Map<String, String> getSubmitkeyResult() {
        return submitkeyResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SubmitMessage [extractMessage=" + extractMessage + ", result=" + result + "]";
    }

}
