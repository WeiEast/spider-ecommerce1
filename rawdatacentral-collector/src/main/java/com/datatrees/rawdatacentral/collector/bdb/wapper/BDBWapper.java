package com.datatrees.rawdatacentral.collector.bdb.wapper;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:54:30
 */
public class BDBWapper {

    private BDBEnvironmentWapper envWapper      = null;

    private BDBDatabaseWapper    databaseWapper = null;

    public BDBWapper(BDBEnvironmentWapper envWapper, BDBDatabaseWapper databaseWapper) {
        super();
        this.envWapper = envWapper;
        this.databaseWapper = databaseWapper;
    }

    public BDBEnvironmentWapper getEnvWapper() {
        return envWapper;
    }

    public void setEnvWapper(BDBEnvironmentWapper envWapper) {
        this.envWapper = envWapper;
    }

    public BDBDatabaseWapper getDatabaseWapper() {
        return databaseWapper;
    }

    public void setDatabaseWapper(BDBDatabaseWapper databaseWapper) {
        this.databaseWapper = databaseWapper;
    }

}
