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

package com.datatrees.spider.share.api;

import java.util.Map;

import com.datatrees.spider.share.domain.ProcessResult;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.model.Task;

/**
 * 对外Task服务
 */
public interface SpiderTaskApi {

    /**
     * 根据taskId获取task
     * @param taskId
     * @return
     */
    Task getByTaskId(Long taskId);

    /**
     * 获取任务基本信息
     * @param taskId
     * @return
     */
    Map<String, String> getTaskBaseInfo(Long taskId);

    /**
     * 获取任务基本信息
     * @param taskId
     * @param websiteName
     * @return
     */
    Map<String, String> getTaskBaseInfo(Long taskId, String websiteName);

    /**
     * 获取任务的 AccountNo
     * @param taskId
     * @return
     */
    String getTaskAccountNo(Long taskId);

    /**
     * 抓取过程中导入图片验证码和短信验证码,如果后端校验失败会重新发出指令附带图片验证码信息
     * 例如:运营商通话记录获取
     * 目前只有短信验证码在用(运营商)
     * @param directiveId 指令ID
     * @param taskId      网关任务id
     * @param type        0:短信验证码 1:图片验证码
     * @param code        验证码(图片或者短信)
     * @param extra       附加信息,目前null
     * @return
     */
    HttpResult<Boolean> importCrawlCode(String directiveId, long taskId, int type, String code, Map<String, String> extra);

    /**
     * 取消任务
     * @param taskId 网关任务id
     * @param extra  附加信息,目前null
     * @return
     */
    HttpResult<Boolean> cancel(long taskId, Map<String, String> extra);


    /**
     * 查询处理结果
     * @param processId 处理号
     * @return
     */
    ProcessResult queryProcessResult(long processId);
}
