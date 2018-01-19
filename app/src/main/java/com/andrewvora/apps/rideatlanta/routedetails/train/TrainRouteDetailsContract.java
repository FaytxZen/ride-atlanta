package com.andrewvora.apps.rideatlanta.routedetails.train;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

/**
 * Created on 12/10/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public interface TrainRouteDetailsContract {
	interface Presenter extends BasePresenter {
		void loadTrains();
	}

	interface View extends BaseView<Presenter> {
		void showTrains(@NonNull List<Train> trains);
	}
}
