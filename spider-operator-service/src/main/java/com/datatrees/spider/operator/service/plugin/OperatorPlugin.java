package com.datatrees.spider.operator.service.plugin;

import java.util.Map;

import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 运营商登陆接口
 * Created by zhouxinghai on 2017/7/13.
 */
public interface OperatorPlugin {

    /**
     * 登陆初始化,获取基本信息
     * 这个很重要,开始或者重新开始task都要调用这个接口,否则用户更换手机号有风险
     * @param param 必填参数:taskId,websiteName,mobile,formType
     * @return
     */
    HttpResult<Map<String, Object>> init(OperatorParam param);

    /**
     * 刷新图片验证码
     * 依赖init
     * 必填参数:taskId,formType
     * @param param
     * @return
     */
    HttpResult<String> refeshPicCode(OperatorParam param);

    /**
     * 刷新短信验证码
     * 依赖init
     * 必填参数:taskId,formType
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param);

    /**
     * 刷新短信验证码
     * 依赖init
     * 必填参数:taskId,formType
     * @return
     */
    HttpResult<Map<String, Object>> submit(OperatorParam param);

    /**
     * 验证图片验证码
     * 依赖init
     * 必填参数:taskId,formType,pidCode
     * @return
     */
    HttpResult<Map<String, Object>> validatePicCode(OperatorParam param);

    /**
     * 自定义方法入口
     * 必填参数:taskId,formType,args
     * @return
     */
    HttpResult<Object> defineProcess(OperatorParam param);

}
