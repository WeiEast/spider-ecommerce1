package com.datatrees.spider.share.service.collector.bdb.env;

import com.datatrees.spider.share.service.collector.bdb.wapper.BDBWapper;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月19日 下午8:19:39
 */
public interface Environment {

    public BDBWapper createDB() throws Exception;

    public void deleteDB(String databaseName);
}
