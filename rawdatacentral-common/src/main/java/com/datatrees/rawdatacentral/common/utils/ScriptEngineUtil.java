package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.io.IOUtils;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;

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
        String javaScrit = IOUtils.toString(inputStream, charsetName);
        IOUtils.closeQuietly(inputStream);
        CheckUtils.checkNotBlank(javaScrit,"empty javaScrit");
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(javaScrit);
        return (Invocable) scriptEngine;
    }

    /**
     * 创建js引擎
     * @param inputStream 文件流,默认编码UTF-8
     * @return
     * @throws Exception
     */
    public static Invocable createInvocable(InputStream inputStream) throws Exception {
       return createInvocable(inputStream,"UTF-8");
    }
}
