package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.common.utils.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;

public class CommonTest extends BaseTest{
    
    private static final Logger logger = LoggerFactory.getLogger(CommonTest.class);

    @Resource
    private TaskService taskService;

    @Test
    public void test1(){
        Date dbTime = taskService.selectNow();
        Date sysTime = new Date();
        long JET_LAG = dbTime.getTime() - sysTime.getTime();
        logger.info("标准时间,dbTime={},sysTime={},JET_LAG={}", DateUtils.formatYmdhms(dbTime), DateUtils.formatYmdhms(sysTime),
                JET_LAG);
    }

}
