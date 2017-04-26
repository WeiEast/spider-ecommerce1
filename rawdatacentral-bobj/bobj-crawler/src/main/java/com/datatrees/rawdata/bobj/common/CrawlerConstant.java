package com.datatrees.rawdatacentral.bobj.common;

import com.datatrees.common.conf.PropertiesConfiguration;

public interface CrawlerConstant {
    int CRAWLER_CORE_THREAD_NUM = PropertiesConfiguration.getInstance().getInt("submitter.upload.corePoolSize", 30);
    int CRAWLER_MAX_THREAD_NUM = PropertiesConfiguration.getInstance().getInt("submitter.upload.maximumPoolSize", 500);
    int CRAWLER_MAX_TASK_NUM = PropertiesConfiguration.getInstance().getInt("submitter.upload.maximumTaskNum", 0);

}
