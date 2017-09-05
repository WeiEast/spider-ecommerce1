package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.CrawlerTaskService;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CrawlerTaskServiceImpl implements CrawlerTaskService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerTaskServiceImpl.class);
    @Resource
    private TaskService taskService;

    @Override
    public Task getByTaskId(Long taskId) {
        return taskService.getByTaskId(taskId);
    }
}
