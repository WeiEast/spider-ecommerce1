/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import com.datatrees.common.util.ReflectionUtils;
import com.datatrees.common.util.type.TypeException;
import com.datatrees.common.util.type.TypeRegistry;
import com.datatrees.crawler.core.domain.config.extractor.ResultType;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.operation.OperationType;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.domain.config.segment.SegmentType;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.domain.config.service.ServiceType;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import com.datatrees.crawler.core.processor.format.impl.*;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.impl.*;
import com.datatrees.crawler.core.processor.page.AbstractPage;
import com.datatrees.crawler.core.processor.page.PageImpl;
import com.datatrees.crawler.core.processor.page.handler.BusinessTypeFilterHandler;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.datatrees.crawler.core.processor.segment.impl.*;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.crawler.core.processor.service.impl.*;
import com.datatrees.crawler.core.util.SpringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 1:23:56 PM
 */
public class ProcessorFactory {

    protected static final String SegmentPrefix    = "Segment_";
    protected static final String OperationPrefix  = "Operation_";
    protected static final String ResultTypePrefix = "ResultType_";
    protected static final String ServicePrefix    = "Service_";
    protected static final String PluginPrefix     = "Plugin_";
    protected static final String PagePrefix       = "Page_";
    protected static final String DEFAULT_KEY      = "default";
    private static final   Logger log              = LoggerFactory.getLogger(ProcessorFactory.class);

    static {
        try {
            // regist segment
            registSegment(SegmentType.REGEX, RegexSegmentImpl.class);
            registSegment(SegmentType.XPATH, XpathSegmentImpl.class);
            registSegment(SegmentType.JSONPATH, JsonPathSegmentImpl.class);
            registSegment(SegmentType.SPLIT, SplitSegmentImpl.class);
            registSegment(SegmentType.CALCULATE, CalculateSegmentImpl.class);

            registSegment(SegmentType.BASE, BaseSegmentImpl.class);

            // regist operation
            registOperation(OperationType.PARSER, ParserOperationImpl.class);
            registOperation(OperationType.XPATH, XpathOperationImpl.class);
            registOperation(OperationType.JSONPATH, JsonPathOperationImpl.class);
            registOperation(OperationType.REGEX, RegexOperationImpl.class);
            registOperation(OperationType.REPLACE, ReplaceOperationImpl.class);
            registOperation(OperationType.TEMPLATE, TemplateOperationImpl.class);
            registOperation(OperationType.CODEC, CodecOperationImpl.class);
            registOperation(OperationType.RETURN, ReturnOperationImpl.class);
            registOperation(OperationType.TRIM, TrimOperationImpl.class);
            registOperation(OperationType.SET, SetOperationImpl.class);
            registOperation(OperationType.EXTRACT, ExtractOperationImpl.class);
            registOperation(OperationType.APPEND, AppendOperationImpl.class);
            registOperation(OperationType.MATCHGROUP, MatchGroupOperationImpl.class);
            registOperation(OperationType.DATETIME, DateTimeOperationImpl.class);
            registOperation(OperationType.TRIPLE, TripleOperationImpl.class);
            registOperation(OperationType.MAILPARSER, MailParserOperationImpl.class);
            registOperation(OperationType.CALCULATE, CalculateOperationImpl.class);
            registOperation(OperationType.ESCAPE, EscapeOperationImpl.class);
            registOperation(OperationType.DECODE, DecodeOperationImpl.class);
            registOperation(OperationType.PROXYSET, ProxySetOperationImpl.class);
            registOperation(OperationType.MAPPING, MappingOperationImpl.class);
            registOperation(OperationType.SLEEP, SleepOperationImpl.class);
            registOperation(OperationType.RETURNMATCH, ReturnMatchOperationImpl.class);

            // regist service
            registService(ServiceType.Plugin_Service, PluginServiceImpl.class);
            registService(ServiceType.Grab_Service, GrabServiceImpl.class);
            registService(ServiceType.Task_Http_Service, TaskHttpServiceImpl.class);
            registService(null, DefaultService.class);

            // page
            registPage(PageImpl.class);

            // format
            registFormat(ResultType.DATE, DateFormatImpl.class);
            registFormat(ResultType.String, StringFormatImpl.class);
            registFormat(ResultType.NUMBER, NumberFormatImpl.class);
            registFormat(ResultType.PAYMENT, PaymentFormatImpl.class);
            registFormat(ResultType.FILE, FileFormatImpl.class);
            registFormat(ResultType.RESOURCESTRING, ResourceStringFormatImpl.class);

            registFormat(ResultType.CURRENCY, CurrencyFormatImpl.class);
            registFormat(ResultType.CURRENCYPAYMENT, CurrencyPaymentFormatImpl.class);
            registFormat(ResultType.RMB, RMBFormatImpl.class);
            registFormat(ResultType.BOOLEAN, BooleanFormatImpl.class);
            registFormat(ResultType.INT, IntFormatImpl.class);
            registFormat(ResultType.LONG, LongFormatImpl.class);

        } catch (Exception e) {
            // ignore
            log.error(e.getMessage());
        }

    }

