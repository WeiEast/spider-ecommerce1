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

package com.treefinance.crawler.plugin.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * js引擎
 * User: yand
 * Date: 2018/3/6
 */
public class ScriptEngineUtil {

    /**
     * 创建js引擎
     * @param javaScrit 文件流
     * @return
     * @exception Exception
     */
    public static Invocable createInvocable(String javaScrit) throws Exception {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(javaScrit);
        return (Invocable) scriptEngine;
    }

    public static Invocable createInvocable(InputStreamReader reader) throws Exception {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(reader);
        return (Invocable) scriptEngine;
    }

    public static Object evalScript(String jsfile, String function, Object... args) throws Exception {
        try (InputStream inputStream = ScriptEngineUtil.class.getClassLoader().getResourceAsStream(jsfile);
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            Invocable invocable = createInvocable(reader);
            return invocable.invokeFunction(function, args);
        }
    }
}
