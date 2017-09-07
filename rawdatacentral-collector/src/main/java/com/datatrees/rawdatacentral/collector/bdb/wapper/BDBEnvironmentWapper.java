package com.datatrees.rawdatacentral.collector.bdb.wapper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.datatrees.rawdatacentral.collector.bdb.env.BDBEnvironmentContext;
import com.datatrees.rawdatacentral.collector.bdb.env.Environment;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.je.SecondaryDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:46:39
 */
public class BDBEnvironmentWapper extends BDBEnvironmentContext implements Environment {

    private static final Logger log = LoggerFactory.getLogger(BDBEnvironmentWapper.class);
    // Save the number of created db
    private final AtomicInteger plusCount;
    // save the number of deleted db
    private final AtomicInteger minusCount;
    private final Map<String, BDBDatabaseWapper> dbBucket = new ConcurrentHashMap<String, BDBDatabaseWapper>();

    /**
     * @param env
     * @exception DatabaseException
     * @exception EnvironmentLockedException
     */
    public BDBEnvironmentWapper() throws Exception {
        super(UUID.randomUUID() + "");
        plusCount = new AtomicInteger();
        minusCount = new AtomicInteger();
    }

    @Override
    public BDBWapper createDB() {
        // use uuid to be the dbname
        String uuid = UUID.randomUUID().toString();
        String databaseName = CollectorConstants.DATABASE_NAME_SUFFIX + uuid;
        String secondaryName = CollectorConstants.SECONDARY_NAME_SUFFIX + uuid;
        BDBWapper wapper = null;
        try {
            Database linkDB = env.openDatabase(null, databaseName, dbConfig);
            SecondaryDatabase slinkDB = env.openSecondaryDatabase(null, secondaryName, linkDB, sdbConfig);
            log.debug("create database [" + databaseName + "] secondaryName : " + secondaryName);
            BDBDatabaseWapper databaseWapper = new BDBDatabaseWapper(linkDB, slinkDB);
            databaseWapper.setDatabaseName(databaseName);
            databaseWapper.setSecondaryName(secondaryName);
            dbBucket.put(databaseName, databaseWapper);
            wapper = new BDBWapper(this, databaseWapper);
            plusCount.incrementAndGet();
            log.debug("Env hashCode : " + this.hashCode() + " ,database count : " + plusCount.get());
        } catch (Exception e) {
            log.error("createDB " + databaseName + " failed.", e);
        }
        return wapper;
    }

    @Override
    public void deleteDB(String databaseName) {
        if (dbBucket.containsKey(databaseName)) {
            long numDiscarded = 0;
            try {
                BDBDatabaseWapper databaseWapper = dbBucket.get(databaseName);
                try {
                    numDiscarded = env.truncateDatabase(null, databaseName, true);
                    env.removeDatabase(null, databaseWapper.getSecondaryName());
                    env.removeDatabase(null, databaseWapper.getDatabaseName());
                } catch (Exception e) {
                    log.error("truncate database  error ,databaseName : " + databaseName, e);
                }
                log.debug("truncate database [" + databaseName + "] count : " + numDiscarded);
            } catch (Exception e) {
                log.error("deleteDB " + databaseName + " failed.", e);
            } finally {
                dbBucket.remove(databaseName);
                minusCount.incrementAndGet();
                log.debug("Env hashCode : " + this.hashCode() + " ,database count : " + minusCount.get());
            }
        }
    }

    public AtomicInteger getPlusDBCount() {
        return plusCount;
    }

    public AtomicInteger getMinusDBCount() {
        return minusCount;
    }

}
