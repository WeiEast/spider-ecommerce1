package com.datatrees.crawler.core.processor.login;

import java.util.List;
import java.util.Map;

import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.common.protocol.metadata.Metadata;
import com.datatrees.common.protocol.util.CookieFormater;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.login.LoginCheckConfig;
import com.datatrees.crawler.core.domain.config.login.LoginConfig;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.*;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.google.common.net.HttpHeaders;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderRequestFactory;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.crawler.framework.context.pipeline.InvokeException;
import com.treefinance.crawler.framework.decode.DecodeUtils;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.util.ServiceUtils;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 21, 2014 10:00:52 AM
 */
public class LoginUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoginUtil.class);
    private static LoginUtil loginUtil = new LoginUtil();

    private LoginUtil() {
    }

    /**
     *
     */
    public static LoginUtil getInstance() {
        return loginUtil;
    }

    /**
     * map key :username ,password
     */
    @SuppressWarnings("unchecked")
    public String doLogin(LoginConfig config, SearchProcessorContext context) {
        try {
            String jsonHeader = config.getHeaders();
            String loginUrl = StandardExpression.eval(config.getUrlTemplate(), context.getContext());
            LinkNode linkNode = new LinkNode(loginUrl);
            // // add json headers
            if (StringUtils.isNotEmpty(jsonHeader)) {
                Map<String, String> headersMap = (Map<String, String>) GsonUtils.fromJson(jsonHeader, Map.class);
                linkNode.addHeaders(headersMap);
            }
            // get retainQuote
            boolean retainQuote = context.getCookieConf() != null ? context.getCookieConf().getRetainQuote() : false;

            ProtocolOutput out = this.getProtocolOutput(linkNode, context);

            if (out != null && out.getContent() != null) {
                Metadata metadata = out.getContent().getMetadata();
                String[] vals = metadata.getValues(HttpHeaders.SET_COOKIE);

                return CookieFormater.INSTANCE.parserCookie(vals, retainQuote);
            }
        } catch (Exception ex) {
            logger.error("LoginUtil doLogin throw exception, error message:[" + ex.getMessage() + "]");
        }
        return StringUtils.EMPTY;
    }

    private ProtocolOutput getProtocolOutput(LinkNode linkNode, SearchProcessorContext context) throws InvokeException, ResultEmptyException {
        AbstractService service = context.getDefaultService();

        SpiderResponse response = ServiceUtils.invoke(service, linkNode, context, null, null);

        return ResponseUtil.getProtocolResponse(response);
    }

    /**
     * do login action with the given cookie.
     * <p>
     * Note: The cookie string is contained in {@link SearchProcessorContext#getContext()}
     * </p>
     * @param config  the login config
     * @param context the search context
     * @return true if it is login successfully, otherwise false.
     */
    @SuppressWarnings("unchecked")
    public boolean doLoginByCookies(LoginConfig config, SearchProcessorContext context) throws ResultEmptyException {
        try {
            String cookie = ProcessorContextUtil.getCookieString(context);

            if (StringUtils.isEmpty(cookie)) {
                logger.warn("Empty cookies!");
                return false;
            }

            LoginCheckConfig loginCheckConfig = config.getLoginCheckConfig();
            String checkUrl = StandardExpression.eval(loginCheckConfig.getCheckUrl(), context.getContext());
            logger.info("do login check, url: {}", checkUrl);

            LinkNode linkNode = new LinkNode(checkUrl);

            // // add json headers
            String jsonHeader = loginCheckConfig.getHeaders();
            if (StringUtils.isNotEmpty(jsonHeader)) {
                Map<String, String> headersMap = (Map<String, String>) GsonUtils.fromJson(jsonHeader, Map.class);
                linkNode.addHeaders(headersMap);
            }

            ProtocolOutput out = this.getProtocolOutput(linkNode, context);

            return checkLoginResult(out, loginCheckConfig, context);
        } catch (ResultEmptyException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("LoginUtil doLoginByCookies throw exception, error message:[" + ex.getMessage() + "]", ex);
        }
        return false;
    }

    private boolean checkLoginResult(ProtocolOutput out, LoginCheckConfig loginCheckConfig, SearchProcessorContext context) throws ResultEmptyException {
        if (out != null && out.getContent() != null) {
            String responseBody = out.getContent().getContentAsString();

            if (isFailure(responseBody, loginCheckConfig.getFailPattern())) {
                return false;
            }

            String successPattern = StandardExpression.eval(loginCheckConfig.getSuccessPattern(), context.getContext());
            // match by page content or cookieString
            if (isSuccess(responseBody, context, successPattern)) {
                callSegments(loginCheckConfig.getSegmentList(), context, responseBody);

                return true;
            }
        }
        return false;
    }

    private void callSegments(List<AbstractSegment> segments, SearchProcessorContext context, String responseBody) throws ResultEmptyException {
        if (CollectionUtils.isNotEmpty(segments)) {
            SpiderRequest request = SpiderRequestFactory.make();
            request.setProcessorContext(context);

            // decode
            request.setInput(DecodeUtils.decodeContent(responseBody, request));
            for (AbstractSegment abstractSegment : segments) {
                try {
                    SpiderResponse segResponse = SpiderResponseFactory.make();
                    SegmentBase segmentBase = ProcessorFactory.getSegment(abstractSegment);
                    segmentBase.invoke(request, segResponse);
                } catch (ResultEmptyException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("invoke segment processor error!", e);
                }
            }
        }
    }

    private boolean isSuccess(String responseBody, SearchProcessorContext context, String successPattern) {
        if (successPattern != null) {
            if (RegExp.find(responseBody, successPattern)) {
                return true;
            }

            String cookie = ProcessorContextUtil.getCookieString(context);

            return cookie != null && RegExp.find(cookie, successPattern);
        }

        return false;
    }

    private boolean isFailure(String responseBody, String failPattern) {
        return failPattern != null && RegExp.find(responseBody, failPattern);
    }

}
