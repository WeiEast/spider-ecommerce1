package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * js引擎
 */
public class ScriptEngineUtil {

    /**
     * 创建js引擎
     * @param inputStream 文件流
     * @param charsetName 源文件编码
     * @return
     * @throws Exception
     */
    public static Invocable createInvocable(InputStream inputStream, String charsetName) throws Exception {
        CheckUtils.checkNotBlank(charsetName, "empty charsetName");
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(new InputStreamReader(inputStream, charsetName));
        return (Invocable) scriptEngine;
    }

}
