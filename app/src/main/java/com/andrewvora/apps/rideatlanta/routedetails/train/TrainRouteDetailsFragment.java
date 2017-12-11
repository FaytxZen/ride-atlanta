package com.andrewvora.apps.rideatlanta.routedetails.train;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.di.DataModule;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;

/**
 * Created on 8/13/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class TrainRouteDetailsFragment extends Fragment {

	public static final String EXTRA_TRAIN_LINE = "trainLineName";
	public static final String EXTRA_TRAIN_DESTINATION = "trainDestination";

	@Inject
	@Named(DataModule.TRAIN_SOURCE)
	TrainsDataSource trainRepo;

	public static TrainRouteDetailsFragment newInstance(@NonNull String line, @NonNull String destination) {
		final Bundle extras = new Bundle();
		extras.putString(EXTRA_TRAIN_LINE, line);
		extras.putString(EXTRA_TRAIN_DESTINATION, destination);

		final TrainRouteDetailsFragment fragment = new TrainRouteDetailsFragment();
		fragment.setArguments(extras);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		AndroidInjection.inject(this);
		super.onCreate(savedInstanceState);
	}
}
