/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.operator.plugin.zhe_jiang_10000_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.datatrees.spider.operator.plugin.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.plugin.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.plugin.OperatorLoginPostPlugin;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/19.
 */
public class ZheJiang10000ForWeb implements OperatorLoginPostPlugin {

    private static final Logger                     logger     = LoggerFactory.getLogger(ZheJiang10000ForWeb.class);

    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCode(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://zj.189.cn/zjpr/service/query/query_order.html?menuFlag=1";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            String referer = templateUrl;
            templateUrl = "http://zj.189.cn/bfapp/buffalo/cdrService";
            String data = "<buffalo-call><method>querycdrasset</method></buffalo-call>";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).setReferer(referer).invoke();

            templateUrl = "http://zj.189.cn/bfapp/buffalo/VCodeOperation";
            String templateData = "<buffalo-call><method>SendVCodeByNbr</method><string>{}</string></buffalo-call>";
            data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "成功")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String productid = TaskUtils.getTaskContext(param.getTaskId(), "productid");
            String areaid = TaskUtils.getTaskContext(param.getTaskId(), "areaid");
            //String cdrlevel = TaskUtils.getTaskContext(param.getTaskId(), "cdrlevel");
            String servtype = TaskUtils.getTaskContext(param.getTaskId(), "servtype");
            String username = TaskUtils.getTaskContext(param.getTaskId(), "Name");
            String idCard = TaskUtils.getTaskContext(param.getTaskId(), "IdentityCard");
            if (StringUtils.isBlank(username) || StringUtils.isBlank(idCard) || StringUtils.contains(idCard, "*")) {
                logger.error("详单-->校验失败,姓名或身份证号不完整,param={},username={},idCard={}", param, username, idCard);
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
            username = URLEncoder.encode(username, "gb2312");
            TaskUtils.addTaskShare(param.getTaskId(), "realname", username);
            TaskUtils.addTaskShare(param.getTaskId(), "idCard", idCard);

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String billMonth = sf.format(c.getTime());

            String referer = "http://zj.189.cn/zjpr/service/query/query_order.html?menuFlag=1";
            String templateUrl = "http://zj.189.cn/zjpr/cdr/getCdrDetail.htm";
            String templateData =
                    "flag=1&cdrCondition.pagenum=1&cdrCondition.pagesize=100&cdrCondition.productnbr={}&cdrCondition.areaid={}&cdrCondition" +
                            ".cdrlevel=&cdrCondition.productid={}&cdrCondition.product_servtype={}&cdrCondition" +
                            ".recievenbr=%D2%C6%B6%AF%B5%E7%BB%B0&cdrCondition.cdrmonth={}&cdrCondition.cdrtype=11&cdrCondition.usernameyanzheng={}&cdrCondition.idyanzheng={}&cdrCondition" +
                            ".randpsw={}";
            String data = TemplateUtils
                    .format(templateData, param.getMobile(), areaid, productid, servtype, billMonth, username, idCard, param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).setSocketTimeout(30000).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "清单详情") || StringUtils.contains(pageContent, "ErrorNo=61010")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10012&toStUrl=http://zj.189.cn/zjpr/balancep/getBalancep.htm";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
