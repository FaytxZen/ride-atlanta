package com.andrewvora.apps.rideatlanta.buses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusRoutesPresenter implements BusRoutesContract.Presenter {

    private Context mContext;
    private BusRoutesContract.View mView;

    public BusRoutesPresenter(@NonNull Context context, @NonNull BusRoutesContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) { }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        loadBusRoutes();
    }

    @Override
    public void loadBusRoutes() {
        BusesRepo repo = BusesRepo.getInstance(mContext);

        useCachedDataIfAvailable(repo);

        repo.getBuses(new BusesDataSource.GetBusesCallback() {
            @Override
            public void onFinished(List<Bus> buses) {
                makeCachedDataAvailable();
                mView.onBusRoutesLoaded(buses);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private boolean hasNoCachedData() {
        return !CachedDataMap.getInstance().hasCachedData(getCachedDataTag());
    }

    private void makeCachedDataAvailable() {
        CachedDataMap.getInstance().put(getCachedDataTag(), true);
    }

    private void useCachedDataIfAvailable(BusesRepo repo) {
        if(hasNoCachedData()) {
            repo.reloadBuses();
        }
    }

    private String getCachedDataTag() {
        return BusRoutesPresenter.class.getSimpleName();
    }
}
