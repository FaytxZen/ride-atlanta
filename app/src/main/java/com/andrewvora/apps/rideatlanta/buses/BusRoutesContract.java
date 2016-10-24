package com.andrewvora.apps.rideatlanta.buses;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.common.models.Bus;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface BusRoutesContract {

    interface Presenter extends BasePresenter {
        void loadBusRoutes();
    }

    interface View extends BaseView<BusRoutesContract.Presenter> {
        void onBusRoutesLoaded(List<Bus> routesList);
    }
}
