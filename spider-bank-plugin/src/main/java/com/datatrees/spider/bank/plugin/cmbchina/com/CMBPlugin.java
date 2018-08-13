package com.datatrees.spider.bank.plugin.cmbchina.com;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.spider.bank.plugin.util.PdfUtils;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.service.plugin.CommonPlugin;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 2018/8/13.
 */
public class CMBPlugin implements CommonPlugin {

    private static final Logger logger = LoggerFactory.getLogger(CMBPlugin.class);

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        switch (param.getFormType()) {
            case "PDF_DETAILS":
                return processForPDF(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Object> processForPDF(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();

        if (logger.isDebugEnabled()) logger.debug("开始解析pdf!taskId={}", param.getTaskId());
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        if (MapUtils.isEmpty(paramMap) || paramMap.containsKey(PluginConstants.FILE_CONTENT) || !paramMap.containsKey(PluginConstants.FILE_NAME) ||
                !paramMap.get(PluginConstants.FILE_NAME).endsWith(".pdf")) {
            logger.warn("没有发现PDF文件!paramMap: {},taskId={}", paramMap, param.getTaskId());
            return result.success(StringUtils.EMPTY);
        }
        String filePath = paramMap.get(PluginConstants.FILE_WAPPER_PATH);
        if (StringUtils.isEmpty(filePath)) {
            logger.warn("PDF文件路径为空!paramMap: {},taskId={}", paramMap, param.getTaskId());
            return result.success(StringUtils.EMPTY);
        }
        String htmlContent = null;
        try {
            htmlContent = PdfUtils.pdfToHtml(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (logger.isDebugEnabled()) logger.debug("解析pdf结束! htmlContent: {},taskId={}", htmlContent, param.getTaskId());
        return result.success(htmlContent);
    }
}
