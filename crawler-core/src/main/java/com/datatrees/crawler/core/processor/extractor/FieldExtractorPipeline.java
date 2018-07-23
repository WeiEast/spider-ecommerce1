package com.datatrees.crawler.core.processor.extractor;

import java.util.List;

import com.datatrees.common.pipeline.ProcessPipeline;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.treefinance.crawler.framework.context.control.BusinessTypeDecider;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Jerry
 * @since 15:53 2018/6/10
 */
public class FieldExtractorPipeline extends ProcessPipeline {

    public FieldExtractorPipeline(List<FieldExtractor> fieldExtractors, AbstractProcessorContext context) {
        if (CollectionUtils.isNotEmpty(fieldExtractors)) {
            for (FieldExtractor fieldExtractor : fieldExtractors) {
                if (fieldExtractor == null) continue;

                if (BusinessTypeDecider.support(fieldExtractor.getBusinessType(), context)) {
                    FieldExtractorImpl fieldExtractorImpl = new FieldExtractorImpl(fieldExtractor);
                    addValve(fieldExtractorImpl);
                } else {
                    logger.warn("Skipped field-extractor[{}] with the forbidden business. field: {}, businessType: {} ,taskId: {}",
                            fieldExtractor.getId(), fieldExtractor.getField(), fieldExtractor.getBusinessType(), context.getTaskId());
                }
            }
        }
    }
}
