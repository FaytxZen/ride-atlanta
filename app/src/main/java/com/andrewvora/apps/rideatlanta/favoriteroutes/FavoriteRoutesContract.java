package com.andrewvora.apps.rideatlanta.favoriteroutes;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.common.FavoriteRouteDataObject;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface FavoriteRoutesContract {

    interface Presenter extends BasePresenter {
        void loadFavoriteRoutes();
    }

    interface View extends BaseView<FavoriteRoutesContract.Presenter> {
        void onFavoriteRoutesLoaded(List<FavoriteRouteDataObject> favRoutes);
    }
}
