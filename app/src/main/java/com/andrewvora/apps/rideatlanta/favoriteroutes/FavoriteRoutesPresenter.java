package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesPresenter implements FavoriteRoutesContract.Presenter {

    private Context mContext;
    private FavoriteRoutesContract.View mView;

    public FavoriteRoutesPresenter(@NonNull Context context,
                                   @NonNull FavoriteRoutesContract.View view)
    {
        mContext = context;
        mView = view;
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {

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
    }

    @Override
    public void loadFavoriteRoutes() {
        FavoriteRoutesRepo repo = FavoriteRoutesRepo.getInstance(mContext);
        repo.reloadRoutes();

        repo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                List<FavoriteRouteDataObject> routes = new ArrayList<>();

                for(FavoriteRoute route : favRoutes) {
                    routes.add(route);
                    //loadRouteInformationAsync(routes);
                }

                mView.onFavoriteRoutesLoaded(routes);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    @Override
    public void loadFavoriteRoute(@NonNull FavoriteRouteDataObject route) {

        if(FavoriteRoute.TYPE_BUS.equals(route.getType())) {
            Bus bus = new Bus();
            bus.setRouteId(route.getRouteId());

            BusesRepo busesRepo = BusesRepo.getInstance(mContext);
            busesRepo.reloadBuses();
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
        else if(FavoriteRoute.TYPE_TRAIN.equals(route.getType())) {
            Train train = new Train();
            train.setTrainId(Long.parseLong(route.getRouteId()));

            TrainsRepo trainsRepo = TrainsRepo.getInstance(mContext);
            trainsRepo.reloadTrains();
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

    private void loadRouteInformationAsync(@NonNull final List<FavoriteRouteDataObject> routes) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for(FavoriteRouteDataObject route : routes) {
                    loadFavoriteRoute(route);
                }
            }
        });
    }
}
