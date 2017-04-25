package com.datatrees.rawdatacentral.collector.chain;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:36:14
 */
public interface FilterConstant {
    // common proerites
    public final static String CURRENT_LINK_NODE = "CURRENT_LINK_NODE";

    // hosting site & redlist filter
    public final static String URL_HANDLER = "URL_HANDLER";
    public final static String FECHED_LINK_NODE = "FECHED_LINK_NODE";
    public final static String FETCHED_DOMAIN = "FETCHED_DOMAIN";

    public final static String DUPLICATE_CHECKER = "DUPLICATE_CHECKER";


    // remove flag
    public final static String TRUE = "1";

    // search processor filter
    public final static String SEARCH_PROCESSOR = "SEARCH_PROCESSOR";
    public final static String CURRENT_RESPONSE = "CURRENT_RESPONSE";
    public final static String CURRENT_REQUEST = "CURRENT_REQUEST";
    public final static String FETCHED_LINK_NODE_LIST = "FETCHED_LINK_NODE_LIST";
    public final static String ERROR_CODE = "ERROR_CODE";

    public final static String PARSER_TEMPLATE_URL_LIST = "PARSER_TEMPLATE_URL_LIST";
}
