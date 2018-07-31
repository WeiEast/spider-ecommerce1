package com.datatrees.rawdatacentral.api;

import java.util.List;

import com.datatrees.rawdatacentral.api.internal.CommonPlugin;
import com.datatrees.rawdatacentral.api.internal.QRPlugin;
import com.datatrees.rawdatacentral.api.internal.XueXinPluginService;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.ProcessResult;
import com.datatrees.spider.share.domain.http.Cookie;

/**
 * 通用插件服务
 */
public interface CommonPluginApi extends CommonPlugin, QRPlugin, XueXinPluginService {

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
