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

package com.treefinance.crawler.framework.process;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import com.treefinance.crawler.framework.config.xml.extractor.FieldExtractor;
import com.treefinance.crawler.framework.config.enums.fields.ResultType;
import com.treefinance.crawler.framework.config.xml.operation.AbstractOperation;
import com.treefinance.crawler.framework.config.enums.operation.OperationType;
import com.treefinance.crawler.framework.config.xml.segment.AbstractSegment;
import com.treefinance.crawler.framework.config.enums.SegmentType;
import com.treefinance.crawler.framework.config.xml.service.AbstractService;
import com.treefinance.crawler.framework.config.enums.ServiceType;
import com.treefinance.crawler.framework.process.operation.Operation;
import com.treefinance.crawler.framework.process.operation.impl.*;
import com.treefinance.crawler.framework.process.segment.SegmentBase;
import com.treefinance.crawler.framework.process.segment.impl.*;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.crawler.core.processor.service.impl.DefaultService;
import com.datatrees.crawler.core.processor.service.impl.PluginServiceImpl;
import com.datatrees.crawler.core.processor.service.impl.TaskHttpServiceImpl;
import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.format.Formatter;
import com.treefinance.crawler.framework.format.base.BooleanFormatter;
import com.treefinance.crawler.framework.format.base.IntegerFormatter;
import com.treefinance.crawler.framework.format.base.LongFormatter;
import com.treefinance.crawler.framework.format.base.StringFormatter;
import com.treefinance.crawler.framework.format.datetime.DateFormatter;
import com.treefinance.crawler.framework.format.money.CurrencyFormatter;
import com.treefinance.crawler.framework.format.money.CurrencyPaymentFormatter;
import com.treefinance.crawler.framework.format.money.PaymentFormatter;
import com.treefinance.crawler.framework.format.money.RmbFormatter;
import com.treefinance.crawler.framework.format.number.NumberFormatter;
import com.treefinance.crawler.framework.format.special.FileFormatter;
import com.treefinance.crawler.framework.format.special.ResourceStringFormatter;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 1:23:56 PM
 */
public final class ProcessorFactory {

    private static final Map<Enum, Class> REGISTER = new HashMap<>();

    static {
        register(SegmentType.BASE, BaseSegmentImpl.class);
        register(SegmentType.REGEX, RegexSegmentImpl.class);
        register(SegmentType.XPATH, XpathSegmentImpl.class);
        register(SegmentType.JSONPATH, JsonPathSegmentImpl.class);
        register(SegmentType.SPLIT, SplitSegmentImpl.class);
        register(SegmentType.CALCULATE, CalculateSegmentImpl.class);

        register(OperationType.PARSER, ParserOperationImpl.class);
        register(OperationType.XPATH, XpathOperationImpl.class);
        register(OperationType.JSONPATH, JsonPathOperationImpl.class);
        register(OperationType.REGEX, RegexOperationImpl.class);
        register(OperationType.REPLACE, ReplaceOperationImpl.class);
        register(OperationType.TEMPLATE, TemplateOperationImpl.class);
        register(OperationType.CODEC, CodecOperationImpl.class);
        register(OperationType.RETURN, ReturnOperationImpl.class);
        register(OperationType.TRIM, TrimOperationImpl.class);
        register(OperationType.SET, SetOperationImpl.class);
        register(OperationType.EXTRACT, ExtractOperationImpl.class);
        register(OperationType.APPEND, AppendOperationImpl.class);
        register(OperationType.MATCHGROUP, MatchGroupOperationImpl.class);
        register(OperationType.DATETIME, DateTimeOperationImpl.class);
        register(OperationType.TRIPLE, TripleOperationImpl.class);
        register(OperationType.MAILPARSER, MailParserOperationImpl.class);
        register(OperationType.CALCULATE, CalculateOperationImpl.class);
        register(OperationType.ESCAPE, EscapeOperationImpl.class);
        register(OperationType.DECODE, DecodeOperationImpl.class);
        register(OperationType.PROXYSET, ProxySetOperationImpl.class);
        register(OperationType.MAPPING, MappingOperationImpl.class);
        register(OperationType.SLEEP, SleepOperationImpl.class);
        register(OperationType.RETURNMATCH, ReturnMatchOperationImpl.class);

        register(ServiceType.Plugin_Service, PluginServiceImpl.class);
        register(ServiceType.Task_Http_Service, TaskHttpServiceImpl.class);
        register(ServiceType.Default, DefaultService.class);

        register(ResultType.DATE, DateFormatter.class);
        register(ResultType.String, StringFormatter.class);
        register(ResultType.NUMBER, NumberFormatter.class);
        register(ResultType.PAYMENT, PaymentFormatter.class);
        register(ResultType.FILE, FileFormatter.class);
        register(ResultType.RESOURCE_STRING, ResourceStringFormatter.class);
        register(ResultType.CURRENCY, CurrencyFormatter.class);
        register(ResultType.CURRENCY_PAYMENT, CurrencyPaymentFormatter.class);
        register(ResultType.RMB, RmbFormatter.class);
        register(ResultType.BOOLEAN, BooleanFormatter.class);
        register(ResultType.INT, IntegerFormatter.class);
        register(ResultType.LONG, LongFormatter.class);
    }

    private ProcessorFactory() {
    }

    public static void register(Enum type, Class clazz) {
        REGISTER.put(type, clazz);
    }

    @Nonnull
    private static <R> Class<R> searchRegister(@Nonnull Enum type, String name) {
        Class<R> clazz = REGISTER.get(type);
        if (clazz == null) {
            throw new UnexpectedException("Not found the " + name + " processor in register. " + name + "-type: " + type);
        }
        return clazz;
    }

    public static <T extends AbstractSegment, R extends SegmentBase<T>> R getSegment(@Nonnull T segment) {
        SegmentType type = segment.getType();

        Class<R> clazz = searchRegister(type, "segment");

        try {
            Constructor<R> constructor = clazz.getConstructor(segment.getClass());
            return constructor.newInstance(segment);
        } catch (Exception e) {
            throw new UnexpectedException("Error new instance for class: " + clazz, e);
        }
    }

    public static <T extends AbstractOperation, R extends Operation<T>> R getOperation(@Nonnull T op, @Nonnull FieldExtractor fieldExtractor) {
        OperationType type = op.getType();

        Class<R> clazz = searchRegister(type, "segment");

        try {
            Constructor<R> constructor = clazz.getConstructor(op.getClass(), FieldExtractor.class);
            return constructor.newInstance(op, fieldExtractor);
        } catch (Exception e) {
            throw new UnexpectedException("Error new instance for class: " + clazz, e);
        }
    }

    public static <T extends AbstractService, R extends ServiceBase<T>> R getService(T service) {
        boolean isDefault = service == null;
        ServiceType type = isDefault ? ServiceType.Default : service.getServiceType();

        Class<R> clazz = searchRegister(type, "service");

        try {
            if (isDefault) {
                return clazz.newInstance();
            } else {
                Constructor<R> constructor = clazz.getConstructor(service.getClass());
                return constructor.newInstance(service);
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error new instance for class: " + clazz, e);
        }
    }

    public static Formatter getFormatter(ResultType type, Configuration conf) {
        Class<Formatter> clazz = searchRegister(type, "formatter");

        Formatter formatter;
        try {
            formatter = clazz.newInstance();
        } catch (Exception e) {
            throw new UnexpectedException("Error new instance for class: " + clazz, e);
        }

        if (formatter instanceof Configurable) {
            ((Configurable) formatter).setConf(conf);
        }

        return formatter;
    }
}
