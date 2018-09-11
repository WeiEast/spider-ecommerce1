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

package com.datatrees.spider.operator.service.plugin;

import java.util.Map;

import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 运营商登陆接口
 * Created by zhouxinghai on 2017/7/13.
 */
public interface OperatorPlugin {

    /**
     * 登陆初始化,获取基本信息
     * 这个很重要,开始或者重新开始task都要调用这个接口,否则用户更换手机号有风险
     * @param param 必填参数:taskId,websiteName,mobile,formType
     * @return
     */
    HttpResult<Map<String, Object>> init(OperatorParam param);

    /**
     * 刷新图片验证码
     * 依赖init
     * 必填参数:taskId,formType
     * @param param
     * @return
     */
    HttpResult<String> refeshPicCode(OperatorParam param);

    /**
     * 刷新短信验证码
     * 依赖init
     * 必填参数:taskId,formType
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param);

    /**
     * 刷新短信验证码
     * 依赖init
     * 必填参数:taskId,formType
     * @return
     */
    HttpResult<Map<String, Object>> submit(OperatorParam param);

    /**
     * 验证图片验证码
     * 依赖init
     * 必填参数:taskId,formType,pidCode
     * @return
     */
    HttpResult<Map<String, Object>> validatePicCode(OperatorParam param);

    /**
     * 自定义方法入口
     * 必填参数:taskId,formType,args
     * @return
     */
    HttpResult<Object> defineProcess(OperatorParam param);

}
