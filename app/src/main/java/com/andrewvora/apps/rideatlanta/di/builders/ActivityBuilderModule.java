package com.andrewvora.apps.rideatlanta.di.builders;

import com.andrewvora.apps.rideatlanta.MainActivity;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created on 8/12/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
@Module
public abstract class ActivityBuilderModule {

	@ContributesAndroidInjector(modules = {FragmentBuilderModule.class})
	abstract MainActivity contributesMainActivityInjector();

	@ContributesAndroidInjector(modules = {FragmentBuilderModule.class})
	abstract RouteDetailsActivity contributesRouteDetailsActivityInjector();
}
