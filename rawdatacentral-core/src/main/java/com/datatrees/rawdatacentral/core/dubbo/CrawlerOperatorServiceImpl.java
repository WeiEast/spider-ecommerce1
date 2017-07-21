package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import org.apache.commons.lang3.StringUtils;
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
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        if (null == taskId || taskId <= 0) {
            return result.failure(ErrorCode.EMPTY_TASK_ID);
        }
        if (StringUtils.isBlank(websiteName)) {
            return result.failure(ErrorCode.EMPTY_WEBSITE_NAME);
        }
        if (null == param || null == param.getMobile() || 0 >= param.getMobile()) {
            return result.failure(ErrorCode.EMPTY_MOBILE);
        }
        return getLoginService(websiteName).refeshSmsCode(taskId, websiteName, param);
    }

    @Override
    public HttpResult<Map<String, Object>> login(Long taskId, String websiteName, OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        if (null == taskId || taskId <= 0) {
            return result.failure(ErrorCode.EMPTY_TASK_ID);
        }
        if (StringUtils.isBlank(websiteName)) {
            return result.failure(ErrorCode.EMPTY_WEBSITE_NAME);
        }
        if (null == param || null == param.getMobile() || 0 >= param.getMobile()) {
            return result.failure(ErrorCode.EMPTY_MOBILE);
        }
        return getLoginService(websiteName).login(taskId, websiteName, param);
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllConfig() {
        return crawlerService.queryAllOperatorConfig();
    }

    private OperatorLoginPluginService getLoginService(String websiteName) {
        return classLoaderService.getOperatorPluginService(websiteName);
    }
}
