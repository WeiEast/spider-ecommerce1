/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment;

import javax.annotation.Nonnull;
import java.util.*;

import com.datatrees.common.conf.Configuration;
import com.datatrees.crawler.core.domain.config.extractor.ResultType;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.ResultMapConverter;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.FieldExtractResultSet;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorPipeline;
import com.treefinance.crawler.framework.context.control.BusinessTypeDecider;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderRequestFactory;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.crawler.framework.context.pipeline.FailureSkipProcessorValve;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.format.Formatter;
import com.treefinance.crawler.framework.util.SourceUtils;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @param <>
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 5:19:42 PM
 */
public abstract class SegmentBase<T extends AbstractSegment> extends FailureSkipProcessorValve {

    private final T                        segment;

    private       List<String>             splits = null;

    private       AbstractProcessorContext context;

    public SegmentBase(@Nonnull T segment) {
        this.segment = Objects.requireNonNull(segment);
    }

    @Override
    protected boolean isSkipped(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        if (request.getInput() == null) {
            logger.warn("Empty input content used for segment processing and skip. segment: {}, taskId: {}", segment.getName(), context.getTaskId());
            return true;
        }

        context = request.getProcessorContext();
        String businessType = segment.getBusinessType();
        if (!BusinessTypeDecider.support(businessType, context)) {
            logger.warn("Business forbidden in segment processing and skip. segment: {}, businessType: {}, taskId: {}", segment.getName(), businessType, context.getTaskId());
            return true;
        }

        return false;
    }

    @Override
    public final void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        List<String> splits = getSplits(request, response);

        if (CollectionUtils.isEmpty(splits)) {
            if (Boolean.TRUE.equals(segment.getNotEmpty())) {
                throw new ResultEmptyException(segment + " result should not be Empty!");
            }
        } else {
            String original = (String) request.getInput();
            try {
                doProcess(request, response);
            } finally {
                request.setInput(original);
            }
        }

        ResponseUtil.setSegmentsContent(response, splits);

