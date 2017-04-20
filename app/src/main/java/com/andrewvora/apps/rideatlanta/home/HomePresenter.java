package com.andrewvora.apps.rideatlanta.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
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
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.NotificationsRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;
import com.andrewvora.apps.rideatlanta.notifications.NotificationsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class HomePresenter implements HomeContract.Presenter {

    private static final int MAX_NOTIFICATIONS = 2;

    @NonNull private HomeContract.View mView;
    private FavoriteRoutesDataSource mFavRoutesRepo;
    private NotificationsDataSource mNotificationRepo;
    private BusesDataSource mBusRepo;
    private TrainsDataSource mTrainRepo;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           loadUpdatedRouteInformation();
        }
    };

    public HomePresenter(@NonNull HomeContract.View view) {
        mView = view;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        mBusRepo = BusesRepo.getInstance(mView.getViewContext());
        mTrainRepo = TrainsRepo.getInstance(mView.getViewContext());
        mFavRoutesRepo = FavoriteRoutesRepo.getInstance(mView.getViewContext());
        mNotificationRepo = NotificationsRepo.getInstance(mView.getViewContext());

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

        final String cachedDataTag = NotificationsPresenter.class.getSimpleName();
        final boolean hasNoCachedData = !CachedDataMap.getInstance().hasCachedData(cachedDataTag);
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
            }

            @Override
            public void onError(Object error) { }
        });
    }

    @Override
    public void loadUpdatedRouteInformation() {
        mFavRoutesRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                for(FavoriteRoute route : favRoutes) {
                    if(route.getType().equals(FavoriteRoute.TYPE_TRAIN)) {
                        loadUpdatedTrainInformation(route);
                    }
                    else if(route.getType().equals(FavoriteRoute.TYPE_BUS)) {
                        loadUpdatedBusInformation(route);
                    }
                }
            }

            @Override
            public void onError(Object error) { }
        });
    }

    private void loadUpdatedBusInformation(@NonNull FavoriteRoute route) {
        Bus bus = new Bus();
        bus.setRouteId(route.getRouteId());

        mBusRepo.getBus(bus, new BusesDataSource.GetBusCallback() {
            @Override
            public void onFinished(Bus bus) {
                updateRouteOnView(new FavoriteRoute(bus));
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void loadUpdatedTrainInformation(@NonNull FavoriteRoute route) {
        Train train = new Train();
        train.setTrainId(Long.parseLong(route.getRouteId()));
        train.setLine(route.getName());
        train.setStation(route.getDestination());

        mTrainRepo.getTrain(train, new TrainsDataSource.GetTrainRouteCallback() {
            @Override
            public void onFinished(Train train) {
                updateRouteOnView(new FavoriteRoute(train));
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
        updateRouteOnDatabase(favoriteRoute);
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
