package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/website")
public class WebsiteController {

    private static final Logger  logger = LoggerFactory.getLogger(WebsiteController.class);

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
    public HttpResult<Boolean> uploadPluginJar(MultipartHttpServletRequest multiReq, String token) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            MultipartFile jar = multiReq.getFile("jar");
            String uploadFilePath = jar.getOriginalFilename();
            String uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1,
                uploadFilePath.indexOf('.'));
            String uploadFileSuffix = uploadFilePath.substring(uploadFilePath.indexOf('.') + 1,
                uploadFilePath.length());
            String fileName = uploadFileName + "." + uploadFileSuffix;
            pluginService.savePlugin(fileName, jar.getBytes());
            logger.info("uploadPluginJar success fileName={},token={}", fileName, token);
            return result.success(true);
        } catch (Exception e) {
            logger.error("uploadPluginJar error token={}", token);
            return result.failure();
        }
    }

    @RequestMapping(value = "testUploadFiles", method = RequestMethod.POST)
    public void handleFileUpload(MultipartHttpServletRequest request) {
        List<MultipartFile> files = request.getFiles("jars");
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String uploadFilePath = file.getOriginalFilename();
                    System.out.println("uploadFlePath:" + uploadFilePath);
                    // 截取上传文件的文件名
                    String uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1,
                        uploadFilePath.indexOf('.'));
                    System.out.println("multiReq.getFile()" + uploadFileName);
                    // 截取上传文件的后缀
                    String uploadFileSuffix = uploadFilePath.substring(uploadFilePath.indexOf('.') + 1,
                        uploadFilePath.length());
                    System.out.println("uploadFileSuffix:" + uploadFileSuffix);
                    String outPath = "/data/upload/" + uploadFileName + "." + uploadFileSuffix;
                    FileUtils.writeByteArrayToFile(new File(outPath), file.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            } else {
                System.out.println("上传文件为空");
            }
        }
        System.out.println("文件接受成功了");
    }

}