    public static SegmentBase getSegment(AbstractSegment segment) throws Exception {
        SegmentType type = segment.getType();
        SegmentBase base = null;
        String key = SegmentPrefix + type.getValue();
        Class<SegmentBase> segmentImpl = TypeRegistry.getInstance().resolve(key);
        if (segmentImpl != null) {
            base = ReflectionUtils.newInstance(segmentImpl);
            base.setSegment(segment);
        }
        return base;
    }

    public static void registSegment(String name, Class<?> segment) throws Exception {
        TypeRegistry.getInstance().regist(SegmentPrefix + name, segment);
    }

    public static void registSegment(SegmentType name, Class<?> segment) throws Exception {
        registSegment(name.getValue(), segment);
    }

    public static void registOperation(String name, Class<?> segment) throws Exception {
        TypeRegistry.getInstance().regist(OperationPrefix + name, segment);
    }

    public static void registOperation(OperationType name, Class<?> segment) throws Exception {
        registOperation(name.getValue(), segment);
    }

    public static void registPage(Class<?> page) throws Exception {
        String key = resolveKey(PagePrefix, null);
        regist(key, page);
    }

    public static AbstractPage getPage(Page page) throws Exception {
        BusinessTypeFilterHandler businessTypeFilterHandler = (BusinessTypeFilterHandler) SpringUtil.getBeanByBeanName("businessTypeFilter");
        log.info("BusinessTypeFilterHandler is {}",businessTypeFilterHandler);
        AbstractPage base = null;
        String key = resolveKey(PagePrefix, null);
        Class<AbstractPage> serviceImpl = TypeRegistry.getInstance().resolve(key);
        if (serviceImpl != null) {
            base = ReflectionUtils.newInstance(serviceImpl);
            base.setPage(page);
            base.setBusinessTypeFilterhandler(businessTypeFilterHandler);
            log.info("businessTypeFilterHandler is {}", businessTypeFilterHandler);
        }
        return base;
    }

    public static void registService(ServiceType type, Class<?> service) throws Exception {
        String prefix = ServicePrefix;
        String key = null;
        if (type != null) {
            key = type.getValue();
        }
        key = resolveKey(prefix, key);
        regist(key, service);
    }

    private static void regist(String key, Class<?> clazz) {
        TypeRegistry.getInstance().regist(key, clazz);
    }

    protected static String resolveKey(String prefix, String key) {
        if (StringUtils.isEmpty(key)) {
            return prefix + DEFAULT_KEY;
        } else {
            return prefix + key;
        }
    }

    public static ServiceBase getService(AbstractService service) throws Exception {
        ServiceBase base = null;
        String key = ServicePrefix;
        if (service == null) {
            key = resolveKey(key, null);
        } else {
            key = resolveKey(key, service.getServiceType().getValue());
        }
        Class<ServiceBase> serviceImpl = TypeRegistry.getInstance().resolve(key);
        if (serviceImpl != null) {
            base = ReflectionUtils.newInstance(serviceImpl);
            base.setService(service);
        }
        return base;
    }

    public static Operation getOperation(AbstractOperation op) throws Exception {
        OperationType type = op.getType();
        Operation operation = null;
        String key = OperationPrefix + type.getValue();
        Class<Operation> operationImpl = TypeRegistry.getInstance().resolve(key);
        if (operationImpl != null) {
            operation = ReflectionUtils.newInstance(operationImpl);
            operation.setOperation(op);
        }
        return operation;
    }

    public static AbstractFormat getFormat(ResultType type, Configuration conf) throws Exception {
        String key = resolveKey(ResultTypePrefix, type.getValue());
        AbstractFormat format = instance(key, conf);
        format.setType(type);
        return format;

    }

    public static void registFormat(ResultType type, Class<? extends AbstractFormat> clazz) throws Exception {
        String key = resolveKey(ResultTypePrefix, type.getValue());
        regist(key, clazz);
    }

    protected static <T> T instance(Class<T> clazz, Configuration conf) throws Exception {
        T t = ReflectionUtils.newInstance(clazz);
        if (t instanceof Configurable) {
            ((Configurable) t).setConf(conf);
        }
        return t;
    }

    protected static <T> T instance(String key, Configuration conf) throws TypeException, Exception {
        return instance((Class<T>) TypeRegistry.getInstance().resolve(key), conf);
    }

}
