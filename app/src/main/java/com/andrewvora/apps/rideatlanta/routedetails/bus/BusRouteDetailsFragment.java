package com.andrewvora.apps.rideatlanta.routedetails.bus;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.di.DataModule;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;

/**
 * Created on 8/13/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class BusRouteDetailsFragment extends Fragment {

	@Inject
	@Named(DataModule.BUS_SOURCE)
	BusesDataSource busRepo;

	public static BusRouteDetailsFragment newInstance(@NonNull String id, @NonNull String destination) {
		return new BusRouteDetailsFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		AndroidInjection.inject(this);
		super.onCreate(savedInstanceState);
	}
}
