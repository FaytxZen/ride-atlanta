package com.andrewvora.apps.rideatlanta.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.InfoAlert;
import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class HomePresenter implements HomeContract.Presenter {

    private static final int MAX_NOTIFICATIONS = 2;

    @NonNull private HomeContract.View mView;
    @NonNull private FavoriteRoutesDataSource mFavRoutesRepo;
    @NonNull private NotificationsDataSource mNotificationRepo;
    @NonNull private BusesDataSource mBusRepo;
    @NonNull private TrainsDataSource mTrainRepo;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           refreshRouteInformation();
        }
    };

    public HomePresenter(@NonNull HomeContract.View view,
                         @NonNull FavoriteRoutesDataSource favRoutesRepo,
                         @NonNull NotificationsDataSource notificationRepo,
                         @NonNull BusesDataSource busRepo,
                         @NonNull TrainsDataSource trainRepo)
    {
        mView = view;
        mFavRoutesRepo = favRoutesRepo;
        mNotificationRepo = notificationRepo;
        mBusRepo = busRepo;
        mTrainRepo = trainRepo;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        loadInfoItems();
        loadAlerts();
        loadFavoriteRoutes();

        mView.subscribeReceiver(mReceiver);
    }

    @Override
    public void stop() {
        mView.unsubscribeReceiver(mReceiver);
    }

    @Override
    public void loadAlerts() {
        final boolean hasNoCachedData = !mNotificationRepo.hasCachedData();

        if(hasNoCachedData) {
            mNotificationRepo.reloadNotifications();
        }

        mNotificationRepo.getNotifications(new NotificationsDataSource.GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
                List<AlertItemModel> alertItems = new ArrayList<>();
                for(int i = 0; i < MAX_NOTIFICATIONS && i < notifications.size(); i++) {
                    alertItems.add(notifications.get(i));
                }

                mView.displayAlerts(alertItems);
            }

            @Override
            public void onError(Object error) { }
        });

    }

    @Override
    public void loadInfoItems() {
        // create item for See & Say
        InfoAlert seeAndSayInfoItem = new InfoAlert();
        String infoText = mView.getViewContext().getString(R.string.text_see_and_say);
        seeAndSayInfoItem.setInfoText(infoText);

        List<InfoItemModel> infoItemModels = new ArrayList<>();
        infoItemModels.add(seeAndSayInfoItem);

        mView.displayInfoItems(infoItemModels);
    }

    @Override
    public void loadFavoriteRoutes() {
        mFavRoutesRepo.reloadRoutes();
        mFavRoutesRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                List<RouteItemModel> routeItems = new ArrayList<>();
                for(FavoriteRoute route : favRoutes) {
                    routeItems.add(route);
                }

                mView.displayRouteItems(routeItems);

                if(mBusRepo.hasCachedData() || mTrainRepo.hasCachedData()) {
                    refreshRouteInformation();
                }
            }

            @Override
            public void onError(Object error) { }
        });
    }

    @Override
    public void refreshRouteInformation() {
        mFavRoutesRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                updateRouteInformation(favRoutes);
            }

            @Override
            public void onError(Object error) { }
        });
    }

    private void updateRouteInformation(@NonNull final List<FavoriteRoute> routes) {
        refreshBusInformation(routes);
        refreshTrainInformation(routes);
    }

    private void refreshBusInformation(@NonNull final List<FavoriteRoute> routes) {
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

                        updateRouteOnView(route);
                    }
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void refreshTrainInformation(@NonNull final List<FavoriteRoute> routes) {
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

                        updateRouteOnView(route);
                    }
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void updateRouteOnView(@NonNull FavoriteRoute favoriteRoute) {
        List<RouteItemModel> routeItemModels = new ArrayList<>();
        routeItemModels.add(favoriteRoute);

        mView.displayRouteItems(routeItemModels);

        // only buses will change their names
        if(favoriteRoute.isBus()) {
            updateRouteOnDatabase(favoriteRoute);
        }
    }

    private void updateRouteOnDatabase(@NonNull final FavoriteRoute favoriteRoute) {
        // make sure this doesn't get marked as a new record
        if(favoriteRoute.getId() == null) {
            favoriteRoute.setId(1L);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mFavRoutesRepo.saveRoute(favoriteRoute);
            }
        });
    }
}
