package com.andrewvora.apps.rideatlanta.data.remote.routes;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesRemoteSource implements FavoriteRoutesDataSource {

    private BusesDataSource remoteBusSource;
    private TrainsDataSource remoteTrainSource;

    public FavoriteRoutesRemoteSource(@NonNull BusesDataSource busSource,
									  @NonNull TrainsDataSource trainSource)
    {
        // prevent instantiation
        remoteBusSource = busSource;
        remoteTrainSource = trainSource;
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    @Override
    public void getFavoriteRoutes(@NonNull GetFavoriteRoutesCallback callback) {

    }

    @Override
    public void getFavoriteRoute(@NonNull String routeId, @NonNull GetFavoriteRouteCallback callback) {

    }

    @Override
    public void saveRoute(@NonNull FavoriteRoute route) {

    }

    @Override
    public void deleteAllRoutes() {
        // no route is stored on a remote server
    }

    @Override
    public void deleteRoute(@NonNull FavoriteRouteDataObject route) {

    }

    @Override
    public void reloadRoutes() {

    }
}
