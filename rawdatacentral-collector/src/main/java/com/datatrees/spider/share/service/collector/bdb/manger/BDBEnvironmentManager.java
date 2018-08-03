package com.datatrees.spider.share.service.collector.bdb.manger;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.collector.bdb.wapper.BDBEnvironmentWapper;
import com.datatrees.spider.share.service.collector.bdb.wapper.BDBEnvironmentWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月19日 下午9:02:57
 */
public class BDBEnvironmentManager implements EnvironmentManager {

    private static final Logger                                      log                    = LoggerFactory.getLogger(BDBEnvironmentManager.class);

    private static       BDBEnvironmentManager                       manager                = new BDBEnvironmentManager();

    // Save the object needs to be destoryed
    // private ConcurrentLinkedQueue<BDBEnvironmentWapper> toBeDestroyedContainer = new
    // ConcurrentLinkedQueue<BDBEnvironmentWapper>();
    private final        int                                         max_db_count           = PropertiesConfiguration.getInstance()
            .getInt("collector.bdb.max.db.count", 10);

    // Keep only one generation object in this queue
    private              ConcurrentLinkedQueue<BDBEnvironmentWapper> newGenerationContainer = new ConcurrentLinkedQueue<BDBEnvironmentWapper>();

    private BDBEnvironmentManager() {}

    ;

    public static BDBEnvironmentManager getInstance() {
        return manager;
    }

    @Override
    public boolean checkIfNeed2CreateEnv(BDBEnvironmentWapper wapper) {
        return (wapper.getPlusDBCount().get() >= max_db_count || !wapper.getEnv().isValid());
    }

    public BDBEnvironmentWapper takeEnv() throws Exception {
        BDBEnvironmentWapper bdbEnvironmentWapper = null;
        if (newGenerationContainer.isEmpty()) {
            bdbEnvironmentWapper = buildEnvironment();
        } else {
            bdbEnvironmentWapper = newGenerationContainer.peek();
            if (manager.checkIfNeed2CreateEnv(bdbEnvironmentWapper)) {
                manager.removeEnv();
                return takeEnv();
            }
        }
        return bdbEnvironmentWapper;
    }

    private BDBEnvironmentWapper buildEnvironment() throws Exception {
        BDBEnvironmentWapper bdbEnvironmentWapper = new BDBEnvironmentWapper();
        newGenerationContainer.add(bdbEnvironmentWapper);
        // toBeDestroyedContainer.add(bdbEnvironmentWapper);
        return bdbEnvironmentWapper;
    }

    /**
     * Move the new generation of objects to destroyed queue
     */
    @Override
    public void removeEnv() {
        newGenerationContainer.remove();
    }

    public void checkWapperDestory(BDBEnvironmentWapper wapper) {
        if (isEnvDestoryed(wapper)) {
            File envHome = wapper.getEnvHome();
            if (envHome.exists()) {
                closeEnv(wapper);
                log.info("Env hashCode : " + wapper.hashCode() + " destory... ");
                BDBEnvironmentWapper.destroyEnv(envHome);
                // toBeDestroyedContainer.remove();
            }
        }
    }

    private void closeEnv(BDBEnvironmentWapper wapper) {
        try {
            wapper.getEnv().close();
        } catch (Exception e) {
            log.error("close environment failed , error : ", e);
        }
    }

    private boolean isEnvDestoryed(BDBEnvironmentWapper wapper) {
        if (wapper.getMinusDBCount().get() >= max_db_count || !wapper.getEnv().isValid()) {
            if (log.isDebugEnabled()) {
                log.debug("Env hashCode : " + wapper.hashCode() + " count  :" + wapper.getMinusDBCount().get());
            }
            return true;
        }
        return false;
    }
}
