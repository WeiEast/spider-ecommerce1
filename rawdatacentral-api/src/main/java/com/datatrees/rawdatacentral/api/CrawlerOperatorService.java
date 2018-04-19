package com.datatrees.rawdatacentral.api;

import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorLoginConfig;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * 运营商接口
 * Created by zhouxinghai on 2017/7/17.
 */
public interface CrawlerOperatorService {

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
    HttpResult<Map<String, Object>> refeshPicCode(OperatorParam param);

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
     * 获取所有运营商登陆配置
     * 所有的可用的
     * 每个运营商这里指定了,不可以缓存
     * @return
     */
    HttpResult<List<OperatorCatalogue>> queryAllConfig();

    /**
     * 自定义处理
     * 必填参数:taskId,formType,args
     * @return
     */
    HttpResult<Object> defineProcess(OperatorParam param);

    /**
     * 校验基本参数
     * taskId,websiteName,mobile
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> checkParams(OperatorParam param);

    /**
     * 获取运营商登陆配置
     * 运营商登陆准备
     * 必填参数:taskId,mobile,groupCode
     * @return
     */
    HttpResult<OperatorLoginConfig> preLogin(OperatorParam param);

    /**
     * 查询运营商分组信息
     * @return
     */
    HttpResult<Map<String, Map<String, String>>> queryGroups();

}
