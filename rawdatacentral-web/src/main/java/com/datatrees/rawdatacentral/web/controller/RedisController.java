package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 上传jar等操作
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    private static final Logger logger = LoggerFactory.getLogger(RedisController.class);

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Object uploadPluginJar(MultipartHttpServletRequest multiReq, String file_name, String token) {
        StringBuilder result = new StringBuilder();
        try {
            MultipartFile file = multiReq.getFile("cache_file");
            if (StringUtils.isBlank(file_name)) {
                String uploadFilePath = file.getOriginalFilename();
                String uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
                String uploadFileSuffix = uploadFilePath.substring(uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
                file_name = uploadFileName + "." + uploadFileSuffix;
            }
            RedisUtils.set(RedisKeyPrefixEnum.FILE_DATA.getRedisKey(file_name).getBytes(), file.getBytes());
            RedisUtils.expire(RedisKeyPrefixEnum.FILE_DATA.getRedisKey(file_name), RedisKeyPrefixEnum.FILE_DATA.toSeconds());

            String md5 = DigestUtils.md5Hex(file.getBytes());
            RedisUtils.set(RedisKeyPrefixEnum.FILE_MD5.getRedisKey(file_name), md5);
            RedisUtils.expire(RedisKeyPrefixEnum.FILE_MD5.getRedisKey(file_name), RedisKeyPrefixEnum.FILE_MD5.toSeconds());
            logger.info("uploadFile success fileName={},md5={},token={}", file_name, md5, token);
            return result.append("uploadFile success:").append(file_name).append(", md5:").append(md5).toString();
        } catch (Exception e) {
            logger.error("uploadFile error token={}", token);
            return "上传失败";
        }
    }

    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    public Object deletePluginJar(String file_name, String token) {
        StringBuilder result = new StringBuilder();
        try {
            RedisUtils.del(RedisKeyPrefixEnum.FILE_MD5.getRedisKey(file_name));
            RedisUtils.del(RedisKeyPrefixEnum.FILE_DATA.getRedisKey(file_name));
            logger.info("deleteFile success fileName={},token={}", file_name, token);
            return result.append("deleteFile success:").append(file_name).toString();
        } catch (Exception e) {
            logger.error("deleteFile error token={}", token);
            return "删除失败";
        }
    }

}
