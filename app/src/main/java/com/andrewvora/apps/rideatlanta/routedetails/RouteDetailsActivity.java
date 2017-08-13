package com.andrewvora.apps.rideatlanta.routedetails;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.di.DataModule;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;

/**
 * Created on 8/13/2017.
 * @author Andrew Vorakrajangthiti
 */
public class RouteDetailsActivity extends Activity {

	@Inject @Named(DataModule.BUS_SOURCE)
	BusesDataSource busRepo;

	@Inject @Named(DataModule.TRAIN_SOURCE)
	TrainsDataSource trainRepo;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		AndroidInjection.inject(this);
		super.onCreate(savedInstanceState);
	}
}
