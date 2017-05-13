package com.andrewvora.apps.rideatlanta.trains;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesPresenter implements TrainRoutesContract.Presenter {

    @NonNull private TrainRoutesContract.View mView;
    @NonNull private TrainsDataSource mTrainRepo;
    @NonNull private FavoriteRoutesDataSource mFavoriteDataSource;

    private BroadcastReceiver mTrainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadTrainRoutes();
        }
    };

    public TrainRoutesPresenter(@NonNull TrainRoutesContract.View view,
                                @NonNull TrainsDataSource trainRepo,
                                @NonNull FavoriteRoutesDataSource favRouteRepo)
    {
        mView = view;
        mTrainRepo = trainRepo;
        mFavoriteDataSource = favRouteRepo;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        mView.subscribeReceiver(mTrainReceiver);

        loadTrainRoutes();
    }

    @Override
    public void stop() {
        mView.unsubscribeReceiver(mTrainReceiver);
    }

    @Override
    public void loadTrainRoutes() {
        useCachedDataIfAvailable(mTrainRepo);

        mTrainRepo.getTrains(createGetTrainRoutesCallbackInstance());
    }

    @Override
    public void refreshTrainRoutes() {
        mTrainRepo.reloadTrains();
        mTrainRepo.getTrains(createGetTrainRoutesCallbackInstance());
    }

    private TrainsDataSource.GetTrainRoutesCallback createGetTrainRoutesCallbackInstance() {
        return new TrainsDataSource.GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                updateViews(trainList);
            }

            @Override
            public void onError(Object error) {

            }
        };
    }

    @Override
    public void favoriteRoute(@NonNull Train route) {
        route.setFavorited(!route.isFavorited());

        mTrainRepo.saveTrain(route);
        mFavoriteDataSource.reloadRoutes();

        FavoriteRoute favoriteRoute = new FavoriteRoute(route);

        if(route.isFavorited()) {
            mFavoriteDataSource.saveRoute(favoriteRoute);
        }
        else {
            mFavoriteDataSource.deleteRoute(favoriteRoute);
        }
    }

    private void updateViews(List<Train> trains) {
        mView.onTrainRoutesLoaded(trains);
    }

    private boolean hasNoCachedData() {
        return !mTrainRepo.hasCachedData();
    }

    private void useCachedDataIfAvailable(TrainsDataSource repo) {
        if(hasNoCachedData()) {
            repo.reloadTrains();
        }
    }
}
