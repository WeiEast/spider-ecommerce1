package com.datatrees.spider.share.common.http;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

/**
 * url特殊处理
 * @author zhouxinghai
 * @date 2017/11/21
 */
public class URIUtils {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(URIUtils.class);

    public static URI create(String fullUrl) {
        try {
            return URI.create(fullUrl);
        } catch (Exception e) {
            logger.warn("create url error,fullUrl={}", fullUrl, e);
            return createAndEncode(fullUrl);
        }
    }

    public static URI createAndEncode(String fullUrl) {
        try {
            if (!StringUtils.contains(fullUrl, "?")) {
                return URI.create(fullUrl);
            }
            Map<String, String> params = getQueryParams(fullUrl);
            if (null == params || params.isEmpty()) {
                return URI.create(fullUrl);
            }
            String url = StringUtils.substringBefore(fullUrl, "?");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), null == entry.getValue() ? "" : String.valueOf(entry.getValue())));
            }
            url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, "UTF-8"));
            return URI.create(url);
        } catch (Throwable e) {
            logger.error("createAndEncode error fullUrl={}", fullUrl, e);
            return null;
        }
    }

    public static Map<String, String> getQueryParams(String fullUrl) {
        if (StringUtils.isBlank(fullUrl)) {
            logger.warn("fullUrl is blank");
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String queryString = StringUtils.substringAfter(fullUrl, "?");
        if (StringUtils.isNotBlank(queryString)) {
            String[] kvs = StringUtils.split(queryString, "&");
            if (null == kvs || kvs.length == 0) {
                logger.warn("query param is blank ,fullUrl={}", fullUrl);
                return map;
            }
            for (String kv : kvs) {
                if (StringUtils.isBlank(kv)) {
                    logger.warn("key value is blank");
                    continue;
                }
                String[] ss = StringUtils.split(kv, "=");
                if (ss.length == 1) {
                    map.put(ss[0], "");
                    continue;
                } else if (ss.length == 1) {
                    logger.warn("invalid namevalue kv={}", kv);
                    continue;
                } else {
                    map.put(ss[0], ss[1]);
                }
            }
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        String fullUrl = FileUtils.readFileToString(new File("/data/url.txt"), Charset.forName("UTF-8"));
        URI uri = create(fullUrl);
        System.out.println(uri.toString());

    }

}
