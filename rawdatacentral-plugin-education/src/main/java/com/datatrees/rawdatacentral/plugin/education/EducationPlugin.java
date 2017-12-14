package com.datatrees.rawdatacentral.plugin.education;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.HttpUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.common.utils.Sign;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangyanjia on 2017/12/13.
 */
public class EducationPlugin extends AbstractClientPlugin {
    private MonitorService monitorService;
    private AbstractProcessorContext context;
    private final static String TX_GENERAL_URL="http://recognition.image.myqcloud.com/ocr/general";
    private final static Long APPID=1255662428L;
    private final static String BUCKET="zyjtest1";
    private final static String SECRETID="AKIDhHk3A5hN2Zu3IPC2X7ZUDj3BBzb0jE5G";
    private final static String SECRETKEY="7b6Wow8LckgQ1RfmmaCcrvscAVovugJy";
    private final static String HOST="recognition.image.myqcloud.com";
    @Override
    public String process(String... args) throws Exception{
        context = PluginFactory.getProcessorContext();
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);

        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);

        TaskUtils.updateCookies(taskId, ProcessorContextUtil.getCookieMap(context));

        TaskUtils.initTaskContext(taskId, context.getContext());
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils.fromJson(args[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String url=paramMap.get("page_content");
        return handlePic(url,taskId,websiteName);
    }

    private String handlePic(String url,Long taskId,String websiteName) {
        Response response;
        try{

            response= TaskHttpClient.create(taskId,websiteName, RequestType.GET,"chsi_com_cn_pic").setFullUrl(url).invoke();
            byte[] pageContent=response.getResponse();
//            String path="education/"+websiteName+"/"+taskId;
//            OssServiceProvider.getDefaultService()
//                    .putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, OssUtils.getObjectKey(path), pageContent);
            Map<String,String> map=new HashMap<>();
            String authorization= Sign.appSign(APPID,SECRETID,SECRETKEY,BUCKET,2592000L);
            map.put("Authorization",authorization);
            map.put("Host",HOST);
            String fileName=taskId+".jpg";
            String imageResult=HttpUtils.doPostForImage(TX_GENERAL_URL,map,APPID.toString(),BUCKET,pageContent,fileName);
            if(imageResult!=null) {
                return imageResult;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
