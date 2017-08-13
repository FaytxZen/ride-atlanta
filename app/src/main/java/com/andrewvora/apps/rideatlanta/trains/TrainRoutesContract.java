package com.andrewvora.apps.rideatlanta.trains;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface TrainRoutesContract {

    interface Presenter extends BasePresenter {
        void refreshTrainRoutes();
        void loadTrainRoutes();
        void favoriteRoute(@NonNull Train route);
        void startPolling();
    }

    interface View extends BaseView<TrainRoutesContract.Presenter> {
        void onTrainRoutesLoaded(List<Train> trainList);
        void applyFavorites(List<FavoriteRouteDataObject> favRoutes);
        void updateFavoriteStatusOf(@NonNull Train train);

        Context getViewContext();
    }
}
