package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesPresenter implements FavoriteRoutesContract.Presenter {

    private FavoriteRoutesContract.View mView;
    private FavoriteRoutesDataSource mFavRouteRepo;
    private BroadcastReceiver mRoutesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && intent.getAction() != null) {
                loadRouteInformation();
            }
        }
    };

    public FavoriteRoutesPresenter(@NonNull FavoriteRoutesContract.View view) {
        mView = view;
    }

    @Override
    public void onSaveState(Bundle outState) {

    }

    @Override
    public void onRestoreState(Bundle savedState) {

    }

    @Override
    public void start() {
        loadFavoriteRoutes();

        mView.subscribeReceiver(mRoutesReceiver);
    }

    @Override
    public void stop() {
        mView.unsubscribeReceiver(mRoutesReceiver);
    }

    @Override
    public void loadFavoriteRoutes() {
        mFavRouteRepo = mView.getFavoritesDataSource();
        mFavRouteRepo.reloadRoutes();

        mFavRouteRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                List<FavoriteRouteDataObject> routes = new ArrayList<>();

                for(FavoriteRoute route : favRoutes) {
                    routes.add(route);
                }

                mView.onFavoriteRoutesLoaded(routes);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    @Override
    public void loadRouteInformation() {
        mFavRouteRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                for(FavoriteRoute route : favRoutes) {
                    determineRouteInfoToLoad(route);
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void determineRouteInfoToLoad(@NonNull FavoriteRoute route) {
        if(FavoriteRoute.TYPE_BUS.equals(route.getType())) {
            loadBusInformation(route);
        }
        else if(FavoriteRoute.TYPE_TRAIN.equals(route.getType())) {
            loadTrainInformation(route);
        }
    }

    private void loadBusInformation(@NonNull FavoriteRoute favoriteRoute) {
        Bus bus = new Bus();
        bus.setRouteId(favoriteRoute.getRouteId());

        BusesDataSource busesRepo = mView.getBusesDataSource();
        busesRepo.getBus(bus, new BusesDataSource.GetBusCallback() {
            @Override
            public void onFinished(Bus bus) {
                mView.onRouteInformationLoaded(bus);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void loadTrainInformation(@NonNull FavoriteRoute favoriteRoute) {
        Train train = new Train();
        train.setTrainId(Long.parseLong(favoriteRoute.getRouteId()));
        train.setLine(favoriteRoute.getName());
        train.setStation(favoriteRoute.getDestination());

        TrainsDataSource trainsRepo = mView.getTrainDataSource();
        trainsRepo.getTrain(train, new TrainsDataSource.GetTrainRouteCallback() {
            @Override
            public void onFinished(Train train) {
                mView.onRouteInformationLoaded(train);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }
}
