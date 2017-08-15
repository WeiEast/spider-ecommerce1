package com.datatrees.rawdatacentral.core.common;

import java.util.Date;

import com.datatrees.rawdatacentral.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午6:05:50
 */
public enum UnifiedSysTime {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedSysTime.class);
    private static TaskService service = (TaskService) BeansFactory.getService("taskServiceImpl", TaskService.class);

    private static final long JET_LAG;

    static {
        LOGGER.info("System date: {} - {}", new Date(),  System.currentTimeMillis());
        JET_LAG = service.selectNow().getTime() - System.currentTimeMillis();
    }


    public Date getSystemTime() {
        return new Date(System.currentTimeMillis() + JET_LAG);
    }

}
