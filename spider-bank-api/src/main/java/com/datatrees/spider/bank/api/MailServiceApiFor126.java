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

package com.datatrees.spider.bank.api;

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 126邮箱模拟登陆接口
 * User: yand
 * Date: 2018/2/27
 */
public interface MailServiceApiFor126 {

    /**
     * 提交登陆请求
     * 必填参数: taskId,username,password
     * <p>
     * 结果异步获取
     * 详见:@see com.datatrees.spider.spider.share.domain.ProcessResult
     * </p>
     * @return
     */
    HttpResult<Object> login(CommonPluginParam param);

    /**
     * 刷新登陆二维码
     * 必填参数: taskId
     * <p>
     * 结果异步获取
     * 详见:@see com.datatrees.spider.spider.share.domain.ProcessResult
     * </p>
     * @param param
     * @return
     */
    HttpResult<Object> refeshQRCode(CommonPluginParam param);

    /**
     * 查询二维码登陆状态
     * 必填参数: taskId
     * <p>
     * 二维码状态详见:@see com.datatrees.spider.spider.share.domain.QRStatus
     * </p>
     * @param param
     * @return
     */
    HttpResult<Object> queryQRStatus(CommonPluginParam param);

}
