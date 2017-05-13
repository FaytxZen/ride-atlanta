package com.andrewvora.apps.rideatlanta.buses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesPresenter implements
        BusRoutesContract.Presenter,
        FavoriteRoutesContract.DataLoadedListener
{
    @NonNull private BusRoutesContract.View mView;
    @NonNull private BusesDataSource mBusesRepo;
    @NonNull private FavoriteRoutesDataSource mFavRoutesRepo;
    @NonNull private FavoriteRoutesContract.LoadingCache mFavRoutesCache;

    @NonNull
    private BroadcastReceiver mBusesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadBusRoutes();
        }
    };

    public BusRoutesPresenter(@NonNull BusRoutesContract.View view,
                              @NonNull BusesDataSource busRepo,
                              @NonNull FavoriteRoutesDataSource routesRepo,
                              @NonNull FavoriteRoutesContract.LoadingCache routesDataManager)
    {
        mView = view;
        mBusesRepo = busRepo;
        mFavRoutesRepo = routesRepo;
        mFavRoutesCache = routesDataManager;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        mFavRoutesCache.setListener(this);
        mFavRoutesCache.loadFavoriteRoutes();

        mView.subscribeReceiver(mBusesReceiver);

        loadBusRoutes();
    }

    @Override
    public void stop() {
        mFavRoutesCache.setListener(null);
        mView.unsubscribeReceiver(mBusesReceiver);
    }

    @Override
    public void onFavoriteRoutesLoaded(@NonNull List<FavoriteRouteDataObject> favRoutes) {
        mView.applyFavorites(favRoutes);
    }

    @Override
    public void loadBusRoutes() {
        useCachedDataIfAvailable();

        mBusesRepo.getBuses(createGetBusesCallbackInstance());
    }

    @Override
    public void refreshBusRoutes() {
        mBusesRepo.reloadBuses();
        mBusesRepo.getBuses(createGetBusesCallbackInstance());
    }

    @Override
    public void favoriteRoute(@NonNull Bus bus) {
        // toggle favorited value
        bus.setFavorited(!bus.isFavorited());

        mBusesRepo.saveBus(bus);

        // set repo to get fresh data
        mFavRoutesRepo.reloadRoutes();
        mFavRoutesCache.setFavoritedRoutes(new ArrayList<FavoriteRouteDataObject>());

        FavoriteRoute favoriteRoute = new FavoriteRoute(bus);

        if(bus.isFavorited()) {
            mFavRoutesRepo.saveRoute(favoriteRoute);
        }
        else {
            mFavRoutesRepo.deleteRoute(favoriteRoute);
        }
    }

    private BusesDataSource.GetBusesCallback createGetBusesCallbackInstance() {
        return new BusesDataSource.GetBusesCallback() {
            @Override
            public void onFinished(List<Bus> buses) {
                updateView(buses);
            }

            @Override
            public void onError(Object error) {

            }
        };
    }

    private void updateView(List<Bus> buses) {
        mView.onBusRoutesLoaded(buses);
    }

    private boolean hasNoCachedData() {
        return !mBusesRepo.hasCachedData();
    }

    private void useCachedDataIfAvailable() {
        if(hasNoCachedData()) {
            mBusesRepo.reloadBuses();
        }
    }
}
