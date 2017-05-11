package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesLocalSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesRepo implements FavoriteRoutesDataSource {

    private static FavoriteRoutesRepo mInstance;

    private FavoriteRoutesDataSource mRemoteSource;
    private FavoriteRoutesDataSource mLocalSource;

    @NonNull
    private Map<String, FavoriteRoute> mCachedRoutes;

    private boolean mCacheIsDirty;

    private FavoriteRoutesRepo(@NonNull FavoriteRoutesDataSource remoteSource,
                               @NonNull FavoriteRoutesDataSource localSource)
    {
        mRemoteSource = remoteSource;
        mLocalSource = localSource;

        mCachedRoutes = new ConcurrentHashMap<>();
    }

    public static FavoriteRoutesRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            // routes is local-only at this point in time
            FavoriteRoutesDataSource localSource = FavoriteRoutesLocalSource.getInstance(context);
            mInstance = new FavoriteRoutesRepo(localSource, localSource);
        }

        return mInstance;
    }

    @Override
    public void getFavoriteRoutes(@NonNull final GetFavoriteRoutesCallback callback) {
        if(!mCachedRoutes.isEmpty() && !mCacheIsDirty) {
            callback.onFinished(new ArrayList<>(mCachedRoutes.values()));
        }
        else {
            mLocalSource.getFavoriteRoutes(new GetFavoriteRoutesCallback() {
                @Override
                public void onFinished(List<FavoriteRoute> favRoutes) {
                    reloadCachedRoutes(favRoutes);
                    callback.onFinished(favRoutes);
                }

                @Override
                public void onError(Object error) {
                    callback.onError(error);
                }
            });
        }
    }

    @Override
    public void getFavoriteRoute(@NonNull String id,
                                 @NonNull final GetFavoriteRouteCallback callback)
    {
        final FavoriteRoute cachedRoute = mCachedRoutes.get(id);

        if(cachedRoute != null) {
            callback.onFinished(cachedRoute);
        }
        else {
            mLocalSource.getFavoriteRoute(id, new GetFavoriteRouteCallback() {
                @Override
                public void onFinished(FavoriteRoute route) {
                    callback.onFinished(route);
                    cacheRoute(route);
                }

                @Override
                public void onError(Object error) {
                    callback.onError(error);
                }
            });
        }
    }

    @Override
    public void saveRoute(@NonNull FavoriteRoute route) {
        // this app only saves things locally
        try {
            mLocalSource.saveRoute(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllRoutes() {
        mLocalSource.deleteAllRoutes();
        mRemoteSource.deleteAllRoutes();

        mCachedRoutes.clear();
    }

    @Override
    public void deleteRoute(@NonNull FavoriteRouteDataObject route) {
        mLocalSource.deleteRoute(route);

        mCachedRoutes.remove(getMapKeyFor(route));
    }

    @Override
    public void reloadRoutes() {
        mCacheIsDirty = true;
    }

    private void reloadCachedRoutes(@NonNull List<FavoriteRoute> favoriteRoutes) {
        mCachedRoutes.clear();

        for(FavoriteRoute route : favoriteRoutes) {
            cacheRoute(route);
        }

        mCacheIsDirty = false;
    }

    private void cacheRoute(FavoriteRoute route) {
        mCachedRoutes.put(getMapKeyFor(route), route);
    }

    private String getMapKeyFor(@NonNull FavoriteRouteDataObject route) {
        return route.getRouteId();
    }
}
