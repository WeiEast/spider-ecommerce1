package com.datatrees.rawdatacentral.api;

import java.util.List;

import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.QRPluginService;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import com.datatrees.rawdatacentral.domain.vo.Cookie;

/**
 * 通用插件服务
 */
public interface CommonPluginApi extends CommonPluginService, QRPluginService {

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

    /**
     * 发送登陆成功消息
     * @param loginMessage
     */
    void sendLoginSuccessMsg(LoginMessage loginMessage, List<Cookie> cookies);

}
