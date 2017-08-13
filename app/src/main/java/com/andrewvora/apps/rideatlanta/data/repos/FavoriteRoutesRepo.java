package com.andrewvora.apps.rideatlanta.data.repos;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
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

    @NonNull private FavoriteRoutesDataSource remoteSource;
    @NonNull private FavoriteRoutesDataSource localSource;

    @NonNull private Map<String, FavoriteRoute> cachedRoutes;

    private boolean cacheIsDirty;

    public FavoriteRoutesRepo(@NonNull FavoriteRoutesDataSource remoteSource,
                               @NonNull FavoriteRoutesDataSource localSource)
    {
        this.remoteSource = remoteSource;
        this.localSource = localSource;

        cachedRoutes = new ConcurrentHashMap<>();
    }

    @Override
    public void getFavoriteRoutes(@NonNull final GetFavoriteRoutesCallback callback) {
        if(!cachedRoutes.isEmpty() && !cacheIsDirty) {
            callback.onFinished(new ArrayList<>(cachedRoutes.values()));
        }
        else {
            localSource.getFavoriteRoutes(new GetFavoriteRoutesCallback() {
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
        final FavoriteRoute cachedRoute = cachedRoutes.get(id);

        if(cachedRoute != null) {
            callback.onFinished(cachedRoute);
        }
        else {
            localSource.getFavoriteRoute(id, new GetFavoriteRouteCallback() {
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
            localSource.saveRoute(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllRoutes() {
        localSource.deleteAllRoutes();
        remoteSource.deleteAllRoutes();

        cachedRoutes.clear();
    }

    @Override
    public void deleteRoute(@NonNull FavoriteRouteDataObject route) {
        localSource.deleteRoute(route);

        cachedRoutes.remove(getMapKeyFor(route));
    }

    @Override
    public boolean hasCachedData() {
        return !cachedRoutes.isEmpty();
    }

    @Override
    public void reloadRoutes() {
        cacheIsDirty = true;
    }

    private void reloadCachedRoutes(@NonNull List<FavoriteRoute> favoriteRoutes) {
        cachedRoutes.clear();

        for(FavoriteRoute route : favoriteRoutes) {
            cacheRoute(route);
        }

        cacheIsDirty = false;
    }

    private void cacheRoute(FavoriteRoute route) {
        cachedRoutes.put(getMapKeyFor(route), route);
    }

    private String getMapKeyFor(@NonNull FavoriteRouteDataObject route) {
        return route.getRouteId();
    }
}
