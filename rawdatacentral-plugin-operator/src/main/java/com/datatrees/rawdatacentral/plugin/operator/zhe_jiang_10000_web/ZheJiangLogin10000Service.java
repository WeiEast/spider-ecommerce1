package com.datatrees.rawdatacentral.plugin.operator.zhe_jiang_10000_web;

import com.datatrees.crawler.plugin.util.PluginHttpUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.Charset;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

/**
 * 浙江10000登陆
// * 操作:登陆(http://www.189.cn/zj中国移动官方网站杭州)-->登陆网上营业厅(https://zj.ac.10086.cn/login)-->服务密码登陆
// * 跳转:登陆2次: 登陆一() --> 登陆二()
 *
 * 浙江移动掌上营业厅(http://wap.zj.10086.cn/szhy/index.html)
 * 登陆页:http://wap.zj.10086.cn/login_wap.jsp?jumpurl=http://wap.zj.10086.cn/szhy/my/index.html
 * http://wap.zj.10086.cn/login_wap.jsp?AISSO_LOGIN=true&jumpurl=http://wap.zj.10086.cn/new/authentication?uid=59&chId=1&nwId=wap&ul_nwid=wap&ul_scid=9c6d64&ul_loginclient=wap
 * 刷新图片验证码:https://zj.ac.10086.cn/ImgDisp?tmp=1500017879477
 *
 *
 *
 * Created by zhouxinghai on 2017/7/13.
 */
public class ZheJiangLogin10000Service implements OperatorLoginPluginService {

    private static final org.slf4j.Logger logger             = org.slf4j.LoggerFactory
        .getLogger(ZheJiangLogin10000Service.class);

    public static final String            picCodeUrlTemplate = "https://zj.ac.10086.cn/ImgDisp?tmp={}";

    //登陆模板
    public static final String            loginUrlTemplate   = "https://zj.ac.10086.cn/loginbox?backurl=&billId={}&continue=http://wap.zj.10086.cn/wapsimple/index.jsp&failurl=http://wap.zj.10086.cn/wapsimple/exception.jsp&loginUserType=1&passwd={}&pwdType=2&service=my&validCode={}";

    @Override
    public HttpResult<Map<String, Object>> init(Long taskId, String websiteName, OperatorParam param) {
        //什么都不用干,刷新图片验证码会拿cookie
        //图片验证码预加载
        //存储中间结果到redis
        return new HttpResult<Map<String, Object>>().success();
    }

    @Override
    public HttpResult<String> refeshPicCode(Long taskId, String websiteName, OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        String url = TemplateUtils.format(picCodeUrlTemplate, System.currentTimeMillis());
        try {
            byte[] data = PluginHttpUtils.doGet(url, taskId);
            String picCode = Base64.encodeBase64String(data);
            logger.info("refeshPicCode success taskId={}", taskId);
            return result.success(picCode);
        } catch (Exception e) {
            logger.error("refeshPicCode error taskId={},url={}", taskId, url);
            return result.failure();
        }
    }

    @Override
    public HttpResult<Boolean> refeshSmsCode(Long taskId, String websiteName, OperatorParam param) {
        return null;
    }

    @Override
    public HttpResult<Map<String, Object>> login(Long taskId, String websiteName, OperatorParam param) {
        try {
            Long mobile = param.getMobile();
            String password = param.getPassword();
            String picCode = param.getPicCode();
            //todo 校验

            String referer = "http://www.zj.10086.cn/my/UnifiedLoginboxServlet?AISSO_LOGIN=true";
            String url = TemplateUtils.format(loginUrlTemplate, mobile, password, picCode);
            String pageContent = PluginHttpUtils.postString(url, null, referer, taskId, Charset.GBK2312);

        } catch (Exception e) {
            //登陆失败,自动刷新图片验证码
            //refeshPicCode

        }
        return null;

    }
}
