package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.share.RedisService;
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

    @Resource
    private RedisService       redisService;

    @Override
    public HttpResult<Map<String, Object>> init(Long taskId, String websiteName, String type, OperatorParam param) {
        redisService.deleteKey(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId.toString()));
        redisService.deleteKey(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId.toString()));
        return getLoginService(websiteName).init(taskId, websiteName, param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(Long taskId, String websiteName, String type,
                                                         OperatorParam param) {
        return getLoginService(websiteName).refeshPicCode(taskId, websiteName, type, param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(Long taskId, String websiteName, String type,
                                                         OperatorParam param) {
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
        return getLoginService(websiteName).refeshSmsCode(taskId, websiteName, type, param);
    }

    @Override
    public HttpResult<Map<String, Object>> submit(Long taskId, String websiteName, String type, OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        if (null == taskId || taskId <= 0) {
            return result.failure(ErrorCode.EMPTY_TASK_ID);
        }
        if (StringUtils.isBlank(websiteName)) {
            return result.failure(ErrorCode.EMPTY_WEBSITE_NAME);
        }
        if (StringUtils.equals(FormType.LOGIN, type)) {
            if (null == param || null == param.getMobile() || 0 >= param.getMobile()) {
                return result.failure(ErrorCode.EMPTY_MOBILE);
            }
            if (StringUtils.isBlank(param.getPassword())) {
                return result.failure(ErrorCode.EMPTY_PASSWORD);
            }
        }
        result = getLoginService(websiteName).submit(taskId, websiteName, type, param);
        if (null != result && result.getStatus() && StringUtils.equals(FormType.LOGIN, type)) {
            redisService.addTaskShare(taskId, AttributeKey.WEBSITE_NAME, websiteName);
            redisService.addTaskShare(taskId, AttributeKey.MOBILE, param.getMobile().toString());
            redisService.addTaskShare(taskId, AttributeKey.PASSWORD, param.getPassword());
            if (StringUtils.isNoneBlank(param.getIdCard())) {
                redisService.addTaskShare(taskId, AttributeKey.ID_CARD, param.getPassword());
            }
            if (StringUtils.isNoneBlank(param.getRealName())) {
                redisService.addTaskShare(taskId, AttributeKey.REAL_NAME, param.getPassword());
            }
        }
        return result;
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllConfig() {
        return crawlerService.queryAllOperatorConfig();
    }

    private OperatorPluginService getLoginService(String websiteName) {
        return classLoaderService.getOperatorPluginService(websiteName);
    }
}
