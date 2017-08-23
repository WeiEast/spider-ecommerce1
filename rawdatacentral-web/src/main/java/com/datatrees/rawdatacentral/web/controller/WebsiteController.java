package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/website")
public class WebsiteController {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteController.class);
    @Resource
    private WebsiteConfigService websiteConfigService;
    @Resource
    private RedisService         redisService;
    @Resource
    private PluginService        pluginService;

    @RequestMapping("/deleteCacheByWebsiteName")
    public HttpResult<Boolean> deleteCacheByWebsiteName(String websiteName) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            websiteConfigService.deleteCacheByWebsiteName(websiteName);
            logger.info("delete cache success websiteName={}", websiteName);
            return result.success(true);
        } catch (Exception e) {
            logger.error("deleteCacheByWebsiteName error websiteName={}", websiteName, e);
            return result.failure();
        }
    }

    @RequestMapping(value = "/updateWebsiteConf", method = RequestMethod.POST)
    public HttpResult<Boolean> updateWebsiteConf(String websiteName, String searchConfig, String extractConfig) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            websiteConfigService.updateWebsiteConf(websiteName, searchConfig, extractConfig);
            logger.info("updateWebsiteConf success websiteName={}", websiteName);
            return result.success(true);
        } catch (Exception e) {
            logger.error("updateWebsiteConf error websiteName={}", websiteName, e);
            return result.failure();
        }
    }

    @RequestMapping(value = "/uploadPluginJar", method = RequestMethod.POST)
    public Object uploadPluginJar(MultipartHttpServletRequest multiReq, String fileName, String token) {
        StringBuilder result = new StringBuilder();
        try {
            MultipartFile jar = multiReq.getFile("jar");
            String uploadFilePath = jar.getOriginalFilename();
            String uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
            String uploadFileSuffix = uploadFilePath.substring(uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
            if (StringUtils.isBlank(fileName)) {
                fileName = uploadFileName + "." + uploadFileSuffix;
            }
            String beforeMd5 = redisService.getString(RedisKeyPrefixEnum.PLUGIN_FILE_MD5.getRedisKey(fileName));
            String md5 = pluginService.savePlugin(fileName, jar.getBytes());
            boolean change = !StringUtils.equals(md5, beforeMd5);
            logger.info("uploadPluginJar success fileName={},md5={},change={},token={}", fileName, md5, change, token);
            return result.append("upload plugin success:").append(fileName).append(", md5:").append(md5).append(change ? " ,插件已经更新\n" : "\n").toString();
        } catch (Exception e) {
            logger.error("uploadPluginJar error token={}", token);
            return "上传失败";
        }
    }

    @RequestMapping(value = "/deletePluginJar", method = RequestMethod.POST)
    public Object deletePluginJar(MultipartHttpServletRequest multiReq, String token) {
        StringBuilder result = new StringBuilder();
        try {
            MultipartFile jar = multiReq.getFile("jar");
            String uploadFilePath = jar.getOriginalFilename();
            String uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
            String uploadFileSuffix = uploadFilePath.substring(uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
            String fileName = uploadFileName + "." + uploadFileSuffix;
            redisService.deleteKey(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName));
            redisService.deleteKey(RedisKeyPrefixEnum.PLUGIN_FILE_MD5.getRedisKey(fileName));
            logger.info("delete plugin jar success fileName={},token={}", fileName, token);
            return result.append("删除插件:").append(fileName).toString();
        } catch (Exception e) {
            logger.error("deletePluginJar error token={}", token);
            return "删除失败";
        }
    }

}
