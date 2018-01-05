package com.datatrees.rawdatacentral.api;

import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;

/**
 * 通用插件服务
 */
public interface CommonPluginApi extends CommonPluginService {

    /**
     * 查询处理结果
     * @param processId 处理号
     * @return
     */
    ProcessResult queryProcessResult(long processId);

    /**
     * 发送登陆成功消息
     * @param loginMessage
     */
    void sendLoginSuccessMsg(LoginMessage loginMessage);

}
