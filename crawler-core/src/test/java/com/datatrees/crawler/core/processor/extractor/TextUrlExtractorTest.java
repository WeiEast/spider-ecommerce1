/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import java.util.List;

import com.datatrees.common.protocol.Constant;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.extractor.util.TextUrlExtractor;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:58:06 AM
 */
public class TextUrlExtractorTest extends BaseConfigTest {

    private static final String data = "bdhd://426626241|CFACFC1FEEFD51CC0B068C3A7C6622A0|超人：钢铁之躯TS中字.rmvb<a sid='8' href='//www.baidu.com/link?url=2TY#atxyY9pkL7Od-oJm4607gms3MAdrQDEdQfpelZXU69rjcyBub4SJn0uIbcLArWl1W4blMoZb2ONAsP0QLl_' title='百　度'>百　度</a></li><li>thunder://QUFodHRwOi8veGwuM3d3eXQuY29tLzAyMDdiLnJhclpa<a sid='17' href='http://tieba.baidu.com/' title='贴　吧'>贴　吧</a></li><li><a sid='9' href='http://i.firefoxchina.cn/redirect/google_rdr.html' title='Google'>Google</a></li><li><a sid='10' href='http://www.sina.com.cn/' title='新　浪'>新　浪</a></li><li><a sid='14' href='http://weibo.com/?c=spr_web_sq_firefox_weibo_t001' title='新浪微博'>新浪微博</a></li><li><a sid='343' href='http://www.qq.com/' title='腾　讯'>腾　讯</a></li><li><a sid='899' href='http://qzone.qq.com/' title='QQ 空 间'>QQ 空 间</a></li><li><a sid='342' href='http://www.163.com/' title='网　易'>网　易</a></li><li><a sid='344' href='http://www.ifeng.com/' title='凤 凰 网'>凤 凰 网</a></li><li><a sid='11' href='http://cps.youku.com/redirect.html?id=00000292' title='优 酷 网'>优 酷 网</a></li><li><a sid='12' href='http://www.iqiyi.com?src=firefoxhm' title='爱 奇 艺'>爱 奇 艺</a></li><li><a sid='15' href='http://renren.com/' title='人 人 网'>人 人 网</a></li><li><a sid='20' href='http://www.kaixin001.com/' title='开 心 网'>开 心 网</a></li><li><a sid='22' href='http://www.4399.com/' title='4 3 9 9'>4 3 9 9</a>";

    @Test
    public void testExtractUrl() {
        // String fileName = "aa.html";
        // String content = getContent(fileName);
        String content = getContent("page.html");
        String regex = Constant.URL_REGEX;
        long start = System.currentTimeMillis();
        List<String> urls = TextUrlExtractor.extractor(data, Constant.URL_REGEX, 1);
        System.out.println(urls.size());
        for (String url : urls) {
            System.out.println(url);
        }
        System.out.println("cost:---------------------" + (System.currentTimeMillis() - start) + "s");

        String content1 = getContent("page.html");
        start = System.currentTimeMillis();
        urls = TextUrlExtractor.extractor(content1, Constant.URL_REGEX, 1);
        System.out.println(urls.size());
        for (String url : urls) {
            System.out.println(url);
        }
        System.out.println("cost:---------------------" + (System.currentTimeMillis() - start) + "s");
    }
}
