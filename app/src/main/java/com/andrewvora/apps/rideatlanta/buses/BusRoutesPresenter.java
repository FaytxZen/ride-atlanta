package com.andrewvora.apps.rideatlanta.buses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesPresenter implements BusRoutesContract.Presenter {

    static final String TAG = BusRoutesPresenter.class.getSimpleName();

    @NonNull
    private BusRoutesContract.View mView;

    @NonNull
    private BusesDataSource mBusesRepo;

    @NonNull
    private FavoriteRoutesDataSource mFavRoutesRepo;

    @NonNull
    private BroadcastReceiver mBusesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadBusRoutes();
        }
    };

    public BusRoutesPresenter(@NonNull BusRoutesContract.View view,
                              @NonNull BusesDataSource busRepo,
                              @NonNull FavoriteRoutesDataSource routesRepo)
    {
        mView = view;
        mBusesRepo = busRepo;
        mFavRoutesRepo = routesRepo;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        mView.subscribeReceiver(mBusesReceiver);
        loadBusRoutes();
    }

    @Override
    public void stop() {
        mView.unsubscribeReceiver(mBusesReceiver);
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
