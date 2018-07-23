package com.datatrees.rawdatacentral.api.mail.exmail_qq;

import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.spider.share.domain.HttpResult;

/**
 * 腾讯企业邮箱登录接口
 * Created by zhangyanjia on 2018/2/26.
 */
public interface MailServiceApiForExMailQQ {

    /**
     * 登录初始化接口
     * 必填参数：taskId
     * 返回结果
     * 详见:@see com.datatrees.spider.share.domain.HttpResult
     * @return 成功时返回值中status为true，data中返回初始化成功；异常时返回值中status为false，data返回初始化失败
     */
    HttpResult<Object> init(CommonPluginParam param);

    /**
     * 登录提交接口
     * 必填参数：taskId，username，password
     * 选填参数：picCode
     * 返回结果
     * 详见:@see com.datatrees.spider.share.domain.HttpResult
     * @return 异常时返回值status为false，其余status都为true；返回值data中是一个map，根据map对应key为directive进行判断，key为information是返回的信息。
     * directive包含：login_fail--登录失败   login_success--登录成功
     * require_picture--登录时需要图片验证码，information返回图片验证码，前端需弹出此验证码
     * require_picture_again--用户输入验证码登录，验证码不正确时返回的指令，此时information再次返回一张验证码，
     * 这时map中多一个key：errorMessage--提示的错误信息
     */
    HttpResult<Object> login(CommonPluginParam param);
}
