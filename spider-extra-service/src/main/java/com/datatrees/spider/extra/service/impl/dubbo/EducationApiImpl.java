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

package com.datatrees.spider.extra.service.impl.dubbo;

import javax.annotation.Resource;

import com.datatrees.spider.extra.api.EducationApi;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2017/12/1.
 */
@Service
public class EducationApiImpl implements EducationApi {

    private static final Logger              logger = LoggerFactory.getLogger(EducationApiImpl.class);

    @Resource
    private              CommonPluginService commonPluginService;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return commonPluginService.init(param);
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return commonPluginService.refeshPicCode(param);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return commonPluginService.refeshSmsCode(param);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        return commonPluginService.submit(param);
    }

}
