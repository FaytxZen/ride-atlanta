package com.andrewvora.apps.rideatlanta.data.remote.routes;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.List;

import io.reactivex.Observable;

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
    public Observable<List<FavoriteRoute>> getFavoriteRoutes() {
        return Observable.empty();
    }

    @Override
    public Observable<FavoriteRoute> getFavoriteRoute(@NonNull String routeId) {
        return Observable.empty();
    }

	@Override
	public Observable<Long> saveRoute(@NonNull FavoriteRoute route) {
		return Observable.empty();
	}

	@Override
	public Observable<Long> deleteAllRoutes() {
		return Observable.empty();
	}

	@Override
	public Observable<Long> deleteRoute(@NonNull FavoriteRouteDataObject route) {
		return Observable.empty();
	}

	@Override
    public void reloadRoutes() {

    }
}
