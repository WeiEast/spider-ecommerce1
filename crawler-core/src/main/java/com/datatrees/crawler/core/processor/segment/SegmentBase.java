/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment;

import java.util.*;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.extractor.ResultType;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.*;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorImpl;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import com.google.common.base.Preconditions;
import com.treefinance.crawler.framework.context.control.BusinessTypeDecider;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.format.Formatter;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @param <>
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 5:19:42 PM
 */
public abstract class SegmentBase<T extends AbstractSegment> extends Processor {

    protected       T            segment = null;
    private         List<String> splits  = null;
    private AbstractProcessorContext context;

    @Override
    protected void preProcess(Request request, Response response) throws Exception {
        super.preProcess(request, response);
        Preconditions.checkNotNull(getSegment(), "segment config should not be null!");
        Preconditions.checkNotNull(request.getInput(), "page content in segment should not be null!");
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        context = RequestUtil.getProcessorContext(request);
        String businessType = segment.getBusinessType();
        if (BusinessTypeDecider.support(businessType, context)) {
            String original = RequestUtil.getContent(request);
            processExtractor(request, response);
            request.setInput(original);
        } else {
            logger.info("segment skip businessType is {},taskId is {}", businessType, context.getTaskId());
        }
    }

    @SuppressWarnings({"unused", "rawtypes", "unchecked"})
    private void processExtractor(Request request, Response response) throws Exception {
        List resultList = initResultList(response);

        List<String> splits = getSplits(request);
        if (BooleanUtils.isTrue(segment.getNotEmpty()) && CollectionUtils.isEmpty(splits)) {
            throw new ResultEmptyException(segment + " result should not be Empty!");
        }

        logger.info("{} begin process, split size: {}", segment, splits.size());

        ResponseUtil.setSegmentsContent(response, splits);

        Integer maxCycles = segment.getMaxCycles();
        if (BooleanUtils.isTrue(segment.getIsReverse())) {
            Collections.reverse(splits);
        }

        String breakPattern;
        String disContains;
        String contains;

        // try to get split
        for (String split : splits) {
            if (StringUtils.isNotBlank(segment.getBreakPattern()) && StringUtils.isNotBlank(breakPattern = StandardExpression.eval(segment.getBreakPattern(), request, response)) && RegExp.find(split, breakPattern, segment.getBreakPatternFlag() != null ? segment.getBreakPatternFlag() : 0)) {
                logger.warn("{} match the break pattern:{}  break...", segment, breakPattern);
                break;
            }
            if (StringUtils.isNotBlank(segment.getDisContains()) && StringUtils.isNotBlank(disContains = StandardExpression.eval(segment.getDisContains(), request, response)) && RegExp.find(split, disContains, segment.getDisContainsFlag() != null ? segment.getDisContainsFlag() : 0)) {
                logger.info("split filtered,matches the dis-contains pattern: {}", disContains);
                continue;
            }
            if (StringUtils.isNotBlank(segment.getContains()) && StringUtils.isNotBlank(contains = StandardExpression.eval(segment.getContains(), request, response)) && !RegExp.find(split, contains, segment.getContainsFlag() != null ? segment.getContainsFlag() : 0)) {
                logger.info("split filtered,not matches the contains pattern: {}", contains);
                continue;
            }

            if (maxCycles != null) {
                if (resultList != null && resultList.size() >= maxCycles) {
                    logger.warn("{} reach the maxCycles: {},total-size: {}", segment, maxCycles, splits.size());
                    break;
                }
            }

            Map<String, Object> resultMap = new HashMap<String, Object>();
            request.setInput(split);
            // extract field
            List<FieldExtractor> fieldExtractors = segment.getFieldExtractorList();
            if (CollectionUtils.isNotEmpty(fieldExtractors)) {
                List<Processor> fieldExtractorProcessors = new ArrayList<Processor>(fieldExtractors.size());
                for (FieldExtractor fieldExtractor : fieldExtractors) {
                    if (BusinessTypeDecider.support(fieldExtractor.getBusinessType(), context)) {
                        FieldExtractorImpl fieldExtractorImpl = new FieldExtractorImpl();
                        fieldExtractorImpl.setFieldExtractor(fieldExtractor);
                        fieldExtractorProcessors.add(fieldExtractorImpl);
                    } else {
                        logger.info("FieldExtractor skip businessType is {},taskId is {}", fieldExtractor.getBusinessType(), context.getTaskId());
                    }

                }

                ProcessorRunner fieldProcessRunner = new ProcessorRunner(fieldExtractorProcessors);
                try {
                    fieldProcessRunner.run(request, response);
                } catch (ResultEmptyException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("Error invoking field extractor!", e);
                }
            }

            // extract segment object
            List<AbstractSegment> segments = segment.getSegmentList();
            for (AbstractSegment abstractSegment : segments) {
                Object segResultList = null;
                try {
                    if (BooleanUtils.isTrue(abstractSegment.getStandBy()) && resultMap.get(abstractSegment.getName()) != null && isValid(resultMap.get(abstractSegment.getName()))) {
                        logger.info("no need to execute the stand by segment: {}", segment);
                        continue;
                    }
                    String content = split;
                    String sourceId = abstractSegment.getSourceId();
                    if (StringUtils.isNotEmpty(sourceId)) {
                        Object result = SourceUtil.getSourceMap(sourceId, request, response);
                        if (result != null) {
                            content = result.toString();
                        }
                    }
                    if (StringUtils.isEmpty(content)) {
                        logger.warn("stop due to upper field value is empty! segment: {}", segment);
                    } else {
                        Request newRequest = new Request();
                        newRequest.setInput(content);
                        RequestUtil.setProcessorContext(newRequest, RequestUtil.getProcessorContext(request));
                        RequestUtil.setConf(newRequest, RequestUtil.getConf(request));
                        RequestUtil.setContext(newRequest, RequestUtil.getContext(request));
                        RequestUtil.setRequestVisibleFields(newRequest, RequestUtil.getRequestVisibleFields(request));
                        RequestUtil.setCurrentUrl(newRequest, RequestUtil.getCurrentUrl(request));
                        Response segResponse = Response.build();
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
                if (BooleanUtils.isTrue(abstractSegment.getNotEmpty()) && (segResultList == null || (segResultList instanceof Collection && CollectionUtils.isEmpty((Collection) segResultList)))) {
                    throw new ResultEmptyException(abstractSegment + " result should not be Empty!");
                }
                if (segResultList != null) {
                    this.valueListToResultMap(resultMap, segResultList, abstractSegment.getName());
                }
            }

            // get response result
            Map<String, FieldExtractorWarpper> fieldMap = ResponseUtil.getResponseFieldResult(response);

            if (fieldMap != null) {
                fieldWrapperMapToField(resultMap, fieldMap);
                // reset field result
                ResponseUtil.setResponseFieldResult(response, new HashMap<String, FieldExtractorWarpper>());
            }
            if (CollectionUtils.isEmpty(segment.getSegmentList()) && ResultType.getResultType(segment.getResultClass()) != null) {
                // convert to basic result type
                ResultType type = ResultType.getResultType(segment.getResultClass());
                Configuration conf = RequestUtil.getConf(request);
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
                if (resultObject != null && resultObject instanceof Map) {
                    resultMap.remove(segment.getName());
                    ((Map) resultObject).putAll(resultMap);
                    resultList.add(resultObject);
                } else if (resultObject != null && resultObject instanceof Collection) {
                    resultMap.remove(segment.getName());
                    for (Object map : (Collection) resultObject) {
                        if (map != null && map instanceof Map) {
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
        resultList = initResultList(response);// reget get resultList

        // result merge
        if (BooleanUtils.isTrue(segment.getMerge()) && CollectionUtils.isNotEmpty(resultList)) {
            CollectionUtils.filter(resultList, new UniquePredicate());
        }
        // get pop return
        if (BooleanUtils.isTrue(segment.getPopReturn())) {
            Object result = null;
            if (CollectionUtils.isNotEmpty(resultList)) {
                result = resultList.get(0);
            }
            // result reset
            ResponseUtil.setSegmentsResults(response, result);
        }
    }

    private void segmentsResultsConvert(Request request, Response response) {
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
     * @param fieldMap
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fieldWrapperMapToField(Map<String, Object> originalResultMap, Map<String, FieldExtractorWarpper> fieldMap) {
        Iterator<String> fieldIds = fieldMap.keySet().iterator();
        while (fieldIds.hasNext()) {
            String id = fieldIds.next();
            FieldExtractorWarpper fWarpper = fieldMap.get(id);
            String fieldName = fWarpper.getExtractor().getField();
            Object result = fWarpper.getResult();
            // remove tmp field
            // set result when filed name is not in and the result is not null
            if (!fieldName.equalsIgnoreCase("temp") && result != null) {
                this.valueListToResultMap(originalResultMap, result, fieldName);
            }
        }
    }

    public List<String> getSplits(Request request) {
        if (CollectionUtils.isEmpty(splits)) {
            splits = getSplit(request);
            if (CollectionUtils.isEmpty(splits)) {
                logger.warn("config error! can't find segments.");
            }
        }
        return splits;
    }

    /**
     * get segment splits by xpath / regex or split
     * @param request
     * @return
     */
    protected abstract List<String> getSplit(Request request);

    public T getSegment() {
        return segment;
    }

    public void setSegment(T segment) {
        this.segment = segment;
    }

    /**
     * return List<Map<String, Object>> or List<ResultType>
     * @param response
     * @return
     */
    @SuppressWarnings("rawtypes")
    private List initResultList(Response response) {
        List resultList = (List) ResponseUtil.getSegmentsResults(response);
        if (resultList == null) {
            resultList = new ArrayList();
            ResponseUtil.setSegmentsResults(response, resultList);
        }
        return resultList;
    }

    private boolean isValid(Object obj) {
        // Check whether the data is valid
        return !(obj instanceof Collection) || !CollectionUtils.isEmpty((Collection) obj);
    }

    @Override
    protected void postProcess(Request request, Response response) throws Exception {
        // invoke next valve
        if (getNext() != null) {
            try {
                getNext().invoke(request, response);
            } catch (Exception e) {
                logger.error("invoke next segment error!", e);
            }
        }
    }
}
