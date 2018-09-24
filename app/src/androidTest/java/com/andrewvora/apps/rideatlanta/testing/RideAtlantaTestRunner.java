package com.andrewvora.apps.rideatlanta.testing;

import android.support.test.runner.AndroidJUnitRunner;

import com.squareup.rx2.idler.Rx2Idler;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created on 2/19/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
public class RideAtlantaTestRunner extends AndroidJUnitRunner {
	@Override
	public void onStart() {
		RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("IO Scheduler"));
		RxJavaPlugins.setInitComputationSchedulerHandler(Rx2Idler.create("Computation Scheduler"));
		RxJavaPlugins.setInitNewThreadSchedulerHandler(Rx2Idler.create("New Thread Scheduler"));
		RxJavaPlugins.setInitSingleSchedulerHandler(Rx2Idler.create("Single Scheduler"));
		super.onStart();
	}
}
