package com.datatrees.rawdatacentral.api.mail.qq;

import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * qq模拟登陆接口
 * @author zhouxinghai
 * @date 2017/12/29
 */
public interface MailServiceApiForQQ {

    /**
     * 提交登陆请求
     * 必填参数: taskId,username,password
     * <p>
     * async:true表示异步
     * data:内包含json:{"processId":888888,"processStatus":"PROCESSING"}
     * 拿到processId后轮训
     * </p>
     * @return
     */
    HttpResult<Object> login(CommonPluginParam param);
}
