package com.datatrees.rawdatacentral.core.service;

/**
 * 消息服务
 * Created by zhouxinghai on 2017/5/11.
 */
public interface MessageService {

    /**
     * 向网关发送任务日志消息
     * @param taskId 任务id
     * @param msg 操作信息
     * @param errorDetail 错误信息
     * @return
     */
    public boolean sendTaskLog(Long taskId, String msg, String errorDetail);

    /**
     * 向网关发送任务日志消息
     * @param taskId 任务id
     * @param msg 操作信息
     * @return
     */
    public boolean sendTaskLog(Long taskId, String msg);
}
