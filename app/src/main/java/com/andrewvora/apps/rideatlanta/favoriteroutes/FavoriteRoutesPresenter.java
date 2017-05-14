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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                refreshRouteInformation();
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

                if(mTrainRepo.hasCachedData() || mBusRepo.hasCachedData()) {
                    refreshRouteInformation();
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    @Override
    public void refreshRouteInformation() {
        mFavRouteRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                updateRouteInformation(favRoutes);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void updateRouteInformation(@NonNull final List<FavoriteRoute> routes) {
        mBusRepo.getBuses(new BusesDataSource.GetBusesCallback() {
            @Override
            public void onFinished(List<Bus> buses) {
                Map<String, Bus> busMap = new HashMap<>();

                for (Bus bus : buses) {
                    busMap.put(bus.getFavoriteRouteKey(), bus);
                }

                for(FavoriteRoute route : routes) {
                    if(route.isBus() && busMap.containsKey(route.getFavoriteRouteKey())) {
                        Bus bus = busMap.get(route.getFavoriteRouteKey());

                        route.setName(bus.getName());
                        route.setDestination(bus.getDestination());
                        route.setTimeUntilArrival(bus.getTimeTilArrival());

                        updateRouteOnDatabase(route);
                        mView.onRouteInformationLoaded(route);
                    }
                }
            }

            @Override
            public void onError(Object error) {

            }
        });

        mTrainRepo.getTrains(new TrainsDataSource.GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                Map<String, List<Train>> trainMap = new HashMap<>();

                for(Train train : trainList) {
                    String key = train.getFavoriteRouteKey();

                    if(trainMap.containsKey(key)) {
                        trainMap.get(key).add(train);
                    }
                    else {
                        List<Train> matching = new ArrayList<>();
                        matching.add(train);

                        trainMap.put(key, matching);
                    }
                }

                for(FavoriteRoute route : routes) {
                    if(!route.isBus() && trainMap.containsKey(route.getFavoriteRouteKey())) {
                        List<Train> list = trainMap.get(route.getFavoriteRouteKey());
                        String arrivalTime = Train.combineArrivalTimes(list);

                        route.setTimeUntilArrival(arrivalTime);

                        updateRouteOnDatabase(route);
                        mView.onRouteInformationLoaded(route);
                    }
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
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
