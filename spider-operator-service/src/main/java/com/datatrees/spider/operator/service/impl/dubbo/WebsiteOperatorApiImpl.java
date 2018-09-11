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

package com.datatrees.spider.operator.service.impl.dubbo;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.spider.operator.api.WebsiteOperatorApi;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import com.datatrees.spider.operator.service.impl.WebsiteOperatorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteOperatorApiImpl implements WebsiteOperatorApi {

    private static final Logger                 logger = LoggerFactory.getLogger(WebsiteOperatorServiceImpl.class);

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    @Override
    public void updateEnable(String websiteName, Boolean enable) {
        websiteOperatorService.updateEnable(websiteName, enable);
        logger.info("updateEnable success wesiteName={},enable={}", websiteName, enable);
    }

    @Override
    public List<WebsiteOperator> queryDisable() {
        return websiteOperatorService.queryDisable();
    }

    @Override
    public WebsiteOperator getByWebsiteName(String websiteName) {
        return websiteOperatorService.getByWebsiteName(websiteName);
    }

    @Override
    public List<WebsiteOperator> queryAll() {
        return websiteOperatorService.queryAll();
    }

    @Override
    public List<WebsiteGroup> updateWebsiteStatus(String groupCode, String websiteName, Boolean enable, Boolean auto) {
        return websiteOperatorService.updateWebsiteStatus(groupCode, websiteName, enable, auto);
    }

}
