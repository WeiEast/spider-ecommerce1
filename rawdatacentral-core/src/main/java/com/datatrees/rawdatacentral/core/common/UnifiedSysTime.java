package com.datatrees.rawdatacentral.core.common;

import java.util.Date;

import com.datatrees.rawdatacentral.service.TaskService;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午6:05:50
 */
public enum UnifiedSysTime {
    INSTANCE;
    private static TaskService service = (TaskService) BeansFactory.getService("taskServiceImpl", TaskService.class);

    private static final long JET_LAG;

    static {
        JET_LAG = service.selectNow().getTime() - System.currentTimeMillis();
    }


    public Date getSystemTime() {
        return new Date(System.currentTimeMillis() + JET_LAG);
    }

}
