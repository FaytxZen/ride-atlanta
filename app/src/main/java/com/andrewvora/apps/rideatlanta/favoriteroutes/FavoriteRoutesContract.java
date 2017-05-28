package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface FavoriteRoutesContract {

    interface Presenter extends BasePresenter {
        void loadFavoriteRoutes();
        void refreshRouteInformation();
    }

    interface View extends BaseView<FavoriteRoutesContract.Presenter> {
        void onFavoriteRoutesLoaded(List<FavoriteRouteDataObject> favRoutes);
        void onRouteInformationLoaded(FavoriteRouteDataObject favRoute);
        void subscribeReceiver(@NonNull BroadcastReceiver receiver);
        void unsubscribeReceiver(@NonNull BroadcastReceiver receiver);

        Context getViewContext();
    }

    interface LoadingCache {
        void setListener(DataLoadedListener listener);
        DataLoadedListener getListener();

        void loadFavoriteRoutes();
        void setFavoritedRoutes(@NonNull List<FavoriteRouteDataObject> favRoutes);
    }

    interface DataLoadedListener {
        void onFavoriteRoutesLoaded(@NonNull List<FavoriteRouteDataObject> favRoutes);
    }
}
