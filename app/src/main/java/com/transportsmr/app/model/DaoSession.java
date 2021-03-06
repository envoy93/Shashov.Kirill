package com.transportsmr.app.model;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.transportsmr.app.model.Stop;
import com.transportsmr.app.model.Route;

import com.transportsmr.app.model.StopDao;
import com.transportsmr.app.model.RouteDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig stopDaoConfig;
    private final DaoConfig routeDaoConfig;

    private final StopDao stopDao;
    private final RouteDao routeDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        stopDaoConfig = daoConfigMap.get(StopDao.class).clone();
        stopDaoConfig.initIdentityScope(type);

        routeDaoConfig = daoConfigMap.get(RouteDao.class).clone();
        routeDaoConfig.initIdentityScope(type);

        stopDao = new StopDao(stopDaoConfig, this);
        routeDao = new RouteDao(routeDaoConfig, this);

        registerDao(Stop.class, stopDao);
        registerDao(Route.class, routeDao);
    }
    
    public void clear() {
        stopDaoConfig.clearIdentityScope();
        routeDaoConfig.clearIdentityScope();
    }

    public StopDao getStopDao() {
        return stopDao;
    }

    public RouteDao getRouteDao() {
        return routeDao;
    }

}
