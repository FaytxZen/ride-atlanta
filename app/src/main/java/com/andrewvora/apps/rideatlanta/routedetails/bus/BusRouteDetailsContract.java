package com.andrewvora.apps.rideatlanta.routedetails.bus;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.List;

/**
 * Created on 12/10/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public interface BusRouteDetailsContract {
	interface Presenter extends BasePresenter {
		void loadBuses();
	}

	interface View extends BaseView<Presenter> {
		void showBusRoutes(@NonNull List<Bus> buses);
	}
}
