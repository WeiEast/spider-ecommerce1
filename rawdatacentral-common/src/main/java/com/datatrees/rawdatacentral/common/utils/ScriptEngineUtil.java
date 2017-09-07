package com.datatrees.rawdatacentral.common.utils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * js引擎
 */
public class ScriptEngineUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScriptEngineUtil.class);

    /**
     * 创建js引擎
     * @param javaScrit 文件流
     * @return
     * @exception Exception
     */
    public static Invocable createInvocableFromBase64(String javaScrit) throws Exception {
        CheckUtils.checkNotBlank(javaScrit, "empty base64");
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(new String(Base64.getDecoder().decode(javaScrit)));
        return (Invocable) scriptEngine;
    }

}
