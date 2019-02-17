/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5;

import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.toolkit.util.io.Streams;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author Jerry
 * @date 2019-02-14 19:48
 */
public final class JavaScriptEngine {
    private static ScriptEngine engine;
    private static String isgJS;
    private static final Object ISG_LOCK = new Object();

    private JavaScriptEngine() {}

    private static ScriptEngine getScriptEngine() {
        if (engine == null) {
            synchronized (JavaScriptEngine.class) {
                ScriptEngineManager sem = new ScriptEngineManager();
                engine = sem.getEngineByName("javascript");
            }
        }
        return engine;
    }

    public static String generateIsg() {
        String js = getIsgJs();

        try {
            return (String)getScriptEngine().eval(js);
        } catch (ScriptException e) {
            throw new UnexpectedException("Error eval javascript \"taobao.login.isg.js\"!", e);
        }
    }

    private static String getIsgJs() {
        if (isgJS == null) {
            synchronized (ISG_LOCK) {
                try (InputStream inputStream = Objects.requireNonNull(JavaScriptEngine.class.getClassLoader().getResourceAsStream("js/taobao.login.isg.js"))) {
                    isgJS = Streams.readToString(inputStream, Charset.defaultCharset());
                } catch (IOException e) {
                    throw new UnexpectedException("Error reading javascript file \"taobao.login.isg.js\"!", e);
                }
            }
        }
        return isgJS;
    }
}
