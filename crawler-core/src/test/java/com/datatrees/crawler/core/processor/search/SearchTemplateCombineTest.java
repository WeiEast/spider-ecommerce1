package com.datatrees.crawler.core.processor.search;

import java.util.HashMap;
import org.junit.Test;

/**
 * @author Jerry
 * @since 22:27 21/05/2017
 */
public class SearchTemplateCombineTest {
    @Test
    public void constructSearchURL() throws Exception {
        String tmp = "http://www.soku.com/search_page_${page,0,40,3+}video/q_${keyword}_orderby_2_page_${page,0,40,3+}";
        String keyword = "tt";
        // tmp="http://putlocker.is/search/search.php?q=${keyword}";
        keyword = "Nausikaja iz_Doline /vetrova";
        String result = SearchTemplateCombine.constructSearchURL(tmp, keyword, "utf-8", 1, false, new HashMap<>());
        System.out.println(result);
    }

    @Test
    public void customTemplate() throws Exception {
        //        String tmp = "http://www.soku.com/search_video_#{page,0*1,5,4+}/page_#{page,0*1,21/20+1,5+}";
        String tmp = "http://mail.163.com/jy6/xhr/list/search.do?sid=${sid}\"fid=0&start=#{page,0*1,1000/100+1,100+}&limit=100&thread=false&keyword=${keyword}&searchType=FULL";
        // customTemplate(tmp, -1);
        SearchTemplateCombine.customTemplate(tmp, 1);
        SearchTemplateCombine.customTemplate(tmp, 2);
        SearchTemplateCombine.customTemplate(tmp, 3);
        SearchTemplateCombine.customTemplate(tmp, 4);
        SearchTemplateCombine.customTemplate(tmp, 5);
        // SearchTemplateCombine.customTemplate(tmp, 2);
    }

}
