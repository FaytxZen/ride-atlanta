package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.andrewvora.apps.rideatlanta.common.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesLocalSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesRepo implements FavoriteRoutesDataSource {

    private static FavoriteRoutesRepo mInstance;

    private FavoriteRoutesDataSource mRemoteSource;
    private FavoriteRoutesDataSource mLocalSource;

    private Map<String, FavoriteRoute> mCachedRoutes;

    private boolean mCacheIsDirty;

    private FavoriteRoutesRepo(@NonNull FavoriteRoutesDataSource remoteSource,
                               @NonNull FavoriteRoutesDataSource localSource)
    {
        mRemoteSource = remoteSource;
        mLocalSource = localSource;
    }

    public static FavoriteRoutesRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            // routes is local-only at this point in time
            FavoriteRoutesDataSource localSource = FavoriteRoutesLocalSource.getInstance(context);
            mInstance = new FavoriteRoutesRepo(localSource, localSource);
        }

        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    @Override
    public void getFavoriteRoutes(@NonNull final GetFavoriteRoutesCallback callback) {

    }

    @Override
    public void getFavoriteRoute(@NonNull String routeId,
                                 @NonNull GetFavoriteRouteCallback callback)
    {

    }

    @Override
    public void saveRoute(@NonNull FavoriteRoute route) {

    }

    @Override
    public void deleteAllRoutes() {

    }

    @Override
    public void reloadRoutes() {
        mCacheIsDirty = true;
    }

    private void reloadCachedRoutes(List<FavoriteRoute> favoriteRoutes) {


        mCacheIsDirty = false;
    }

    private void cacheRoute(FavoriteRoute route) {

    }

    private Map<String, FavoriteRoute> checkNotNull(Map<String, FavoriteRoute> favRoutesMap) {
        if(favRoutesMap == null) {
            favRoutesMap = new LinkedHashMap<>();
        }

        return favRoutesMap;
    }
}
