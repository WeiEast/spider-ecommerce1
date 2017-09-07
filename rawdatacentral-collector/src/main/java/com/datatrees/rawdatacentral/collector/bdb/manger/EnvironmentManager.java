package com.datatrees.rawdatacentral.collector.bdb.manger;

import com.datatrees.rawdatacentral.collector.bdb.wapper.BDBEnvironmentWapper;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:43:43
 */
public interface EnvironmentManager {

    public boolean checkIfNeed2CreateEnv(BDBEnvironmentWapper wapper);

    public void checkWapperDestory(BDBEnvironmentWapper wapper);

    public BDBEnvironmentWapper takeEnv() throws Exception;

    public void removeEnv();
}
