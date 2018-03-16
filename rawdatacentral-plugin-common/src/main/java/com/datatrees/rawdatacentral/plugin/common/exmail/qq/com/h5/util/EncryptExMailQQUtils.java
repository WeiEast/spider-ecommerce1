package com.datatrees.rawdatacentral.plugin.common.exmail.qq.com.h5.util;

import org.apache.commons.io.IOUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;

/**
 * Created by zhangyanjia on 2018/2/27.
 */
public class EncryptExMailQQUtils {
    private static Invocable JsEngineInvocable_EXMAILQQSP = null;

    private static final String pubKey = "CF87D7B4C864F4842F1D337491A48FFF54B73A17300E8E42FA365420393AC0346AE55D8AFAD975DFA175FAF0106CBA81AF1DDE4ACEC284DAC6ED9A0D8FEB1CC070733C58213EFFED46529C54CEA06D774E3CC7E073346AEBD6C66FC973F299EB74738E400B22B1E7CDC54E71AED059D228DFEB5B29C530FF341502AE56DDCFE9";

    public static String getExMailQQSP(String publicTs, String password) throws Exception {
        if (JsEngineInvocable_EXMAILQQSP == null) {
            String jsStr;
            InputStream indepentStrStream = null;
            try {
                indepentStrStream = EncryptExMailQQUtils.class.getClassLoader().getResourceAsStream("js/exMailQQEncoder.js");
                jsStr = IOUtils.toString(indepentStrStream);
            } finally {
                IOUtils.closeQuietly(indepentStrStream);
            }
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("function getExMailQQSP(publicTs,pubKey,password){ var RSA = new RSAKey();RSA.setPublic(pubKey, \"10001\");var Res = RSA.encrypt(password + '\\n' + publicTs + '\\n');return hex2b64(Res)}");
            strBuilder.append(jsStr);
            JsEngineInvocable_EXMAILQQSP = loadJsEngineInvocable(strBuilder.toString());
        }
        return (String) JsEngineInvocable_EXMAILQQSP.invokeFunction("getExMailQQSP", new Object[]{publicTs, pubKey, password});
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

    public static void main(String[] args) throws Exception {
        String string = EncryptExMailQQUtils.getExMailQQSP("1519699932", "Qweds");
        System.out.println("-----" + string);
    }
}
