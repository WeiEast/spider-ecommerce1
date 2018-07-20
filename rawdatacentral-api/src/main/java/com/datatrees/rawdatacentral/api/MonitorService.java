package com.datatrees.rawdatacentral.api;

import java.util.List;

import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.HttpResult;

/**
 * 监控
 * Created by zhouxinghai on 2017/9/4
 */
public interface MonitorService {

    /**
     * 初始化监控信息
     * @param taskId      任务id
     * @param websiteName 配置名称
     * @param userName    用户名
     */
    void initTask(Long taskId, String websiteName, Object userName);

    /**
     * 发送任务完成消息
     * @param taskId 任务id
     */
    void sendTaskCompleteMsg(Long taskId, String websiteName, Integer errorCode, String errorMsg);

    /**
     * 发送任务日志消息
     * @param taskId      任务id
     * @param errorCode   错误代码
     * @param errorMsg    错误信息
     * @param errorDetail 错误详细信息
     */
    void sendTaskLog(Long taskId, String websiteName, String msg, Integer errorCode, String errorMsg, String errorDetail);

    /**
     * 发送任务日志消息
     * @param taskId 任务id
     * @param result 处理结果
     */
    void sendTaskLog(Long taskId, String websiteName, String msg, HttpResult result);

    /**
     * 发送任务日志消息
     * @param taskId    任务id
     * @param errorCode 错误信息
     */
    void sendTaskLog(Long taskId, String websiteName, String msg, ErrorCode errorCode);

    /**
     * 发送任务日志消息
     * @param taskId    任务id
     * @param errorCode 错误信息
     */
    void sendTaskLog(Long taskId, String websiteName, String msg, ErrorCode errorCode, String errorDetail);

    /**
     * 发送任务日志消息
     * @param taskId 任务id
     * @param msg    操作信息
     * @return
     */
    void sendTaskLog(Long taskId, String websiteName, String msg);

    /**
     * 发送任务日志消息
     * @param taskId 任务id
     * @param msg    操作信息
     * @return
     */
    void sendTaskLog(Long taskId, String msg);

    /**
     * 发送任务日志消息
     * @param taskId    任务id
     * @param errorCode 错误信息
     */
    void sendTaskLog(Long taskId, String msg, ErrorCode errorCode, String errorDetail);

    /**
     * 发送任务日志消息
     * @param taskId    任务id
     * @param errorCode 错误信息
     */
    void sendTaskLog(Long taskId, String msg, ErrorCode errorCode);

    /**
     * 发送接口耗时
     * @param taskId
     * @param methodName 接口名称
     * @param params     参数
     * @param startTime  开始时间
     * @param finishTime 结束时间
     */
    /**
     * 发送接口耗时
     * @param taskId      任务id
     * @param websiteName 配置名称
     * @param key         关键字
     * @param className   接口名称
     * @param methodName  方法名称
     * @param param       参数
     * @param result      返回结果
     * @param startTime   开始时间
     * @param finishTime  结束时间
     */
    void sendMethodUseTime(Long taskId, String websiteName, String key, String className, String methodName, List<Object> param, Object result,
            long startTime, long finishTime);

}
