package com.datatrees.crawler.core.processor.service.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0 2012-04-19
 * @since 1.0
 */
public class ParamsOut {

    private int          error_code         = 0;
    private String       error_extra_info   = "";
    private int          http_code          = 200;
    private String       image_hash         = "";
    private String       last_modified_time = "";
    private String       page_title         = "";
    private int          total_duration     = -1;
    private String       video_download_url = "";
    private List<Html>   extracted_htmls    = new ArrayList<Html>();
    private List<Links>  extracted_links    = new ArrayList<Links>();
    private List<Search> searchFormInfo     = new ArrayList<Search>();

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_extra_info() {
        return error_extra_info;
    }

    public void setError_extra_info(String error_extra_info) {
        this.error_extra_info = error_extra_info;
    }

    public int getHttp_code() {
        return http_code;
    }

    public void setHttp_code(int http_code) {
        this.http_code = http_code;
    }

    public String getImage_hash() {
        return image_hash;
    }

    public void setImage_hash(String image_hash) {
        this.image_hash = image_hash;
    }

    public String getLast_modified_time() {
        return last_modified_time;
    }

    public void setLast_modified_time(String last_modified_time) {
        this.last_modified_time = last_modified_time;
    }

    public String getPage_title() {
        return page_title;
    }

    public void setPage_title(String page_title) {
        //page_title = UrlEncoded.encodeString(page_title);
        this.page_title = page_title;
    }

    public int getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(int total_duration) {
        this.total_duration = total_duration;
    }

    public List<Html> getExtracted_htmls() {
        return extracted_htmls;
    }

    public void setExtracted_htmls(List<Html> extracted_htmls) {
        this.extracted_htmls = extracted_htmls;
    }

    public List<Links> getExtracted_links() {
        return extracted_links;
    }

    public void setExtracted_links(List<Links> extracted_links) {
        this.extracted_links = extracted_links;
    }

    public List<Search> getSearchFormInfo() {
        return searchFormInfo;
    }

    public void setSearchFormInfo(List<Search> searchFormInfo) {
        this.searchFormInfo = searchFormInfo;
    }

    public void adddSearchFormInfo(String searchPostDataTemplate, String searchURLTemplate, String urlCharset) {
        searchFormInfo.add(new Search(searchPostDataTemplate, searchURLTemplate, urlCharset));
    }

    public void adddExtracted_links(String anchor_text, String url, String iframe_type) {
        extracted_links.add(new Links(anchor_text, url, iframe_type));
    }

    public void adddExtracted_htmls(String html, String url) {
        extracted_htmls.add(new Html(html, url));
    }

    public String getVideo_download_url() {
        return video_download_url;
    }

    public void setVideo_download_url(String video_download_url) {
        this.video_download_url = video_download_url;
    }

    // -----------------------------------------------------
    public static class Html {

        private String html;
        private String url;

        Html(String html, String url) {
            this.html = html;
            this.url = url;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public static class Links {

        private String anchor_text;
        private String url;
        private String iframe_type;

        Links(String anchor_text, String url, String iframe_type) {
            this.anchor_text = anchor_text;
            this.url = url;
            this.iframe_type = iframe_type;
        }

        public String getAnchor_text() {
            return anchor_text;
        }

        public void setAnchor_text(String anchor_text) {
            this.anchor_text = anchor_text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIframe_type() {
            return iframe_type;
        }

        public void setIframe_type(String iframe_type) {
            this.iframe_type = iframe_type;
        }

    }

    public static class Search {

        private String searchPostDataTemplate;
        private String searchURLTemplate;
        private String urlCharset;

        Search(String searchPostDataTemplate, String searchURLTemplate, String urlCharset) {
            this.searchPostDataTemplate = searchPostDataTemplate;
            this.searchURLTemplate = searchURLTemplate;
            this.urlCharset = urlCharset;
        }

        public String getSearchPostDataTemplate() {
            return searchPostDataTemplate;
        }

        public void setSearchPostDataTemplate(String searchPostDataTemplate) {
            this.searchPostDataTemplate = searchPostDataTemplate;
        }

        public String getSearchURLTemplate() {
            return searchURLTemplate;
        }

        public void setSearchURLTemplate(String searchURLTemplate) {
            this.searchURLTemplate = searchURLTemplate;
        }

        public String getUrlCharset() {
            return urlCharset;
        }

        public void setUrlCharset(String urlCharset) {
            this.urlCharset = urlCharset;
        }

    }
}
