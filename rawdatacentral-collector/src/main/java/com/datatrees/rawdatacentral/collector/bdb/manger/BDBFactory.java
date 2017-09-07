package com.datatrees.rawdatacentral.collector.bdb.manger;

import com.datatrees.rawdatacentral.collector.bdb.env.Environment;
import com.datatrees.rawdatacentral.collector.bdb.wapper.BDBEnvironmentWapper;
import com.datatrees.rawdatacentral.collector.bdb.wapper.BDBWapper;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:43:39
 */
public enum BDBFactory implements Environment {
    INSTANCE;
    private BDBEnvironmentManager manager = BDBEnvironmentManager.getInstance();

    public synchronized BDBWapper createDB() throws Exception {
        BDBEnvironmentWapper bdbEnvironmentWapper = manager.takeEnv();
        BDBWapper wapper = bdbEnvironmentWapper.createDB();
        return wapper;
    }

    public void deleteDB(BDBEnvironmentWapper bdbEnvironmentWapper, String databaseName) {
        bdbEnvironmentWapper.deleteDB(databaseName);
        manager.checkWapperDestory(bdbEnvironmentWapper);
    }

    @Deprecated
    public void deleteDB(String databaseName) {}
}
