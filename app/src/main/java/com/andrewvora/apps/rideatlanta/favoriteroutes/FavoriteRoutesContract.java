package com.andrewvora.apps.rideatlanta.favoriteroutes;

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
        void loadFavoriteRoute(@NonNull FavoriteRouteDataObject route);
    }

    interface View extends BaseView<FavoriteRoutesContract.Presenter> {
        void onFavoriteRoutesLoaded(List<FavoriteRouteDataObject> favRoutes);
        void onRouteInformationLoaded(FavoriteRouteDataObject favRoute);
    }
}
