package com.datatrees.rawdatacentral.api;

import java.util.Map;

import com.datatrees.rawdatacentral.domain.model.Task;

/**
 * 对外Task服务
 */
public interface CrawlerTaskService {

    /**
     * 根据taskId获取task
     * @param taskId
     * @return
     */
    Task getByTaskId(Long taskId);

    /**
     * 获取任务基本信息
     * @param taskId
     * @return
     */
    Map<String, String> getTaskBaseInfo(Long taskId);

    /**
     * 获取任务基本信息
     * @param taskId
     * @param websiteName
     * @return
     */
    Map<String, String> getTaskBaseInfo(Long taskId, String websiteName);

}
