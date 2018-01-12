package com.datatrees.rawdatacentral.service.dubbo.economic.taobao;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.dubbo.economic.taobao.util.QRCodeStatus;
import com.datatrees.rawdatacentral.service.dubbo.economic.taobao.util.QRUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by guimeichao on 18/1/11.
 */
@Service
public class EconomicApiForTaoBaoQRImpl implements EconomicApiForTaoBaoQR {

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login.do?adUrl=&adImage=&adText=&viewFd4PC=&viewFd4Mobile=" +
                    "&from=tb&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "")
                    .setFullUrl(templateUrl, System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).invoke();
            String jsonString = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(jsonString);
            String imgUrl = json.getString("url");
            String lgToken = json.getString("lgToken");
            TaskUtils.addTaskShare(param.getTaskId(), "lgToken", lgToken);
            if (!StringUtils.contains(imgUrl, "https:")) {
                imgUrl = "https:" + imgUrl;
            }
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "").setFullUrl(imgUrl).invoke();
            byte[] bytes = response.getResponse();
            String qrBase64 = response.getPageContentForBase64();
            QRUtils qrUtils = new QRUtils();
            String qrText = qrUtils.parseCode(bytes);
            Map<String,String> dataMap = new HashMap<>();
            dataMap.put("qrBase64",qrBase64);
            dataMap.put("qrText",qrText);
            return result.success(dataMap);
        } catch (Exception e) {

        }

        return result;
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String lgToken = TaskUtils.getTaskShare(param.getTaskId(), "lgToken");
            String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken={}&defaulturl=&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "")
                    .setFullUrl(templateUrl, lgToken,System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).invoke();
            String resultJson = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(resultJson);
            String code = json.getString("code");
            Map<String,String> dataMap = new HashMap<>();
            switch (code) {
                case "10000":
                    return result.success(QRCodeStatus.QR_CODE_STATUS_WATING);
                case "10001":
                    return result.success(QRCodeStatus.QR_CODE_STATUS_READY);
                case "10004":
                    return result.success(QRCodeStatus.QR_CODE_STATUS_EXPIRE);
                case "10006":

                    return result.success(QRCodeStatus.QR_CODE_STATUS_SUCCESS);
                default:
                    return result.failure(QRCodeStatus.QR_CODE_STATUS_FAIL.getStatusMessage());

            }

        } catch (Exception e) {

        }

        return result;
    }
}
