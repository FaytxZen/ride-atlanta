package com.andrewvora.apps.rideatlanta.data.remote.routes;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.remote.buses.BusesRemoteSource;
import com.andrewvora.apps.rideatlanta.data.remote.trains.TrainsRemoteSource;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesRemoteSource implements FavoriteRoutesDataSource {

    private static FavoriteRoutesRemoteSource mInstance;

    private BusesDataSource mRemoteBusSource;
    private TrainsDataSource mRemoteTrainSource;

    private FavoriteRoutesRemoteSource(@NonNull Context context) {
        // prevent instantiation
        mRemoteBusSource = BusesRemoteSource.getInstance(context);
        mRemoteTrainSource = TrainsRemoteSource.getInstance(context);
    }

    public static FavoriteRoutesRemoteSource getInstance(@NonNull Context context) {
        if(mInstance == null) {
            mInstance = new FavoriteRoutesRemoteSource(context);
        }

        return mInstance;
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
    public void reloadRoutes() {

    }
}
