/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.plugin.sina.com.h5;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

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
        return (String) JsEngineInvocable_SINASU.invokeFunction("getSinaSU", new Object[]{mailAccount});
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
        return (String) JsEngineInvocable_SINASP.invokeFunction("getSinaSP", new Object[]{serverTime, nonce, pubKey, password});
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
