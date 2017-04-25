package com.datatrees.rawdatacentral.domain.message;

import com.datatrees.rawdatacentral.domain.enums.OperationEnum;
import com.datatrees.rawdatacentral.domain.enums.TaskLogStatusEnum;

/**
 * 任务状态日志
 * Created by zhouxinghai on 2017/4/25.
 */
public class TaskStatusLogMessage extends TaskMessage {

    /**
     * 操作代码
     */
    private String operationCode;

    /**
     * 操作名称
     */
    private String operationName;

    /**
     * 任务状态代码
     */
    private String statusCode;

    /**
     * 任务状态名称
     */
    private String statusName;

    /**
     * 返回代码
     */
    private String responseCode = "0";

    /**
     * 返回消息
     */
    private String responseMsg;

    /**
     * 设置操作类型
     *
     * @param operationEnum 操作类型
     */
    public void setOperation(OperationEnum operationEnum) {
        this.operationCode = operationEnum.getCode();
        this.operationName = operationEnum.getName();
    }

    /**
     * 设置状态码
     *
     * @param statusEnum 任务状态
     */
    public void setStatus(TaskLogStatusEnum statusEnum) {
        this.statusCode = statusEnum.getCode();
        this.statusName = statusEnum.getName();
    }


    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
