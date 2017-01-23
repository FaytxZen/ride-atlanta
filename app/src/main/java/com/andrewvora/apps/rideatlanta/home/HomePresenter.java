package com.andrewvora.apps.rideatlanta.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.InfoAlert;
import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.NotificationsRepo;
import com.andrewvora.apps.rideatlanta.notifications.NotificationsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class HomePresenter implements HomeContract.Presenter {

    private static final int MAX_NOTIFICATIONS = 2;

    @NonNull private Context mContext;
    @NonNull private HomeContract.View mView;

    public HomePresenter(@NonNull Context context, @NonNull HomeContract.View view) {
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

        loadAlerts();
        loadInfoItems();
        loadFavoriteRoutes();
    }

    @Override
    public void loadAlerts() {
        NotificationsRepo alertRepo = NotificationsRepo.getInstance(mContext);

        final String cachedDataTag = NotificationsPresenter.class.getSimpleName();
        final boolean hasNoCachedData = !CachedDataMap.getInstance().hasCachedData(cachedDataTag);
        if(hasNoCachedData) {
            alertRepo.reloadNotifications();
        }

        alertRepo.getNotifications(new NotificationsDataSource.GetNotificationsCallback() {
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
        seeAndSayInfoItem.setInfoText(mContext.getString(R.string.text_see_and_say));

        List<InfoItemModel> infoItemModels = new ArrayList<>();
        infoItemModels.add(seeAndSayInfoItem);

        mView.displayInfoItems(infoItemModels);
    }

    @Override
    public void loadFavoriteRoutes() {
        FavoriteRoutesRepo repo = FavoriteRoutesRepo.getInstance(mContext);
        repo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
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
}
