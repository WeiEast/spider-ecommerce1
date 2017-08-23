package com.datatrees.rawdatacentral.core.common;

import java.util.Date;

import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午6:05:50
 */
public enum UnifiedSysTime {
    INSTANCE;
    private static final Logger      logger  = LoggerFactory.getLogger(UnifiedSysTime.class);
    private static final long JET_LAG;
    private static       TaskService service = (TaskService) BeansFactory.getService("taskServiceImpl", TaskService.class);

    static {
        Date dbTime = service.selectNow();
        Date sysTime = new Date();
        JET_LAG = dbTime.getTime() - sysTime.getTime();
        logger.info("dbTime={},sysTime={},JET_LAG={}", DateUtils.formatYmdhms(dbTime), DateUtils.formatYmdhms(sysTime), JET_LAG);
    }

    public Date getSystemTime() {
        return new Date(System.currentTimeMillis() + JET_LAG);
    }

}
