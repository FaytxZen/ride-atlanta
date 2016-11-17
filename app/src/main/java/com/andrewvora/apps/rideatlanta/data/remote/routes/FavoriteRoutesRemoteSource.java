package com.andrewvora.apps.rideatlanta.data.remote.routes;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesRemoteSource implements FavoriteRoutesDataSource {

    private static FavoriteRoutesRemoteSource mInstance;

    private FavoriteRoutesRemoteSource() {
        // prevent instantiation
    }

    public static FavoriteRoutesRemoteSource getInstance() {
        if(mInstance == null) {
            mInstance = new FavoriteRoutesRemoteSource();
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

    }

    @Override
    public void reloadRoutes() {

    }
}
