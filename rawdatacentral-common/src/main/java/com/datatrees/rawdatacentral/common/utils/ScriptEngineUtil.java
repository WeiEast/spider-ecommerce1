package com.datatrees.rawdatacentral.common.utils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Base64;

import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * js引擎
 */
public class ScriptEngineUtil {

    private static final Logger logger          = LoggerFactory.getLogger(ScriptEngineUtil.class);

    private static final String DEFAULT_CHARSET = "UTF-8";

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

    public static Invocable createInvocable(String websiteName, String fileName, String charsetName) throws Exception {
        if (StringUtils.isAnyBlank(websiteName, fileName)) {
            return null;
        }
        String sassEnv = TaskUtils.getSassEnv();
        String pluginName = websiteName + "." + fileName;
        String version = RedisUtils.hget(RedisKeyPrefixEnum.PLUGIN_VERSION.getRedisKey(sassEnv), pluginName);
        byte[] bytes = RedisUtils.hgetForByte(RedisKeyPrefixEnum.PLUGIN_DATA.getRedisKey(sassEnv), pluginName);
        logger.info("load plugin sassEnv:{},pluginName:{},version:{}", sassEnv, pluginName, version);
        if (null == charsetName) {
            charsetName = DEFAULT_CHARSET;
        }
        String javaScrit = new String(bytes, charsetName);
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(javaScrit);
        return (Invocable) scriptEngine;
    }

}
