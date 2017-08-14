package com.datatrees.rawdatacentral.api;

/**
 * 消息服务
 * Created by zhouxinghai on 2017/5/11.
 */
public interface MessageService {

    /**
     * 向网关发送任务日志消息
     * @param taskId 任务id
     * @param msg 操作信息
     * @return
     */
    boolean sendTaskLog(Long taskId, String msg);

    /**
     * 向网关发送任务日志消息
     * @param taskId 任务id
     * @param msg 操作信息
     * @param errorDetail 错误信息
     * @return
     */
    boolean sendTaskLog(Long taskId, String msg, String errorDetail);

    /**
     * 发送交互指令
     * @param taskId 任务id
     * @param directive 指令
     * @param remark 指令内容 
     * @return 指令ID
     */
    String sendDirective(Long taskId, String directive, String remark);

    /**
     * 发送消息
     * @param topic 订阅主题
     * @param msg 消息
     * @return
     */
    boolean sendMessage(String topic, Object msg);

    /**
     * 发送消息
     * @param topic 订阅主题
     * @param msg 消息
     * @param charsetName JSON个时候发送编码类型
     * @param maxRetry  失败重试次数
     * @return
     */
    boolean sendMessage(String topic, Object msg, String charsetName, int maxRetry);

    /**
     * 发送登录成功消息
     * @return
     */
    boolean sendLoginSuccessMessage(Long taskId, String websiteName);
}
