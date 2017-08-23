package com.datatrees.crawler.plugin.qrcode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.crawler.plugin.AbstractRawdataPlugin;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQRCodePlugin extends AbstractRawdataPlugin implements QRCodeVerification {

    int verifyQRCodeCount = 0;
    private Logger logger = LoggerFactory.getLogger(AbstractQRCodePlugin.class);

    @Override
    public String process(String... args) throws Exception {
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils.fromJson(args[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        return GsonUtils.toJson(doProcess(paramMap));
    }

    public Map<String, String> doProcess(Map<String, String> paramMap) throws Exception {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        logger.info("start run qrcode plugin!taskId={},websiteName={}", taskId, websiteName);
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        Map paramsMap = perpareParam(paramMap);

        Map<String, Object> redisMap = new HashMap<String, Object>();
        if (null == taskId || StringUtils.isBlank(websiteName)) {
            logger.error("invalid params taskId or websitename is empty! taskId={},websiteName={}", taskId, websiteName);
            resultMap.put(AttributeKey.ERROR_CODE, "-1");
            resultMap.put(AttributeKey.ERROR_MESSAGE, "userId or websitename is empty");
            return resultMap;
        }
        if (!this.preQRCode(paramsMap)) {
            logger.warn("no need to do qr plugin .taskId={},websiteName={}", taskId, websiteName);
            resultMap.putAll(paramMap);
            return resultMap;
        }
        //二维码不需要刷新,暂时不需要重试
        //最大重试次数
        int maxRetry = 0;
        //当前重试次数
        int retry = 0;
        getMessageService().sendTaskLog(taskId, "等待用户扫描二维码");
        //保存交互指令到redis,让APP端轮询进入等待模式
        DirectiveResult<String> sendDirective = new DirectiveResult<>(DirectiveType.CRAWL_QR, taskId);
        sendDirective.fill(DirectiveRedisCode.WAITTING, null);
        String directiveId = null;
        do {
            String qrcodeResult = refreshQRCode(paramsMap);
            logger.info("do refresh qrcode  retry={},taskId={},websiteName={}", retry, taskId, websiteName);
            if (StringUtils.isEmpty(qrcodeResult)) {
                logger.error("request qrCode error!retry={},taskId={},websiteName={}", retry, taskId, websiteName);
                continue;
            }
            directiveId = getMessageService().sendDirective(taskId, DirectiveEnum.REQUIRE_QR.getCode(), qrcodeResult);
            getRedisService().saveDirectiveResult(directiveId, sendDirective);

            long statTime = System.currentTimeMillis();

            while (!isTimeOut(statTime, websiteName)) {
                TimeUnit.MILLISECONDS.sleep(500);
                QRCodeResult verifyResult = verifyQRCodeStatus(paramsMap);
                if (null == verifyResult) {
                    continue;
                }
                logger.info("taskId={},directiveId={},websiteName={},verifyResult={}", taskId, directiveId, websiteName, verifyResult.status);
                if (QRCodeStatus.SCANNED == verifyResult.status) {
                    //已扫描,待确认
                    sendDirective.fill(DirectiveRedisCode.SCANNED, null);
                    getRedisService().saveDirectiveResult(directiveId, sendDirective);
                    logger.info("verifyQRCodeStatus taskId={},directiveId={},websiteName={},status = {}", taskId, directiveId, websiteName, DirectiveRedisCode.SCANNED);
                }
                if (QRCodeStatus.CONFIRMED == verifyResult.status) {
                    QRCodeResult confirmResult = confirmQRCodeStatus(paramsMap);
                    if (null != confirmResult) {
                        logger.info("confirmQRCodeStatus taskId={},directiveId={},websiteName={},confirmResult = {}", taskId, directiveId, websiteName, confirmResult.status);
                        if (QRCodeStatus.SUCCESS == confirmResult.status) {
                            resultMap.put(PluginConstants.FIELD, this.postQRCode(confirmResult.result));
                            sendDirective.fill(DirectiveRedisCode.SUCCESS, null);
                            getRedisService().saveDirectiveResult(directiveId, sendDirective);
                            getMessageService().sendTaskLog(taskId, "二维码验证成功");
                            return resultMap;
                        }
                    }
                }
            }
        } while (retry++ < maxRetry);
        getMessageService().sendTaskLog(taskId, "二维码验证失败");

        if (StringUtils.isNoneBlank(directiveId)) {
            sendDirective.fill(DirectiveRedisCode.SKIP, null);
            getRedisService().saveDirectiveResult(directiveId, sendDirective);
        }

        redisMap.put(PluginConstants.FIELD, DirectiveRedisCode.SKIP);
        logger.info("qrcode valid fail, taskId={},directiveId={},websiteName={},status={}", taskId, directiveId, websiteName, DirectiveRedisCode.SKIP);
        return resultMap;
    }

    public Map<String, String> perpareParam(Map<String, String> parms) {
        logger.debug("default perpareParam directly return!");
        return parms;
    }

    public boolean preQRCode(Map<String, String> parms) {
        return true;
    }

    public String postQRCode(Map<String, Object> parms) {
        return "CONFIRMED";
    }

    protected void setSendMessageTips(Map<String, String> parms, String title, String tips) {
        if (parms != null) {
            parms.put("tips", tips);
            parms.put("title", title);
        }
    }

    public boolean isQRCodeNotEmpty(Map<String, String> parms) {
        return true;
    }

    protected int getMaxInterval(String websiteName) {
        return PropertiesConfiguration.getInstance().getInt(websiteName + ".qrCode.max.waittime", 2 * 60 * 1000);
    }

}
