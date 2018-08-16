/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.share.web.controller;

import javax.annotation.Resource;

import com.datatrees.spider.share.service.PluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * plugin上传接口
 * @author zhouxinghai
 * @date 2017/12/5
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    private static final Logger        logger = LoggerFactory.getLogger("plugin_log");

    @Resource
    private              PluginService pluginService;

    /**
     * 上传java插件
     * @param multiReq
     * @param fileName 文件名称
     * @param version  文件版本,默认当前时间戳
     * @param sassEnv  系统环境,可以指定
     * @return
     */
    @RequestMapping(value = "/uploadPlugin", method = RequestMethod.POST)
    public Object uploadPlugin(MultipartHttpServletRequest multiReq, String fileName, String version, String sassEnv) {
        StringBuilder result = new StringBuilder();
        try {
            MultipartFile jar = multiReq.getFile("file");
            String uploadFilePath = jar.getOriginalFilename();
            String uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
            String uploadFileSuffix = uploadFilePath.substring(uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
            if (StringUtils.isBlank(fileName)) {
                fileName = uploadFileName + "." + uploadFileSuffix;
            }
            version = pluginService.savePlugin(sassEnv, fileName, jar.getBytes(), version);
            logger.info("uploadPlugin success fileName={},version={},sassEnv={}", fileName, version, sassEnv);
            return result.append("uuploadJavaPlugin success:").append(fileName).append(", version:").append(version).append("\n").toString();
        } catch (Exception e) {
            logger.error("uploadPlugin error fileName={},version={},sassEnv={}", fileName, version, sassEnv, e);
            return "上传失败";
        }
    }

}
