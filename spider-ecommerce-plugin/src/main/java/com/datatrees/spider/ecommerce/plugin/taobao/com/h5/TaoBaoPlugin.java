/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5;

import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRLoginMonitor;
import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRLoginOperation;
import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRLoginOperation.LoginResult;
import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRStatusManager;
import com.datatrees.spider.ecommerce.plugin.util.QRUtils;
import com.datatrees.spider.share.common.http.ProxyUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.QRStatus;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.service.plugin.AbstractQRPlugin;
import com.treefinance.toolkit.util.Base64Codec;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author guimeichao
 * @date 18/4/8.
 */
public class TaoBaoPlugin extends AbstractQRPlugin {

    private static final String IS_RUNNING = "economic_qr_is_runing_";

    private static final String QRCODE_GEN_TIME_KEY = "com.treefinance.spider.ecommerce.h5_login.qrcode.gen_time:";
    /**
     * qrcode 过期时间，单位：秒
     */
    private static final int QRCODE_EXPIRATION = 240;

    private QRLoginOperation operation = new QRLoginOperation();

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        // TODO:埋点-刷新验证码 2019-02-14 李梁杰
        // 清理二维码状态
        QRStatusManager.clear(param.getTaskId());

        HttpResult<Object> result = new HttpResult<>();

