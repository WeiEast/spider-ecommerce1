package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.Map;

/**
 * 运营商登陆接口
 * Created by zhouxinghai on 2017/7/13.
 */
public interface OperatorPluginService {

    String RETURN_FIELD_PIC_CODE = "picCode";//返回图片验证码

    /**
     * 登陆初始化,获取基本信息
     * @param taskId
     * @param websiteName
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> init(Long taskId, String websiteName, OperatorParam param);

    /**
     * 刷新图片验证码
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,VALIDATE_CALL_LOGS:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> refeshPicCode(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 刷新短信验证码
     * 必填:手机号
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,VALIDATE_CALL_LOGS:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> refeshSmsCode(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 刷新短信验证码
     * 必填:手机号
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,VALIDATE_CALL_LOGS:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> submit(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 验证图片验证码
     * 必填:picCode
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,VALIDATE_CALL_LOGS:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> validatePicCode(Long taskId, String websiteName, String type, OperatorParam param);

    //    /**
    //     * 验证图片验证码
    //     * 必填:picCode
    //     * @param taskId
    //     * @param websiteName
    //     * @param param
    //     * @return
    //     */
    //    HttpResult<Map<String, Object>> validateMobile(Long taskId, String websiteName, OperatorParam param);
}
