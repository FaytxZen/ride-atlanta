package com.andrewvora.apps.rideatlanta.trains;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.common.models.Train;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface TrainRoutesContract {

    interface Presenter extends BasePresenter {
        void loadTrainRoutes();
    }

    interface View extends BaseView<TrainRoutesContract.Presenter> {
        void onTrainRoutesLoaded(List<Train> trainList);
    }
}
