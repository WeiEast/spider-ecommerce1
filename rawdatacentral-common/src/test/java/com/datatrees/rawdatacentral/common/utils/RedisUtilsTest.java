package com.datatrees.rawdatacentral.common.utils;

import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class RedisUtilsTest {

    @Test
    public void test() {
        RedisUtils.init("192.168.5.24", 6379, null);
        long taskId = 93399525661634560L;
        Map<String, String> map = null;
        String redisKey = RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
        } else {
            map = RedisUtils.hgetAll(redisKey);
        }
        System.out.println(map);
        //task.share.94450026213830656
        String mobile = TaskUtils.getTaskShare(94450026213830656L, "mobile");
        System.out.println(mobile);

    }

}