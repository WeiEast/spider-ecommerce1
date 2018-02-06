package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.io.IOUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;

/**
 * Created by zhangyanjia on 2018/1/29.
 */
public class EncryptSinaUtils {
    private static Invocable JsEngineInvocable_SINASU = null;
    private static Invocable JsEngineInvocable_SINASP = null;

    public static String getSinaSU(String mailAccount) throws Exception {
        if (JsEngineInvocable_SINASU == null) {
            String jsStr;
            InputStream indepentStrStream = null;
            try {
                indepentStrStream = EncryptSinaUtils.class.getClassLoader().getResourceAsStream("js/sinaSSOEncoder.js");
                jsStr = IOUtils.toString(indepentStrStream);
            } finally {
                IOUtils.closeQuietly(indepentStrStream);
            }
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(" function getSinaSU(username){ return sinaSSOEncoder.base64.encode(encodeURIComponent(username)); }");
            strBuilder.append(jsStr);
            JsEngineInvocable_SINASU = loadJsEngineInvocable(strBuilder.toString());
        }
        return (String) JsEngineInvocable_SINASU.invokeFunction("getSinaSU", new Object[] {mailAccount});
    }

    public static String getSinaSP(String serverTime, String nonce, String pubKey, String password) throws Exception {
        if (JsEngineInvocable_SINASP == null) {
            String jsStr;
            InputStream indepentStrStream = null;
            try {
                indepentStrStream = EncryptSinaUtils.class.getClassLoader().getResourceAsStream("js/sinaSSOEncoder.js");
                jsStr = IOUtils.toString(indepentStrStream);
            } finally {
                IOUtils.closeQuietly(indepentStrStream);
            }
            StringBuilder strBuilder = new StringBuilder();
            strBuilder
                    .append(" function getSinaSP(serverTime,nonce,pubKey,password){  var RSAKey = new sinaSSOEncoder.RSAKey();RSAKey.setPublic(pubKey, \"10001\");return RSAKey.encrypt([serverTime, nonce].join(\"\\t\") + \"\\n\" + password);  }");
            strBuilder.append(jsStr);
            JsEngineInvocable_SINASP = loadJsEngineInvocable(strBuilder.toString());
        }
        return (String) JsEngineInvocable_SINASP.invokeFunction("getSinaSP", new Object[] {serverTime, nonce, pubKey, password});
    }

    private static Invocable loadJsEngineInvocable(String scriptHtml) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("var navigator={appName:'Microsoft Internet Explorer'};var window={};\r\n");
        sb.append(scriptHtml);
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        engine.eval(sb.toString());
        return (Invocable) engine;
    }
}
