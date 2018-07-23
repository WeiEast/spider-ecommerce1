package com.datatrees.rawdatacentral.domain.vo;

/**
 * 步骤
 * @author zhouxinghai
 * @date 2017/11/29
 */
public class Step {

    /**
     * 步骤代码
     */
    private int    stepCode;

    /**
     * 名称
     */
    private String stepName;

    /**
     * 时间戳
     */
    private long   timestamp;

    public int getStepCode() {
        return stepCode;
    }

    public void setStepCode(int stepCode) {
        this.stepCode = stepCode;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
