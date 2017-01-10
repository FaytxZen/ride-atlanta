package com.andrewvora.apps.rideatlanta.home;

import com.andrewvora.apps.rideatlanta.common.AlertItemModel;
import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.common.InfoItemModel;
import com.andrewvora.apps.rideatlanta.common.RouteItemModel;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface HomeContract {

    interface Presenter extends BasePresenter {
        void loadAlerts();
        void loadInfoItems();
        void loadFavoriteRoutes();
    }

    interface View extends BaseView<HomeContract.Presenter> {
        void displayAlerts(AlertItemModel alertItem);
        void displayInfoItems(InfoItemModel infoItem);
        void displayRouteItems(RouteItemModel routeItem);
    }
}
