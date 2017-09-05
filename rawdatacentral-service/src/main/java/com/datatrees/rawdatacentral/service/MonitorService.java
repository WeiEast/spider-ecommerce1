package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * 监控
 * Created by zhouxinghai on 2017/9/4
 */
public interface MonitorService {

    //void

    /**
     * 发送任务完成消息
     * @param taskId 任务id
     */
    void sendTaskCompleteMsg(Long taskId, Integer errorCode, String errorMsg);

    /**
     * 发送任务日志消息
     * @param taskId      任务id
     * @param errorCode   错误代码
     * @param errorMsg    错误信息
     * @param errorDetail 错误详细信息
     */
    void sendTaskLog(Long taskId, String msg, Integer errorCode, String errorMsg, String errorDetail);

    /**
     * 发送任务日志消息
     * @param taskId 任务id
     * @param result 处理结果
     */
    void sendTaskLog(Long taskId, String msg, HttpResult result);

    /**
     * 发送任务日志消息
     * @param taskId 任务id
     * @param msg    操作信息
     * @return
     */
    void sendTaskLog(Long taskId, String msg);
}
