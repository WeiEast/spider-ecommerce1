package com.datatrees.spider.share.service.extract;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.Collection;

import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.util.StoragePathUtil;
import com.datatrees.spider.share.service.util.UniqueKeyGenUtil;
import com.treefinance.crawler.exception.UnexpectedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ExtractResultHandlerFactory {

    @Resource
    private ApplicationContext context;

    @Nonnull
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        AbstractExtractResult result = null;
        Collection<ExtractResultHandler> handlers = context.getBeansOfType(ExtractResultHandler.class).values();
        for (ExtractResultHandler handler : handlers) {
            if (handler.getSupportResultType() == extractMessage.getResultType()) {
                result = handler.build(extractMessage);
                break;
            }
        }
        if (result == null) {
            throw new UnexpectedException("Initial extract result failure!");
        }

        result.setUniqueMd5(UniqueKeyGenUtil.uniqueKeyGen(result.getUniqueSign()));
        result.setTaskId(extractMessage.getTaskLogId());
        result.setWebsiteId(extractMessage.getWebsiteId());
        result.setStoragePath(StoragePathUtil.genStoragePath(extractMessage, result.getUniqueMd5()));
        return result;
    }

    public  void save(AbstractExtractResult result){
        Collection<ExtractResultHandler> handlers = context.getBeansOfType(ExtractResultHandler.class).values();
        for (ExtractResultHandler handler : handlers) {
            if(StringUtils.equals(result.getClass().getName(),handler.getSupportResult().getName())){
                handler.save(result);
                break;
            }

        }
    }
}
