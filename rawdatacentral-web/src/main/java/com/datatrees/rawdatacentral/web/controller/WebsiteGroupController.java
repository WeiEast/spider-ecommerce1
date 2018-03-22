package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.WebsiteType;
import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权重
 * Created by zhouxinghai on 2017/8/31
 */
@RestController
@RequestMapping("/website/group")
public class WebsiteGroupController {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteGroupController.class);
    @Resource
    private WebsiteGroupService    websiteGroupService;
    @Resource
    private WebsiteOperatorService websiteOperatorService;

    /**
     * 配置权重
     * 格式:{
     * "groupCode":"CHINA_10000",
     * "config":{"china_10000_app":200}
     * }
     * @param map
     * @return
     */
    @RequestMapping("/configGroup")
    public HttpResult<Object> configGroup(@RequestBody Map<String, Object> map) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            String groupCode = (String) map.get("groupCode");
            Map<String, Integer> config = (LinkedHashMap<String, Integer>) map.get("config");
            List<WebsiteGroup> list = websiteGroupService.configGroup(groupCode, config);
            logger.info("configGroup success config={}", JSON.toJSONString(map));
            return result.success(list);
        } catch (Exception e) {
            logger.error("configGroup error config={}", JSON.toJSONString(map), e);
            return result.failure();
        }
    }

    /**
     * 删除权重配置
     * @param groupCode
     * @return
     */
    @RequestMapping("/deleteGroup")
    public HttpResult<Object> deleteGroupConfig(String groupCode) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteGroupService.deleteByGroupCode(groupCode);
            logger.info("deleteGroupConfig success groupCode={}", groupCode);
            return result.success(true);
        } catch (Exception e) {
            logger.error("deleteGroupConfig error groupCode={}", groupCode, e);
            return result.failure();
        }
    }

    /**
     * 统计运营商使用版本情况
     * @return
     */
    @RequestMapping("/statistics")
    public Object statistics() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("有本地-->没有使用", new ArrayList<>());
        map.put("有本地-->使用全国版", new ArrayList<>());
        map.put("有本地-->使用本地版", new ArrayList<>());

        map.put("无本地-->使用全国版", new ArrayList<>());
        map.put("无本地-->使用老版", new ArrayList<>());
        map.put("未识别", new ArrayList<>());

        for (GroupEnum group : GroupEnum.values()) {
            if (group.getWebsiteType() != WebsiteType.OPERATOR) {
                continue;
            }
            if (group == GroupEnum.CHINA_10086 || group == GroupEnum.CHINA_10000 || group == GroupEnum.CHINA_10010) {
                continue;
            }
            String maxWeightWebsiteName = RedisUtils.get(RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR.getRedisKey(group.getGroupCode()));
            String env= TaskUtils.getSassEnv();
            List<WebsiteOperator> operators = websiteOperatorService.queryByGroupCodeAndEnv(group.getGroupCode(),env);

            String template = "{}({})";
            if (CollectionUtils.isEmpty(operators)) {
                //无本地
                if (StringUtils.isBlank(maxWeightWebsiteName)) {
                    map.get("无本地-->使用老版").add(TemplateUtils.format(template, group.getGroupName(), group.getWebsiteName()));
                } else if (StringUtils.contains(maxWeightWebsiteName, "china")) {
                    map.get("无本地-->使用全国版").add(TemplateUtils.format(template, group.getGroupName(), maxWeightWebsiteName));
                } else {
                    map.get("未识别").add(TemplateUtils.format(template, group.getGroupName(), maxWeightWebsiteName));
                }
            } else {
                WebsiteOperator operator = operators.get(0);
                //有本地
                if (StringUtils.isBlank(maxWeightWebsiteName)) {
                    map.get("有本地-->没有使用").add(TemplateUtils.format(template, group.getGroupName(), operator.getWebsiteName()));
                } else {
                    if (StringUtils.equals(maxWeightWebsiteName, operator.getWebsiteName())) {
                        map.get("有本地-->使用本地版").add(TemplateUtils.format(template, group.getGroupName(), operator.getWebsiteName()));
                    } else if (StringUtils.contains(maxWeightWebsiteName, "china")) {
                        map.get("有本地-->使用全国版").add(TemplateUtils.format(template, group.getGroupName(), maxWeightWebsiteName));
                    } else {
                        map.get("未识别").add(TemplateUtils.format(template, group.getGroupName(), maxWeightWebsiteName));
                    }
                }
            }
        }
        return map;
    }

}
