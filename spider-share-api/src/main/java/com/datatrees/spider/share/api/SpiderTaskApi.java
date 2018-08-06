package com.datatrees.spider.share.api;

import java.util.Map;

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

}
