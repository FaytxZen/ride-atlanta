package com.andrewvora.apps.rideatlanta.home;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface HomeContract {

    interface Presenter extends BasePresenter {
        void loadAlerts();
        void loadInfoItems();
        void loadFavoriteRoutes();
        void refreshRouteInformation();
		void startPolling();
    }

    interface View extends BaseView<HomeContract.Presenter> {
        Context getViewContext();

        void displayAlerts(@NonNull List<AlertItemModel> alertItems);
        void displayInfoItems(@NonNull List<InfoItemModel> infoItems);
        void displayRouteItems(@NonNull List<RouteItemModel> routeItems);
    }
}
