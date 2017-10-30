package com.datatrees.rawdatacentral.common.utils;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class RegexpUtilsTest {

    @Test
    public void selectLast() throws Exception {
        String source = FileUtils.readFileToString(new File("/data/sms.html"));
        String regex = "document.write\\(\"(.*)\"\\)";
        String selectText = RegexpUtils.selectLast(source, regex, 1);
        List<Element> list = JsoupXpathUtils.selectElements("<table>" + selectText + "</table>", "//tr");
        for (Element e : list) {
            Elements tds = e.getElementsByTag("td");
            Integer port = Integer.valueOf(tds.get(0).text());
            String mobile = tds.get(1).text();
            Date date = DateUtils.parse(tds.get(2).text(), "yyyy/MM/dd HH:mm:ss");
            String content = tds.get(3).getElementsByTag("textarea").text();
            System.out.println(1);
        }

    }
}