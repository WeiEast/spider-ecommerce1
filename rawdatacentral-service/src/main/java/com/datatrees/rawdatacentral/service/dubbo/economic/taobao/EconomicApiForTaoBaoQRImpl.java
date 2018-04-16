package com.datatrees.rawdatacentral.service.dubbo.economic.taobao;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.plugin.qrcode.QRCodeVerification;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.dubbo.economic.taobao.util.QRUtils;
import com.treefinance.proxy.domain.IpLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by guimeichao on 18/1/11.
 */
@Service
public class EconomicApiForTaoBaoQRImpl implements EconomicApiForTaoBaoQR {

    @Resource
    private CommonPluginApi commonPluginApi;

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.TAOBAO_COM_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginApi.refeshQRCode(param);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.TAOBAO_COM_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginApi.queryQRStatus(param);
    }
}
