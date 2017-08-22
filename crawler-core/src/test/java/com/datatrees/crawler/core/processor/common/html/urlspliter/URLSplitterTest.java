package com.datatrees.crawler.core.processor.common.html.urlspliter;

import java.util.Collection;
import org.junit.Test;

/**
 * @author Jerry
 * @since 22:58 21/05/2017
 */
public class URLSplitterTest {
    @Test
    public void split() throws Exception {
        String originalURL = "http://www.baidu.com1https://www.baidu.com2:htrtmp://www.baidu.com3:https://www.baidu.com4";
        // originalURL =
        // "http://www.baidu.com1https://www.baidu.com2http://www.baidu.com3";
        originalURL =
                "http://www.baidu.com1hsttps://www.baidu.com2rtsp://192.168.1.130/3030/live/a?qvod://fdsaf/testhttp://www.baidu.com3 https://redirector.googlevideo.com/videoplayback?id=a30e53dacc7cf27c&itag=18&source=picasa&ip=125.227.210.74&ipbits=0&expire=1419128897&sparams=cmbypass,expire,id,ip,ipbits,itag,mm,ms,mv,requiressl,shardbypass,source&signature=7163B411367EA8C1329B4BC955475DEECD48FB3E.7A35E718D4B82E3CB96CDA3A7D551E9B4B74BE63&key=cms1&cms_redirect=yes&mm=30&ms=nxu&mt=1416536835&mv=m&requiressl=yes&shardbypass=yes&cmbypass=yes&pa://abc/ertmp://abc/t/b/f https://c/a/?love";
        // originalURL = "http://www.baidu.com";
        // originalURL = "https://http://pa://rtmp://";
        originalURL =
                "http://letitbit.net/download/80487.8d25153ac6ba2294a4e0b8f99e18/Fearsome_Gargoyles.rar.html http://www.filesonic.com/file/2461277591/Fearsome_Gargoy";
        originalURL = "http://funapna.net/rmex.htm?url=http://www.divxstage.eu/video/6bcacaaae7f66";
        Collection<String> result = URLSplitter.split(originalURL);
        for (String res : result) {
            System.out.println("res  : " + res);
        }
    }

}