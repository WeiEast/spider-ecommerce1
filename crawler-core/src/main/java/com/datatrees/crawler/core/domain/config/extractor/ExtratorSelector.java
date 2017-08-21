package com.datatrees.crawler.core.domain.config.extractor;

import java.io.Serializable;

import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 上午10:41:44
 */
@Tag("selector")
public class ExtratorSelector implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8089954857564467379L;

    private String field;

    private String containRegex;

    private String disContainRegex;

    private PageExtractor pageExtractor;

    @Attr("field")
    public String getField() {
        return field;
    }

    @Node("@field")
    public void setField(String field) {
        this.field = field;
    }

    @Attr("contain")
    public String getContainRegex() {
        return containRegex;
    }


    @Node("@contain")
    public void setContainRegex(String containRegex) {
        this.containRegex = containRegex;
    }

    @Attr("dis-contain")
    public String getDisContainRegex() {
        return disContainRegex;
    }

    @Node("@dis-contain")
    public void setDisContainRegex(String disContainRegex) {
        this.disContainRegex = disContainRegex;
    }

    @Attr(value = "ref", referenced = true)
    public PageExtractor getPageExtractor() {
        return pageExtractor;
    }

    @Node(value = "@ref", referenced = true)
    public void setPageExtractor(PageExtractor pageExtractor) {
        this.pageExtractor = pageExtractor;
    }

}
