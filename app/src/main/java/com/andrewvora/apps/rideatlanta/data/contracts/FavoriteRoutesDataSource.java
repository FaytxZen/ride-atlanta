package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface FavoriteRoutesDataSource {

    Observable<List<FavoriteRoute>> getFavoriteRoutes();
    Observable<FavoriteRoute> getFavoriteRoute(@NonNull String routeId);
    Observable<Long> saveRoute(@NonNull FavoriteRoute route);
    Observable<Long> deleteRoute(@NonNull FavoriteRouteDataObject route);
    Observable<Long> deleteAllRoutes();
    void reloadRoutes();

    boolean hasCachedData();
}
