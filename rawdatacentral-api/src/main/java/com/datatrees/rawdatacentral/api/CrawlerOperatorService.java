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
     * @param taskId
     * @param websiteName
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> init(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 刷新图片验证码
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,详单VALIDATE_BILL_DETAIL:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> refeshPicCode(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 刷新短信验证码
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,详单VALIDATE_BILL_DETAIL:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> refeshSmsCode(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 刷新短信验证码
     * @param taskId
     * @param websiteName
     * @param type 表单类型,LOGIN:登陆,详单VALIDATE_BILL_DETAIL:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     * @param param
     * @return
     */
    HttpResult<Map<String, Object>> submit(Long taskId, String websiteName, String type, OperatorParam param);

    /**
     * 获取所有运营商登陆配置
     * 所有的可用的
     * 每个运营商这里指定了,不可以缓存
     * @return
     */
    public HttpResult<List<OperatorCatalogue>> queryAllConfig();

}
