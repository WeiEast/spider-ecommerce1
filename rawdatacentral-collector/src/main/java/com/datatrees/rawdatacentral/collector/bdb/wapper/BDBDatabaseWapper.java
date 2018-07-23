package com.datatrees.rawdatacentral.collector.bdb.wapper;

import com.sleepycat.je.Database;
import com.sleepycat.je.SecondaryDatabase;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:46:33
 */
public class BDBDatabaseWapper {

    private String            databaseName  = null;

    private String            secondaryName = null;

    private Database          db            = null;

    private SecondaryDatabase sdb           = null;

    public BDBDatabaseWapper(Database db, SecondaryDatabase sdb) {
        super();
        this.db = db;
        this.sdb = sdb;
    }

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public SecondaryDatabase getSdb() {
        return sdb;
    }

    public void setSdb(SecondaryDatabase sdb) {
        this.sdb = sdb;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getSecondaryName() {
        return secondaryName;
    }

    public void setSecondaryName(String secondaryName) {
        this.secondaryName = secondaryName;
    }
}