        try {
            // 设置请求使用代理, 默认区域：浙江杭州
            ProxyUtils.setProxyLocation(param.getTaskId(), "浙江", "杭州");

            if (!operation.startLoginPage(param)) {
                return result.success("刷新二维码失败");
            }

            Map<String, String> dataMap = queryQRCode(param);

            logger.info("刷新二维码成功，taskId={}", param.getTaskId());

            QRLoginMonitor.notifyLogger(param, "刷新二维码成功", "刷新二维码-->成功");

            return result.success(dataMap);
        } catch (Exception e) {
            logger.error("刷新二维码失败，param={}", param, e);

            QRLoginMonitor.notifyLogger(param, "刷新二维码失败", "刷新二维码-->失败", ErrorCode.REFESH_QR_CODE_ERROR, "二维码刷新失败,请重试");

            return result.success("刷新二维码失败");
        }
    }

    private Map<String, String> queryQRCode(CommonPluginParam param) throws Exception {
        Map<String, String> dataMap = getQRCode(param);

        RedisUtils.set(QRCODE_GEN_TIME_KEY + param.getTaskId(), String.valueOf(System.currentTimeMillis()), QRCODE_EXPIRATION);

        String isRunning = RedisUtils.get(IS_RUNNING + param.getTaskId());
        if (!Boolean.TRUE.toString().equals(isRunning)) {
            new Thread(new QRCodeStatusQuery(param)).start();
        }

        return dataMap;
    }

    private Map<String, String> getQRCode(CommonPluginParam param) throws Exception {
        Exception ex = null;
        for (int i = 0; i < 3; i++) {
            try {
                byte[] qrCodeImage = operation.getQrCodeImage(param);

                QRUtils qrUtils = new QRUtils();
                String qrText = qrUtils.parseCode(qrCodeImage);
                if (StringUtils.isEmpty(qrText)) {
                    continue;
                }

                Map<String, String> dataMap = new HashMap<>(2);
                dataMap.put("qrBase64", Base64Codec.encode(qrCodeImage));
                dataMap.put("qrText", qrText);

                return dataMap;
            } catch (Exception e) {
                logger.warn("获取二维码失败异常！", e);
                ex = e;
            }
        }

        throw new Exception("获取二维码失败.", ex);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        String status = QRStatusManager.getStatus(param.getTaskId());
        if (QRStatus.REQUIRE_SMS.equals(status)) {
            String directiveId = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.DIRECTIVE_ID);
            Map<String, Object> extra = new HashMap<>(1);
            extra.put("directiveId", directiveId);
            result.setExtra(extra);
        }
        return result.success(status);
    }

    private class QRCodeStatusQuery implements Runnable {

        private final CommonPluginParam param;

        private final String cacheKey;

        private String lastFailure;

        QRCodeStatusQuery(CommonPluginParam param) {
            this.param = param;
            this.cacheKey = QRCODE_GEN_TIME_KEY + param.getTaskId();
        }

        @Override
        public void run() {
            RedisUtils.set(IS_RUNNING + param.getTaskId(), Boolean.TRUE.toString());
            try {
                while (true) {
                    try {
                        String time = RedisUtils.get(cacheKey);
                        if (StringUtils.isEmpty(time)) {
                            QRStatusManager.setStatus(param.getTaskId(), QRStatus.EXPIRE);
                            QRLoginMonitor.notifyLogger(param, "登录超时", "校验-->超时", ErrorCode.LOGIN_TIMEOUT_ERROR, "登陆超时,请重试");
                            break;
                        }

                        String lgToken = TaskUtils.getTaskShare(param.getTaskId(), "lgToken");
                        if (lgToken != null && !lgToken.equals(lastFailure)) {
                            lastFailure = null;
                            String status = operation.queryQRCodeStatus(param, lgToken);

                            logger.info("状态更新成功,当前二维码状态：{},taskId={}", status, param.getTaskId());
                            if (QRStatus.CONFIRMED.equals(status)) {
                                // TODO:埋点-二维码已确认 2019-02-14 李梁杰
                                QRStatusManager.setStatus(param.getTaskId(), QRStatus.CONFIRMED);
                                triggerAfterConfirmed(param);
                                break;
                            }

                            if (QRStatusManager.recordLastStatus(param.getTaskId(), lgToken, status) == 1) {
                                if (QRStatus.SCANNED.equals(status)) {
                                    // TODO:埋点-二维码已扫描 2019-02-14 李梁杰
                                    QRLoginMonitor.notifyLogger(param, "二维码已扫描", "扫描二维码-->已扫描");
                                } else if (QRStatus.EXPIRE.equals(status)) {
                                    // TODO:埋点-二维码已过期 2019-02-14 李梁杰
                                    QRLoginMonitor.notifyLogger(param, "二维码已过期", "扫描二维码-->已过期");
                                    RedisUtils.expire(cacheKey, 30);
                                    lastFailure = lgToken;
                                } else if (QRStatus.FAILED.equals(status)) {
                                    // TODO:埋点-二维码未扫描 2019-02-14 李梁杰
                                    QRLoginMonitor.notifyLogger(param, "二维码扫描失败", "扫描二维码-->失败");
                                    RedisUtils.expire(cacheKey, 30);
                                    lastFailure = lgToken;
                                }
                            }
                            QRStatusManager.setStatus(param.getTaskId(), status);
                        }

                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.error("QRCode status querying process was interrupted!", e);
                        break;
                    } catch (Exception e) {
                        logger.warn("Something was wrong when querying QRCode status!", e);
                    }
                }
            } finally {
                RedisUtils.del(IS_RUNNING + param.getTaskId());
            }
        }

        private void triggerAfterConfirmed(CommonPluginParam param) {
            logger.info("用户已确认二维码，开始登录验证，taskId={}, website={}", param.getTaskId(), param.getWebsiteName());
            LoginResult result = null;
            try {
                result = operation.startLogin(param);

                String referer = result.getRedirectUrl();
                logger.info("taskId={}, referer: {}", param.getTaskId(), referer);
                if (referer != null) {
                    if (referer.contains("/login/trust_login.do")) {
                        operation.doTrustLogin(param, result);

                        triggerAfterLogin(param, result.getAccountNo());
                        return;
                    } else if (referer.contains("/member/login_unusual.htm")) {
                        logger.info("记录一下关键页面,taskId={},url={},page={}", param.getTaskId(), referer, result.getPageContent());
                        // TODO:埋点-触发短信验证 2019-02-14 李梁杰
                        operation.doUnusualLogin(param, result);

                        triggerAfterLogin(param, result.getAccountNo());
                        return;
                    } else if (referer.contains(".alipay.com/portal/i.htm")) {
                        triggerAfterLogin(param, result.getAccountNo());
                        return;
                    }
                } else if (result.getPageContent().contains(QRLoginOperation.ALIPAY_MAIN_PAGE_TITLE)) {
                    triggerAfterLogin(param, result.getAccountNo());
                    return;
                }
                throw new IllegalStateException("Unexpected response!  login_result: " + result);
            } catch (Exception e) {
                logger.error("淘宝二维码登录验证失败，taskId={}, login_result: {}", param.getTaskId(), result, e);
                QRStatusManager.setStatus(param.getTaskId(), QRStatus.FAILED);
                QRLoginMonitor.notifyLogger(param, "登录失败", "校验-->失败", ErrorCode.LOGIN_FAIL, "登陆失败,请重试");
            }
        }

        private void triggerAfterLogin(CommonPluginParam param, String accountNo) {
            QRStatusManager.setStatus(param.getTaskId(), QRStatus.SUCCESS);
            logger.info("用户完成登录验证，发送登录成功消息，taskId={}, website={}", param.getTaskId(), param.getWebsiteName());
            QRLoginMonitor.notifyLogger(param, "二维码确认成功", "校验-->成功");

            // TODO:埋点-扫码登录成功 2019-02-15 李梁杰

            String cookieString = TaskUtils.getCookieString(param.getTaskId());
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setTaskId(param.getTaskId());
            loginMessage.setWebsiteName(param.getWebsiteName());
            loginMessage.setCookie(cookieString);
            loginMessage.setAccountNo(StringUtils.defaultString(accountNo));
            TaskUtils.addTaskShare(param.getTaskId(), "username", accountNo);

            BeanFactoryUtils.getBean(CommonPluginService.class).sendLoginSuccessMsg(TopicEnum.SPIDER_ECOMMERCE.getCode(), loginMessage);
        }
    }

}
