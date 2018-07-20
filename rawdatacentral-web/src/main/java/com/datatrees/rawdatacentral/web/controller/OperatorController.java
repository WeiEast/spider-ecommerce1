package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/operator")
public class OperatorController {

    @Resource
    private RedisService redisService;

    @RequestMapping("/mappingPluginFile")
    public Object mappingPluginFile(String websiteName, String fileName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        CheckUtils.checkNotBlank(fileName, "fileName is empty");
        redisService.saveString(RedisKeyPrefixEnum.WEBSITE_PLUGIN_FILE_NAME, websiteName, fileName);
        return new HttpResult<>().success();
    }

}
