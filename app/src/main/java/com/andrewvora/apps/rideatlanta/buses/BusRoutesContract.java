package com.andrewvora.apps.rideatlanta.buses;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface BusRoutesContract {

    interface Presenter extends BasePresenter {
        void loadBusRoutes();
        void refreshBusRoutes();
        void favoriteRoute(int position, @NonNull Bus bus);
        void startPolling();
    }

    interface View extends BaseView<BusRoutesContract.Presenter> {
    	void onRouteUpdated(int position, @NonNull Bus bus);
        void onBusRoutesLoaded(List<Bus> routesList);
        void refreshError(Throwable e);
        void hideLoadingView();

	    void showEmptyState();
        void hideEmptyState();
        void favoriteError();

        Context getViewContext();
    }
}
