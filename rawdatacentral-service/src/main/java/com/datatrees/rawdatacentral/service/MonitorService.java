package com.datatrees.rawdatacentral.service;

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
    void sendTaskCompleteMsg(Long taskId, boolean status, Integer errorCode, String errorMsg);

    /**
     * 发送任务失败信息
     * 如果任务成功,这些失败信息是没有意义的
     * 但是如果任务失败了,那么最后的错误信息就很重要,但是要防止处理过的消息把真实错误信息给覆盖了
     * @param taskId      任务id
     * @param errorCode   错误代码
     * @param errorMsg    错误信息
     * @param errorDetail 错误详细信息
     */
    void sendTaskErrorMsg(Long taskId, Integer errorCode, String errorMsg, String errorDetail);
}
