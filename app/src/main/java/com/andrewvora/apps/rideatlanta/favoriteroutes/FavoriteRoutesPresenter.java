package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.BroadcastReceiver;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Contains business logic for {@link FavoriteRoutesContract.View}
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesPresenter implements FavoriteRoutesContract.Presenter {

    @NonNull private FavoriteRoutesContract.View mView;
    @NonNull private FavoriteRoutesDataSource mFavRouteRepo;
    @NonNull private BusesDataSource mBusRepo;
    @NonNull private TrainsDataSource mTrainRepo;

    private BroadcastReceiver mRoutesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && intent.getAction() != null) {
                loadRouteInformation();
            }
        }
    };

    public FavoriteRoutesPresenter(@NonNull FavoriteRoutesContract.View view,
                                   @NonNull FavoriteRoutesDataSource favRepo,
                                   @NonNull BusesDataSource busRepo,
                                   @NonNull TrainsDataSource trainRepo)
    {
        mView = view;
        mFavRouteRepo = favRepo;
        mBusRepo = busRepo;
        mTrainRepo = trainRepo;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

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
        mFavRouteRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                List<FavoriteRouteDataObject> routes = new ArrayList<>();

                // load saved routes
                for(FavoriteRoute route : favRoutes) {
                    routes.add(route);
                }

                // display on UI
                mView.onFavoriteRoutesLoaded(routes);

                // attempt to load their most recent data
                for(FavoriteRoute route : favRoutes) {
                    if(route.isBus()) {
                        loadBusInformation(route);
                    }
                    else {
                        loadTrainInformation(route);
                    }
                }
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

    private void loadBusInformation(@NonNull final FavoriteRoute favoriteRoute) {
        Bus bus = new Bus();
        bus.setRouteId(favoriteRoute.getRouteId());

        mBusRepo.getBus(bus, new BusesDataSource.GetBusCallback() {
            @Override
            public void onFinished(Bus bus) {
                FavoriteRoute routeToSave = new FavoriteRoute(bus);
                routeToSave.setId(favoriteRoute.getId());

                updateRouteOnDatabase(routeToSave);
                mView.onRouteInformationLoaded(bus);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void loadTrainInformation(@NonNull final FavoriteRoute favoriteRoute) {
        final Train train = new Train();
        train.setTrainId(Long.parseLong(favoriteRoute.getRouteId()));
        train.setLine(favoriteRoute.getName());
        train.setStation(favoriteRoute.getDestination());

        mTrainRepo.getTrain(train, new TrainsDataSource.GetTrainRouteCallback() {
            @Override
            public void onFinished(Train train) {
                FavoriteRoute routeToSave = new FavoriteRoute(train);
                routeToSave.setId(favoriteRoute.getId());

                updateTrainArrivalTime(train);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void updateTrainArrivalTime(final Train train) {
        TrainsDataSource.GetTrainRoutesCallback callback = new TrainsDataSource.GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                StringBuilder sb = new StringBuilder();

                for(int i = 0; i < trainList.size(); i++) {
                    if(i != 0) {
                        sb.append(", ");
                    }

                    sb.append(trainList.get(i).getTimeTilArrival());
                }

                Train trainToLoad = train.getCopy();
                trainToLoad.setWaitingTime(sb.toString());

                mView.onRouteInformationLoaded(trainToLoad);
            }

            @Override
            public void onError(Object error) {

            }
        };

        mTrainRepo.getTrains(train.getStation(), train.getLine(), callback);
    }

    private void updateRouteOnDatabase(@NonNull final FavoriteRoute favoriteRoute) {
        // make sure it's not identified as a new route
        if(favoriteRoute.getId() == null) {
            favoriteRoute.setId(1L);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mFavRouteRepo.saveRoute(favoriteRoute);
            }
        });
    }
}
