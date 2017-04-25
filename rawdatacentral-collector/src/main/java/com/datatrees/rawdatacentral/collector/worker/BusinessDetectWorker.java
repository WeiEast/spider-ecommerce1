package com.datatrees.rawdatacentral.collector.worker;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.collector.listener.message.BusinessDetectMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.core.common.UniqueKeyGenUtil;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.core.service.TaskService;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年5月11日 下午4:28:22
 */
@Service
public class BusinessDetectWorker {
    private static final Logger log = LoggerFactory.getLogger(BusinessDetectWorker.class);
    @SuppressWarnings("unchecked")
    Map<String, Integer> businessTypeUserMapping = (Map<String, Integer>) GsonUtils.fromJson(
            PropertiesConfiguration.getInstance().get("business.type.user.mapping", "{'opinionDetect':2,'webDetect':3,'businessLicense':4,'p2pBlack':5}"),
            new TypeToken<Map<String, Integer>>() {}.getType());

    @Resource
    private Collector collector;
    @Resource
    private TaskService taskService;

    public void process(BusinessDetectMessage message) {
        CollectorMessage collectorMessage = getCollectorMessage(message);
        if (collectorMessage != null) {
            collector.processMessage(collectorMessage);
        }
    }

    private CollectorMessage getCollectorMessage(BusinessDetectMessage message) {
        CollectorMessage collectorMessage = new CollectorMessage();
        // 爬网查QQ空间
//        if ("qq.qzone.com".equals(message.getWebsiteName())) {
//            Integer userid = message.getUserId();
//            Task task = taskService.selectTaskByBankBillsKey(userid, message.getQqAccount());
//            if (task != null && StringUtils.isNotBlank(task.getResultMessage())) {
//                String startMsg = StringUtils.defaultString(PatternUtils.group(task.getResultMessage(), "startMsg\":(.*)", 1), "");
//                Map<String, Object> map = (Map) GsonUtils.fromJson(startMsg, new TypeToken<Map<String, Object>>() {}.getType());
//                collectorMessage.setEndURL(MapUtils.getString(map, "endURL"));
//                collectorMessage.setCookie(MapUtils.getString(map, "cookie"));
//            } else {
//                log.warn("can't find task with userid:{} ,qqAccount:{}", userid, message.getQqAccount());
//            }
//        }

        Integer userid = businessTypeUserMapping.get(message.getBusinessType());
        if (message.getUserId() != null) {
            userid = message.getUserId();
        }
        if (userid != null) {
            collectorMessage.setUserId(userid);
            collectorMessage.setFinish(false);
            // collectorMessage.setLoginCheckIgnore(true);
            collectorMessage.setWebsiteName(message.getWebsiteName());
            collectorMessage.setSerialNum(UniqueKeyGenUtil.uniqueKeyGen(null));// unique sign
            Map<String, Object> propertys = new HashMap<String, Object>();
            propertys.put("businessType", message.getBusinessType());
            propertys.put("keyword", message.getKeyword());
            propertys.put("entryName", message.getEntryName());
            propertys.put("containKey", message.getContainKey());
            // 发回消息体
            collectorMessage.getSendBack().put("guid", message.getGuid());
            propertys.put("guid", message.getGuid());
            // 当没有的DisContainKey，以‘_DisContainKey’ 为默认值
            String disContainKey = StringUtils.isEmpty(message.getDisContainKey()) ? "_DisContainKey" : message.getDisContainKey();
            propertys.put("disContainKey", disContainKey);
            collectorMessage.setProperty(propertys);
        } else {
            log.warn("no such businessType" + message.getBusinessType());
        }
        return collectorMessage;

    }
}
