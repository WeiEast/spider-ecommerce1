package com.datatrees.crawler.plugin.operator;

import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.Map;

/**
 * 运营商登陆接口
 * Created by zhouxinghai on 2017/7/13.
 */
public interface OperatorLoginPlugin {

    /**
     * 刷新图片验证码
     * @param taskId
     * @param websiteName
     * @param param
     * @return
     */
    HttpResult<String> refeshPicCode(Long taskId, String websiteName, OperatorParam param);

    /**
     * 刷新短信验证码
     * @param taskId
     * @param websiteName
     * @param param
     * @return
     */
    HttpResult<Boolean> refeshSmsCode(Long taskId, String websiteName, OperatorParam param);

    /**
     * 刷新短信验证码
     * @param taskId
     * @param websiteName
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> login(Long taskId, String websiteName, OperatorParam param);
}
