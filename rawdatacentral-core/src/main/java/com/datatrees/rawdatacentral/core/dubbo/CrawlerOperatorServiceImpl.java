package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
@Service
public class CrawlerOperatorServiceImpl implements CrawlerOperatorService {

    @Resource
    private CrawlerService     crawlerService;

    @Resource
    private ClassLoaderService classLoaderService;

    @Override
    public HttpResult<Map<String, Object>> init(Long taskId, String websiteName, OperatorParam param) {
        return getLoginService(websiteName).init(taskId, websiteName, param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(Long taskId, String websiteName, OperatorParam param) {
        return getLoginService(websiteName).refeshPicCode(taskId, websiteName, param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(Long taskId, String websiteName, OperatorParam param) {
        return getLoginService(websiteName).refeshSmsCode(taskId, websiteName, param);
    }

    @Override
    public HttpResult<Map<String, Object>> login(Long taskId, String websiteName, OperatorParam param) {
        return getLoginService(websiteName).login(taskId, websiteName, param);
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllConfig() {
        return crawlerService.queryAllOperatorConfig();
    }

    private OperatorLoginPluginService getLoginService(String websiteName) {
        return classLoaderService.getOperatorLongService(websiteName);
    }
}
