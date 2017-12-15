package com.datatrees.rawdatacentral.plugin.education;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.HttpUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.Sign;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssServiceProvider;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssUtils;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangyanjia on 2017/12/13.
 */
public class EducationPlugin extends AbstractClientPlugin {
    private MonitorService monitorService;
    private AbstractProcessorContext context;
    private final static String TX_GENERAL_URL = "http://recognition.image.myqcloud.com/ocr/general";
    private final static String appid ;
    private final static String bucket;
    private final static String secretid;
    private final static String secretkey;
    private final static String HOST = "recognition.image.myqcloud.com";

    static {
        Configuration configuration = PropertiesConfiguration.getInstance();
        secretid = configuration.get("education_secretid");
        secretkey=configuration.get("education_secretkey");
        bucket=configuration.get("education_bucket");
        appid= configuration.get("education_appid");
    }

    private final static Logger logger= LoggerFactory.getLogger(EducationPlugin.class);

    @Override
    public String process(String... args) throws Exception {
        context = PluginFactory.getProcessorContext();
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);

        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);

        TaskUtils.updateCookies(taskId, ProcessorContextUtil.getCookieMap(context));

        TaskUtils.initTaskContext(taskId, context.getContext());
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils.fromJson(args[0], new TypeToken<LinkedHashMap<String, String>>() {
        }.getType());
        String url = paramMap.get("page_content");
        String string = handlePic(url, taskId, websiteName);
        Map<String, Object> pluginResult = new HashMap<>();
        pluginResult.put(PluginConstants.FIELD, string);
        return JSON.toJSONString(pluginResult);
    }

    private String handlePic(String url, Long taskId, String websiteName) {
        Response response;
        try {

            response = TaskHttpClient.create(taskId, websiteName, RequestType.GET, "chsi_com_cn_pic").setFullUrl(url).invoke();
            byte[] pageContent = response.getResponse();
            int i=(int)(Math.random()*100000);
            String path="education/"+websiteName+"/"+taskId+"/"+i;
            OssServiceProvider.getDefaultService()
                    .putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, OssUtils.getObjectKey(path), pageContent);
            logger.info("学信网图片上传oss成功！path={}",path);
            Map<String, String> map = new HashMap<>();
            String authorization = RedisUtils.get("authorization");
            if (authorization == null) {
                Long appId=Long.parseLong(appid);
                //authorization的有效期为81天
                authorization = Sign.appSign(appId, secretid, secretkey, bucket, 6998400L);
                //存redis存80天
                RedisUtils.set("authorization", authorization, 6912000);
            }
            map.put("Authorization", authorization);
            map.put("Host", HOST);
            String fileName = taskId + ".jpg";
            String imageResult = HttpUtils.doPostForImage(TX_GENERAL_URL, map, appid, bucket, pageContent, fileName);
            if (imageResult != null) {
                return imageResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
