package com.datatrees.rawdatacentral.api;

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

}
