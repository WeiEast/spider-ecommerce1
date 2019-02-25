/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
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

import com.treefinance.toolkit.util.Base64Codec;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * @author Jerry
 * @date 2019-02-25 18:34
 */
public class TopApiTest {
    /**
     * cookie.cna值来自该脚本的ETag值
     */
    private static final String CNA_URL = "https://log.mmstat.com/eg.js";

    private static final Pattern ETAG_PATTERN = Pattern.compile("goldlog.Etag=\"((.*)+?)\"");
    @Test
    public void getUmData() throws Exception {
//        String pageContent = HttpService.get(CNA_URL);
//        Matcher matcher = ETAG_PATTERN.matcher(pageContent);
//        String cna = null;
//        if (matcher.find()) {
//            cna = matcher.group(1);
//        }
//
//        if (StringUtils.isEmpty(cna)) {
//            throw new UnexpectedException("CNA值获取失败！请求eg.js结果 ");
//        }
        System.out.println(TopApi.getUmData("BtftFA6670wCAXrpv71Zqbv6"));
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String text = "eyJ4diI6IjMuMy43IiwieHQiOiJDMTU1MTA5MTIxMjE0OTYxNDQ2NTc3MzMzMTU1MTA5MTIxMjE0OTY0NSIsImV0ZiI6InUiLCJ4YSI6InRhb2Jhb19tbG9naW4iLCJzaXRlSWQiOiIiLCJ1aWQiOiIiLCJlbWwiOiJBQSIsImV0aWQiOiIiLCJlc2lkIjoiIiwidHlwZSI6InBjIiwibmNlIjp0cnVlLCJwbGF0IjoiTWFjSW50ZWwiLCJuYWNuIjoiTW96aWxsYSIsIm5hbiI6Ik5ldHNjYXBlIiwibmxnIjoiemgtQ04iLCJzdyI6MTI4MCwic2giOjgwMCwic2F3IjoxMjgwLCJzYWgiOjc3NywiYnN3Ijo2NDAsImJzaCI6NzAxLCJlbG9jIjoiaHR0cHMlM0ElMkYlMkZsb2dpbi5tLnRhb2Jhby5jb20lMkZtc2dfbG9naW4uaHRtIiwiZXR6Ijo0ODAsImV0dCI6MTU1MTA5MTIxMjE2MiwiZWNuIjoiZGIxZWI0ZjJmOGI0MDNjZTBmMmQyMzZlOTkyNGY0MTg2NGU5ZTBkMyIsImVjYSI6IkM3VDZGR3RvRVhBQ0FYcmdZOUtmV1ptaiIsImVyZCI6ImRlZmF1bHQsZTQzYjZlMGQ1N2FlZjQzYmMzMDg3ZDU0MzcyMDRhMWQ4ODI5OWU5ODU2YTEzYjMwNWFmMjQwOGExMmIxMTQwYyxhYzBiNjgxOTBiNjJlNWYzN2IyYTI4YTQyNDg0NWU0ZmIwMjQ1N2UyZTJlNTViNDVhMzhlM2ExN2Y1Yjc4YTA3LGUwYjcxMjQ0ZTUwNmM2NGRmM2FiNjFiYjI5NjM5YmQ1OWZkNmM1MDMzMDk1ZTZlMjZkOGNjZWFiMjgzZjFhNTgsZGVmYXVsdCxhYzNjZGYyYzE2ODA2NDkwMzE0YTMwNjQyOTJkNjlhYzNkNmU5NWQ3Mzk2ODhiOWFjYzM5OWIwMzBkN2U2ODg5LDBmYjM4YWVlZGUwNTFhYmQ3MzcyYWY4Njc5NDhkMDMxNGQ1MTExNzliMGRmNTU5OTE0MGQ5MDhkNTMwZGM2MjQsODI0NmQwM2RlZDllZWM2ZjJkZjQ1NDczMThmNDcyOWJjN2Y1YTQ2OWIyNzc3ZmQ2NDMwYzVlMTU1ZTMyODJlYiw1MjMxNTY1NGU3YjNhMTZkMGRmYjA0NjYwZGZjOGNlMTM0OTNmOTY4MGNkMGUyNTc5ZTdiODNmZjE1ZjhmMGEwIiwieGgiOiIiLCJpcHMiOiIiLCJlcGwiOjMsImVwIjoiMmZiZjRhMGQzNDIxNGQ0ZmRlNmNjOGEyMjg5N2QxMTVhNzY2NzgxMSIsImVwbHMiOiJDMzcwYzMwN2Y0YWNhNzg1ODQ5M2RmZTMyMjI1NGU1Y2I0MzhiZTk0NCxOMGZjZDZlMThmZjZkZjc0Zjk4YTY5OGI3ZjZiNmQ4MzhhNmMxMWU2OSIsImVzbCI6ZmFsc2V9";
        System.out.println(new String(Base64Codec.decode(text)));
    }
}