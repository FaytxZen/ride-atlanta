package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.List;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface FavoriteRoutesDataSource {

    interface GetFavoriteRoutesCallback {
        void onFinished(List<FavoriteRoute> favRoutes);
        void onError(Object error);
    }

    interface GetFavoriteRouteCallback {
        void onFinished(FavoriteRoute route);
        void onError(Object error);
    }

    void getFavoriteRoutes(@NonNull GetFavoriteRoutesCallback callback);
    void getFavoriteRoute(@NonNull String routeId, @NonNull GetFavoriteRouteCallback callback);
    void saveRoute(@NonNull FavoriteRoute route);
    void deleteRoute(@NonNull FavoriteRouteDataObject route);
    void deleteAllRoutes();
    void reloadRoutes();

    boolean hasCachedData();
}
