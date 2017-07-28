package com.datatrees.rawdatacentral.api;

import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.List;
import java.util.Map;

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
    public HttpResult<List<OperatorCatalogue>> queryAllConfig();

}
