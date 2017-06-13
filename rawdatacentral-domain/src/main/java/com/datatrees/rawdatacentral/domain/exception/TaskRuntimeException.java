package com.datatrees.rawdatacentral.domain.exception;

/**
 * Created by zhouxinghai on 2017/6/2.
 */
public class TaskRuntimeException extends RuntimeException {

    /**
     * 任务ID
     */
    private long   taskId    = 0;

    /**
     * 错误代码
     */
    private int    errorCode = -1;

    /**
     * 错误信息
     */
    private String errorMsg;

    public TaskRuntimeException(long taskId, int errorCode, String errorMsg) {
        super(errorMsg);
        this.taskId = taskId;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public TaskRuntimeException(long taskId, String errorMsg) {
        super(errorMsg);
        this.taskId = taskId;
        this.errorMsg = errorMsg;
    }
}
