package com.datatrees.rawdatacentral.api.mail.qq;

import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * 163邮箱模拟登陆接口
 * @author zhouxinghai
 * @date 2017/12/29
 */
public interface MailServiceApiFor163 {

    /**
     * 提交登陆请求
     * 必填参数: taskId,username,password
     * <p>
     *     结果异步获取
     *     详见:@see com.datatrees.rawdatacentral.domain.result.ProcessResult
     * </p>
     * @return
     */
    HttpResult<Object> login(CommonPluginParam param);

    /**
     * 刷新登陆二维码
     * 必填参数: taskId
     * @param param
     * @return
     */
    HttpResult<Object> refeshQRCode(CommonPluginParam param);

    /**
     * 查询二维码登陆状态
     * 必填参数: taskId,processId
     * <p>
     *     二维码状态详见:@see com.datatrees.rawdatacentral.domain.enums.QRStatus
     * </p>
     * @param param
     * @return
     */
    HttpResult<Object> queryQRStatus(CommonPluginParam param);
}
