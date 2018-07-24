package com.datatrees.rawdatacentral.plugin.common.taobao.com.h5;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.datatrees.crawler.plugin.login.AbstractLoginPlugin.ContentType;
import com.datatrees.crawler.plugin.login.AbstractPicPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.treefinance.crawler.framework.format.base.StringFormatter;
import javassist.compiler.ast.CondExpr;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.jute.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此插件只能解决淘宝交易记录抓取中出现图片验证码问题
 * User: yand
 * Date: 2018/7/16
 */
public class TaoBaoRecordPlugin extends AbstractPicPlugin {

    private static final Logger logger = LoggerFactory.getLogger(TaoBaoRecordPlugin.class);

    {
        this.setTags("ecommerce");
        this.setTips("请输入图片验证码");
    }

    @Override
    public String process(String... args) throws Exception {
        logger.info("TaoBaoRecordPlugin  插件启动");
        Map<String, String> paramMap = GsonUtils.fromJson(args[0], new TypeToken<HashMap<String, String>>() {}.getType());
        Map<String, String> result;
        if (isNormalPage(paramMap)) {
            result = ImmutableMap.of(PluginConstants.FIELD, getPageContent(paramMap));
        } else {
            result = doProcess(paramMap);
        }

        return GsonUtils.toJson(result);
    }

    private String getPageContent(Map<String, String> paramMap) {
        return paramMap.get(PluginConstants.PAGE_CONTENT);
    }

    private boolean isNormalPage(Map<String, String> paramMap) {
        String pageContent = getPageContent(paramMap);
        if (pageContent.contains("\"rgv587_flag0\":\"sm\"")) {
            logger.info("taobao 交易页面 is {}", pageContent);
            return false;
        }
        return true;
    }

    @Override
    public String requestPicCode(Map<String, String> parms) {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String str = getPageContent(parms);
        String url = JsonPathUtil.readAsString(str, "$.url");
        LinkNode checkNode = new LinkNode(url);
        checkNode.setReferer(url);
        logger.info("请求url1 is {}", url);
        String pageContent = (String) getResponseByWebRequest(checkNode, ContentType.Content, null);
        logger.debug("访问安全页1：{}", pageContent);
        url = JsonPathUtil.readAsString(pageContent, "$.url");
        String smApp = PatternUtils.group(url, "smApp=([^&]+)", 1);
        String smPolicy = PatternUtils.group(url, "smPolicy=([^&]+)", 1);
        String smTag = PatternUtils.group(url, "smTag=([^&]+)", 1);
        String smReturn = PatternUtils.group(url, "smReturn=([^&]+)", 1);
        String smSign = PatternUtils.group(url, "smSign=([^&]+)", 1);
        checkNode = new LinkNode(url);
        checkNode.setReferer(url);
        logger.info("请求url2 is {}", url);
        pageContent = (String) getResponseByWebRequest(checkNode, ContentType.Content, null);
        logger.debug("访问安全页2：{}", pageContent);
        String identity = PatternUtils.group(pageContent, "identity:\\s*'([^']+)'", 1);
        String sessionid = PatternUtils.group(pageContent, "sessionid:\\s*'([^']+)'", 1);
        context.getContext().put("identity", identity);
        context.getContext().put("sessionid", sessionid);
        context.getContext().put("smTag", smTag);
        context.getContext().put("smReturn", smReturn);
        context.getContext().put("smSign", smSign);
        context.getContext().put("smPolicy", smPolicy);
        url = "https://pin.aliyun.com/get_img?identity=" + identity + "&sessionid=" + sessionid + "&type=150_40&t=" + System.currentTimeMillis();
        checkNode = new LinkNode(url);
        checkNode.setReferer(url);
        logger.info("请求url3 is {}", url);
        byte[] validCodeBytes = (byte[]) getResponseByWebRequest(checkNode, ContentType.ValidCode, null);
        if (validCodeBytes != null) {
            logger.info("访问安全页3：{}", Base64.encodeBase64String(validCodeBytes));
            return Base64.encodeBase64String(validCodeBytes);
        }
        return null;
    }

    @Override
    public String vaildPicCode(Map<String, String> parms, String pidCode) {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String identity = (String) context.getContext().get("identity");
        String sessionid = (String) context.getContext().get("sessionid");
        String smTag = (String) context.getContext().get("smTag");
        String smReturn = (String) context.getContext().get("smReturn");
        String smSign = (String) context.getContext().get("smSign");
        String smPolicy = (String) context.getContext().get("smPolicy");
        LinkNode validNode = new LinkNode("https://pin.aliyun.com/check_img?code=" + pidCode + "&_ksTS=" + timestampFlag() + "&callback=&identity=" + identity + "&sessionid=" + sessionid + "&delflag=0&type=150_40");
        String pageContent = (String) getResponseByWebRequest(validNode, ContentType.Content, null);
        logger.debug("校验后交易页面1：{}", pageContent);
        if (StringUtils.isNotBlank(pageContent) && pageContent.contains("message\":\"SUCCESS.\"")) {
            String url = "https://sec.taobao.com/query.htm?action=QueryAction&event_submit_do_unique=ok&smPolicy=" + smPolicy + "&smApp=trademanager&smReturn=" + smReturn + "&smCharset=UTF-8&smTag=" + smTag + "&captcha=&smSign=" + smSign + "&identity=" + identity + "&code=" + pidCode + "&_ksTS=" + timestampFlag() + "&callback=";
            logger.debug("校验后交易请求2：{}", url);
            validNode = new LinkNode(url);
            pageContent = (String) getResponseByWebRequest(validNode, ContentType.Content, null);
            logger.debug("校验后交易页面2：{}", pageContent);
            /**
             * 这地方的响应：
             * ({"url":"https://buyertrade.taobao.com/trade/itemlist/asyncBought.htm?action=itemlist%2FBoughtQueryAction
             &event_submit_do_query=1&_input_charset=utf8&dateBegin=0&dateEnd=0&pageNum=1&pageSize=100&queryOrder
             =desc&smToken=ca491215edee4c818edcef9af612b9db&smSign=qMoUSmXjKol3wWIIGDp63A%3D%3D","queryToken":"smToken
             =ca491215edee4c818edcef9af612b9db&smSign=qMoUSmXjKol3wWIIGDp63A%3D%3D"})
             * 下面的请求是猜测的
             */
            String urll = PatternUtils.group(pageContent, "\\\"url\\\":\\\"([^\\\"]+)\\\"", 1);
            String queryToken = PatternUtils.group(pageContent, "\\\"queryToken\\\":\\\"([^\\\"]+)\\\"", 1);
            logger.debug("校验后交易请求3：{}", urll);
            validNode = new LinkNode(urll + "&" + queryToken);
            pageContent = (String) getResponseByWebRequest(validNode, ContentType.Content, null);
            logger.debug("校验后交易页面3：{}", pageContent);
            return pageContent;
        }
        logger.error("imageCode input error!");
        return null;

    }

    private static String timestampFlag() {
        return System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
    }

}
