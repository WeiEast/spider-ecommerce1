package com.datatrees.rawdatacentral.extractor.actor;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.Extractor;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.SubmitMessage;
import com.datatrees.rawdatacentral.domain.enums.ExtractCode;
import com.datatrees.rawdatacentral.domain.result.AbstractExtractResult;
import com.datatrees.rawdatacentral.extractor.builder.impl.DefaultExtractResultBuilder;
import com.datatrees.rawdatacentral.extractor.builder.impl.DefaultProcessorContextBuilder;
import com.datatrees.rawdatacentral.extractor.storage.ResultStorage;
import com.datatrees.rawdatacentral.submitter.SubmitProcessor;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Created by wuminlang on 15/7/29.
 */
@Service
public class ExtractWorker {

    private static final Logger logger = LoggerFactory.getLogger(ExtractWorker.class);
    @Resource
    private ResultStorage                  resultStorage;
    @Resource
    private SubmitProcessor                submitProcessor;
    @Resource
    private DefaultProcessorContextBuilder contextBuilder;
    @Resource
    private DefaultExtractResultBuilder    resultBuilder;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doSubExtractProcess(ExtractMessage extractMessage, Object obj, int messageIndex) {
        try {
            if (obj != null && obj instanceof Map && MapUtils.isNotEmpty((Map) obj)) {
                Object messageObject = extractMessage.getMessageObject();
                if (messageObject != null && messageObject instanceof Map) {
                    ExtractMessage subExtractMessage = new ExtractMessage();
                    Map map = (Map) BeanUtils.instantiate(messageObject.getClass());
                    map.putAll((Map) messageObject);
                    map.putAll((Map) obj);
                    subExtractMessage.setMessageObject(map);
                    subExtractMessage.setTask(extractMessage.getTask());
                    subExtractMessage.setResultType(extractMessage.getResultType());
                    subExtractMessage.setTaskId(extractMessage.getTaskId());
                    subExtractMessage.setTypeId(extractMessage.getTypeId());
                    subExtractMessage.setWebsiteId(extractMessage.getWebsiteId());
                    subExtractMessage.setMessageIndex(messageIndex);
                    this.process(subExtractMessage);
                    // extractMessage.getSubmitkeyResult().putAll(subExtractMessage.getSubmitkeyResult());
                    extractMessage.addSubExtractMessage(subExtractMessage);
                }
            }
        } catch (Exception e) {
            logger.error("do doSubExtractProcess error " + extractMessage + e.getMessage(), e);
        }
    }

    private void doSubExtract(Object subExtrat, ExtractMessage extractMessage) {
        if (subExtrat != null) {
            if (subExtrat instanceof Collection) {
                Object[] arrays = ((Collection) subExtrat).toArray();
                for (int i = 0; i < arrays.length; i++) {
                    this.doSubExtractProcess(extractMessage, arrays[i], i + 1);
                }
            } else {
                this.doSubExtractProcess(extractMessage, subExtrat, 1);
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    private Map doExtract(ExtractMessage extractMessage, ExtractorProcessorContext context, AbstractExtractResult result) {
        // set StoragePath to context
        context.getContext().put("StoragePath", result.getStoragePath());
        ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);
        request.setInput(extractMessage.getMessageObject());
        Response response = Extractor.extract(request.contextInit());
        Map extractResultMap = ResponseUtil.getResponsePageExtractResultMap(response);
        if (response.getAttribute("CRAWLER_EXCEPTION") != null) {
            result.setExtractCode(ExtractCode.EXTRACT_FAIL, ((Exception) response.getAttribute("CRAWLER_EXCEPTION")).getMessage());
        } else {
            if (MapUtils.isNotEmpty(extractResultMap)) {
                try {
                    result.setResultType(StringUtils.join(extractResultMap.keySet(), ","));
                    result.setPageExtractId(ResponseUtil.getPageExtractor(response).getId());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return extractResultMap;
    }

    private SubmitMessage extractAndSubmit(ExtractMessage extractMessage, ExtractorProcessorContext context, AbstractExtractResult result) {
        SubmitMessage submitMessage = new SubmitMessage();
        submitMessage.setExtractMessage(extractMessage);
        submitMessage.setResult(result);
        if (context != null) {
            submitMessage.setExtractResultMap(this.doExtract(extractMessage, context, result));
        } else {
            result.setExtractCode(ExtractCode.EXTRACT_CONF_FAIL);
        }
        // submit to redis & oss
        if (!submitProcessor.process(submitMessage)) {
            result.setExtractCode(ExtractCode.EXTRACT_STORE_FAIL);
        } else {
            extractMessage.setSubmitkeyResult(submitMessage.getSubmitkeyResult());
        }
        return submitMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.actor.AbstractActor#processMessage(java.lang.Object)
     */
    public void process(ExtractMessage extractMessage) {
        ExtractorProcessorContext context = contextBuilder.buildExtractorProcessorContext(extractMessage);
        AbstractExtractResult result = resultBuilder.buildExtractResult(extractMessage);
        if (result != null) {
            long start = System.currentTimeMillis();
            // extract & submit results to redis
            SubmitMessage submitMessage = this.extractAndSubmit(extractMessage, context, result);
            // doSubExtract extract & submit results to redis
            if (submitMessage.getExtractResultMap() != null) {
                this.doSubExtract(submitMessage.getExtractResultMap().get("subExtrat"), extractMessage);
            }
            result.setDuration(System.currentTimeMillis() - start);
            logger.info("extractAndSubmit resource:" + result + ",cost time:" + result.getDuration() + "ms");
            // save result recode to mysql
            resultStorage.doExtractResultSave(result);
            extractMessage.setExtractCode(ExtractCode.getExtractCode(result.getStatus()));
        } else {
            extractMessage.setExtractCode(ExtractCode.ERROR_INPUT);
        }
    }

}
