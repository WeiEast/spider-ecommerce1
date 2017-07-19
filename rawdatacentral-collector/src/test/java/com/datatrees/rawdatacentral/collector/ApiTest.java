package com.datatrees.rawdatacentral.collector;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * Created by zhouxinghai on 2017/6/23
 */
public class ApiTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Resource
    private CrawlerService      crawlerService;

    @Test
    public void testQueryAllOperatorConfig() throws Exception {
        HttpResult<List<OperatorCatalogue>> result = crawlerService.queryAllOperatorConfig();
        List<OperatorCatalogue> data = result.getData();
        logger.info("data={}", JSON.toJSONString(data));
    }

}
