package com.miaxis.face.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.LocalFeature;
import com.miaxis.face.bean.Record;
import com.miaxis.face.bean.WhiteItem;

import com.miaxis.face.greendao.gen.ConfigDao;
import com.miaxis.face.greendao.gen.LocalFeatureDao;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.greendao.gen.WhiteItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig configDaoConfig;
    private final DaoConfig localFeatureDaoConfig;
    private final DaoConfig recordDaoConfig;
    private final DaoConfig whiteItemDaoConfig;

    private final ConfigDao configDao;
    private final LocalFeatureDao localFeatureDao;
    private final RecordDao recordDao;
    private final WhiteItemDao whiteItemDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        configDaoConfig = daoConfigMap.get(ConfigDao.class).clone();
        configDaoConfig.initIdentityScope(type);

        localFeatureDaoConfig = daoConfigMap.get(LocalFeatureDao.class).clone();
        localFeatureDaoConfig.initIdentityScope(type);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        whiteItemDaoConfig = daoConfigMap.get(WhiteItemDao.class).clone();
        whiteItemDaoConfig.initIdentityScope(type);

        configDao = new ConfigDao(configDaoConfig, this);
        localFeatureDao = new LocalFeatureDao(localFeatureDaoConfig, this);
        recordDao = new RecordDao(recordDaoConfig, this);
        whiteItemDao = new WhiteItemDao(whiteItemDaoConfig, this);

        registerDao(Config.class, configDao);
        registerDao(LocalFeature.class, localFeatureDao);
        registerDao(Record.class, recordDao);
        registerDao(WhiteItem.class, whiteItemDao);
    }
    
    public void clear() {
        configDaoConfig.clearIdentityScope();
        localFeatureDaoConfig.clearIdentityScope();
        recordDaoConfig.clearIdentityScope();
        whiteItemDaoConfig.clearIdentityScope();
    }

    public ConfigDao getConfigDao() {
        return configDao;
    }

    public LocalFeatureDao getLocalFeatureDao() {
        return localFeatureDao;
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public WhiteItemDao getWhiteItemDao() {
        return whiteItemDao;
    }

}