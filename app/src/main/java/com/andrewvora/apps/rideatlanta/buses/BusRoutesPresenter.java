package com.andrewvora.apps.rideatlanta.buses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
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

    private BusRoutesContract.View mView;
    private BusesDataSource mBusesRepo;
    private FavoriteRoutesDataSource mFavRoutesRepo;
    private BroadcastReceiver mBusesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadBusRoutes();
        }
    };

    public BusRoutesPresenter(@NonNull BusRoutesContract.View view) {
        mView = view;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        mBusesRepo = mView.getBusesDataSource();
        mFavRoutesRepo = mView.getFavRoutesDataSource();

        mView.subscribeReceiver(mBusesReceiver);
        loadBusRoutes();
    }

    @Override
    public void stop() {
        mBusesRepo = null;
        mView.unsubscribeReceiver(mBusesReceiver);
    }

    @Override
    public void loadBusRoutes() {
        useCachedDataIfAvailable(mBusesRepo);

        mBusesRepo.getBuses(createGetBusesCallbackInstance());
    }

    @Override
    public void refreshBusRoutes() {
        mBusesRepo.reloadBuses();

        mBusesRepo.getBuses(createGetBusesCallbackInstance());
    }

    @Override
    public void favoriteRoute(@NonNull Bus bus) {
        bus.setFavorited(!bus.isFavorited());
        mBusesRepo.saveBus(bus);

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
                makeCachedDataAvailable();
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
        return !CachedDataMap.getInstance().hasCachedData(TAG);
    }

    private void makeCachedDataAvailable() {
        CachedDataMap.getInstance().put(TAG, true);
    }

    private void useCachedDataIfAvailable(BusesDataSource repo) {
        if(hasNoCachedData()) {
            repo.reloadBuses();
        }
    }
}
