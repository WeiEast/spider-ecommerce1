package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.common.utils.BooleanUtils;
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
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
@Service
public class CrawlerOperatorServiceImpl implements CrawlerOperatorService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CrawlerOperatorServiceImpl.class);

    @Resource
    private CrawlerService                crawlerService;

    @Resource
    private ClassLoaderService            classLoaderService;

    @Resource
    private RedisService                  redisService;

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
            //清理共享信息
            redisService.deleteKey(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId()));
            redisService.deleteKey(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(param.getTaskId()));
            //保存mobile和websiteName
            redisService.addTaskShare(param.getTaskId(), AttributeKey.MOBILE, param.getMobile().toString());
            redisService.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());
        }
        return getLoginService(param).init(param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        return getLoginService(param).refeshPicCode(param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        return getLoginService(param).refeshSmsCode(param);
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        if (null != param && null != param.getTaskId()) {
            redisService.removeTaskShare(param.getTaskId(), AttributeKey.LOGIN_PIC_CODE);
        }
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (StringUtils.isBlank(param.getPicCode())) {
            return result.failure(ErrorCode.EMPTY_PIC_CODE);
        }
        result = getLoginService(param).validatePicCode(param);
        if (result.getStatus()) {
            switch (param.getFormType()) {
                case FormType.LOGIN:
                    redisService.addTaskShare(param.getTaskId(), AttributeKey.LOGIN_PIC_CODE, param.getPicCode());
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        result = getLoginService(param).submit(param);
        if (null != result && result.getStatus() && StringUtils.equals(FormType.LOGIN, param.getFormType())) {
            if (StringUtils.isNoneBlank(param.getPassword())) {
                redisService.addTaskShare(param.getTaskId(), AttributeKey.PASSWORD, param.getPassword());
            }
            if (StringUtils.isNoneBlank(param.getIdCard())) {
                redisService.addTaskShare(param.getTaskId(), AttributeKey.ID_CARD, param.getPassword());
            }
            if (StringUtils.isNoneBlank(param.getRealName())) {
                redisService.addTaskShare(param.getTaskId(), AttributeKey.REAL_NAME, param.getPassword());
            }
        }
        return result;
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllConfig() {
        return crawlerService.queryAllOperatorConfig();
    }

    private OperatorPluginService getLoginService(OperatorParam param) {
        return classLoaderService.getOperatorPluginService(param.getWebsiteName());
    }

    /**
     * 从前面的共享的信息中提取参数
     * @param param
     * @return
     */
    private void fillParamFromShares(OperatorParam param) {
        Map<String, String> map = redisService.getTaskShares(param.getTaskId());
        if (null != map) {
            if (StringUtils.isBlank(param.getWebsiteName()) && map.containsKey(AttributeKey.WEBSITE_NAME)) {
                param.setWebsiteName(map.get(AttributeKey.WEBSITE_NAME));
            }
            if (null == param.getMobile() && map.containsKey(AttributeKey.MOBILE)) {
                param.setMobile(Long.valueOf(map.get(AttributeKey.MOBILE)));
            }
            if (StringUtils.isBlank(param.getPassword()) && map.containsKey(AttributeKey.PASSWORD)) {
                param.setPassword(map.get(AttributeKey.PASSWORD));
            }
            if (StringUtils.isBlank(param.getIdCard()) && map.containsKey(AttributeKey.ID_CARD)) {
                param.setIdCard(map.get(AttributeKey.ID_CARD));
            }
            if (StringUtils.isBlank(param.getRealName()) && map.containsKey(AttributeKey.REAL_NAME)) {
                param.setRealName(map.get(AttributeKey.REAL_NAME));
            }

            if (StringUtils.equals(FormType.LOGIN, param.getFormType()) && StringUtils.isBlank(param.getPicCode())
                && map.containsKey(AttributeKey.LOGIN_PIC_CODE)) {
                param.setPicCode(map.get(AttributeKey.LOGIN_PIC_CODE));
            }
        }
    }

    /**
     * 校验基本参数
     * taskId,websiteName,mobile
     * @param param
     * @return
     */
    private HttpResult<Map<String, Object>> checkParams(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        if (null == param || BooleanUtils.isNotPositiveNumber(param.getTaskId())) {
            return result.failure(ErrorCode.EMPTY_TASK_ID);
        }
        if (StringUtils.isBlank(param.getFormType())) {
            return result.failure(ErrorCode.EMPTY_FORM_TYPE);
        }
        fillParamFromShares(param);
        if (StringUtils.isBlank(param.getWebsiteName())) {
            return result.failure(ErrorCode.EMPTY_WEBSITE_NAME);
        }
        if (BooleanUtils.isNotPositiveNumber(param.getMobile())) {
            return result.failure(ErrorCode.EMPTY_MOBILE);
        }
        return result.success();
    }
}