        adaptResult(response);
    }

    private void adaptResult(SpiderResponse response) {
        List<Object> resultList = ResponseUtil.prepareSegmentsResults(response);

        // result merge
        if (Boolean.TRUE.equals(segment.getMerge()) && CollectionUtils.isNotEmpty(resultList)) {
            CollectionUtils.filter(resultList, new UniquePredicate());
        }

        // get pop return
        if (Boolean.TRUE.equals(segment.getPopReturn())) {
            Object result;
            if (CollectionUtils.isNotEmpty(resultList)) {
                result = resultList.get(0);
            } else {
                result = new HashMap<String, Object>();
            }
            // result reset
            ResponseUtil.setSegmentsResults(response, result);
        }
    }

    private void doProcess(SpiderRequest request, SpiderResponse response) throws Exception {
        List<Object> resultList = ResponseUtil.prepareSegmentsResults(response);

        List<String> splits = getSplits(request, response);

        logger.info("{} begin process, split size: {}", segment, splits.size());

        if (Boolean.TRUE.equals(segment.getIsReverse())) {
            Collections.reverse(splits);
        }

        Integer maxCycles = segment.getMaxCycles();

        logger.debug("Segment-break pattern: {}", segment.getBreakPattern());
        String breakPattern = StandardExpression.eval(segment.getBreakPattern(), request, response);
        logger.debug("Segment-dis-contains pattern: {}", segment.getDisContains());
        String disContains = StandardExpression.eval(segment.getDisContains(), request, response);
        logger.debug("Segment-contains pattern: {}", segment.getContains());
        String contains = StandardExpression.eval(segment.getContains(), request, response);

        for (String split : splits) {
            if (matches(split, breakPattern, segment.getBreakPatternFlag(), false)) {
                logger.warn("Break segment content processing with the given break pattern: {}", breakPattern);
                break;
            }
            if (matches(split, disContains, segment.getDisContainsFlag(), false)) {
                logger.warn("Skip segment content processing with the given dis-contains pattern: {}", disContains);
                continue;
            }
            if (matches(split, contains, segment.getContainsFlag(), true)) {
                logger.warn("Skip segment content processing with the mismatch contains pattern: {}", contains);
                continue;
            }

            if (maxCycles != null && resultList != null && resultList.size() >= maxCycles) {
                logger.warn("Break segment content processing with reaching the limit cycles{} reach the maxCycles: {},total-size: {}", segment,
                        maxCycles, splits.size());
                break;
            }

            Map<String, Object> resultMap = new HashMap<>();
            request.setInput(split);
            // extract field

            FieldExtractorPipeline pipeline = new FieldExtractorPipeline(segment.getFieldExtractorList(), context);
            pipeline.invokeQuietly(request, response);

            // extract segment object
            List<AbstractSegment> segments = segment.getSegmentList();
            for (AbstractSegment abstractSegment : segments) {
                Object segResultList = null;
                try {
                    if (BooleanUtils.isTrue(abstractSegment.getStandBy()) && isValid(resultMap.get(abstractSegment.getName()))) {
                        logger.info("no need to execute the stand by segment: {}", segment);
                        continue;
                    }
                    String content = split;
                    String sourceId = abstractSegment.getSourceId();
                    if (StringUtils.isNotEmpty(sourceId)) {
                        Object result = SourceUtils.getSourceFieldValue(sourceId, request, response);
                        if (result != null) {
                            content = result.toString();
                        }
                    }
                    if (StringUtils.isEmpty(content)) {
                        logger.warn("stop due to upper field value is empty! segment: {}", segment);
                    } else {
                        SpiderRequest newRequest = request.withInput(content);

                        SpiderResponse segResponse = SpiderResponseFactory.make();
                        SegmentBase segmentBase = ProcessorFactory.getSegment(abstractSegment);
                        segmentBase.invoke(newRequest, segResponse);
                        segResultList = ResponseUtil.getSegmentsResults(segResponse);
                    }
                } catch (ResultEmptyException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("invoke segment processor error!", e);
                    segResultList = null;
                }
                // 暂时保留 兼容老程序
                if (BooleanUtils.isTrue(abstractSegment.getNotEmpty()) &&
                        (segResultList == null || (segResultList instanceof Collection && CollectionUtils.isEmpty((Collection) segResultList)))) {
                    throw new ResultEmptyException(abstractSegment + " result should not be Empty!");
                }
                if (segResultList != null) {
                    this.valueListToResultMap(resultMap, segResultList, abstractSegment.getName());
                }
            }

            // get response result
            FieldExtractResultSet fieldExtractResultSet = ResponseUtil.getFieldExtractResultSet(response);

            if (fieldExtractResultSet != null) {
                fieldWrapperMapToField(resultMap, fieldExtractResultSet);
                // reset field result
                ResponseUtil.setFieldExtractResultSet(response, new FieldExtractResultSet());
            }
            if (CollectionUtils.isEmpty(segment.getSegmentList()) && ResultType.getResultType(segment.getResultClass()) != null) {
                // convert to basic result type
                ResultType type = ResultType.getResultType(segment.getResultClass());
                Configuration conf = request.getConfiguration();
                Formatter formatter = ProcessorFactory.getFormatter(type, conf);
                Collection values = resultMap.values();
                if (CollectionUtils.isNotEmpty(values) && formatter.supportResultType(values.toArray()[0])) {
                    logger.debug("{} format to {} with type: {}", resultMap, values.toArray()[0], type);

                    resultList.add(values.toArray()[0]);
                } else {
                    logger.warn("{} failed to format to {}", resultMap, segment.getResultClass());
                }
            } else {
                // obj-seg --> obj-seg with the same name
                Object resultObject = resultMap.get(segment.getName());
                if (resultObject instanceof Map) {
                    resultMap.remove(segment.getName());
                    ((Map) resultObject).putAll(resultMap);
                    resultList.add(resultObject);
                } else if (resultObject instanceof Collection) {
                    resultMap.remove(segment.getName());
                    for (Object map : (Collection) resultObject) {
                        if (map instanceof Map) {
                            ((Map) map).putAll(resultMap);
                        }
                    }
                    resultList.addAll((Collection) resultObject);
                } else {
                    if (MapUtils.isNotEmpty(resultMap)) {
                        if (StringUtils.isNotBlank(segment.getResultClass())) {
                            resultMap.put(Constants.SEGMENT_RESULT_CLASS_NAMES, segment.getResultClass());
                        }
                        resultList.add(resultMap);
                    }
                }
            }
        }

        this.segmentsResultsConvert(request, response);
    }

    @Nonnull
    private List<String> getSplits(SpiderRequest request, SpiderResponse response) {
        if (splits == null) {
            splits = splitInputContent((String) request.getInput(), segment, request, response);
            if (splits == null) {
                splits = Collections.emptyList();
            }
        }
        return splits;
    }

    protected abstract List<String> splitInputContent(String content, T segment, SpiderRequest request, SpiderResponse response);

    private boolean matches(String content, String pattern, Integer patternFlag, boolean reverse) {
        if (StringUtils.isBlank(pattern)) {
            return false;
        }

        return reverse ^ RegExp.find(content, pattern, patternFlag != null ? patternFlag : 0);
    }

    private void segmentsResultsConvert(SpiderRequest request, SpiderResponse response) {
        List results = new ArrayList();
        LinkNode current = RequestUtil.getCurrentUrl(request);
        String baseURL = null;
        if (current != null) {
            baseURL = (StringUtils.isNotEmpty(current.getBaseUrl()) ? current.getBaseUrl() : current.getUrl());
        }

        Object object = ResponseUtil.getSegmentsResults(response);
        if (object instanceof List) {
            for (Object obj : (List) object) {
                ResultMapConverter.convert(obj, baseURL, current, results);
            }
        } else {
            ResultMapConverter.convert(object, baseURL, current, results);
        }
        ResponseUtil.setSegmentsResults(response, results);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void valueListToResultMap(Map<String, Object> resultMap, Object segResultList, String segmentName) {
        Object originalResult = resultMap.get(segmentName);
        if (originalResult == null) {
            resultMap.put(segmentName, segResultList);
        } else if (!(originalResult instanceof Collection)) {
            List newResults = new ArrayList();
            newResults.add(originalResult);
            if (segResultList instanceof Collection) {
                newResults.addAll((Collection) segResultList);
            } else {
                newResults.add(segResultList);
            }
            resultMap.put(segmentName, newResults);
        } else {
            if (segResultList instanceof Collection) {
                ((Collection) originalResult).addAll((Collection) segResultList);
            } else {
                ((Collection) originalResult).add(segResultList);
            }
        }
    }

    /**
     * mapper field id --> field warpper to field name --> field val
     * @param fieldExtractResultSet
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fieldWrapperMapToField(Map<String, Object> originalResultMap, FieldExtractResultSet fieldExtractResultSet) {
        fieldExtractResultSet.forEach((id, fWarpper) -> {
            String fieldName = fWarpper.getExtractor().getField();
            Object result = fWarpper.getResult();
            // remove tmp field
            // set result when filed name is not in and the result is not null
            if (!fieldName.equalsIgnoreCase("temp") && result != null) {
                this.valueListToResultMap(originalResultMap, result, fieldName);
            }
        });
    }

    public T getSegment() {
        return segment;
    }

    /**
     * Check whether the data is valid
     */
    private boolean isValid(Object obj) {
        if (obj == null) {
            return false;
        }

        return !(obj instanceof Collection) || !CollectionUtils.isEmpty((Collection) obj);
    }

}
