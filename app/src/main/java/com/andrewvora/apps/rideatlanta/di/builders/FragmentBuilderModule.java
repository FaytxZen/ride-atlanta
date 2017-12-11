package com.andrewvora.apps.rideatlanta.di.builders;

import com.andrewvora.apps.rideatlanta.buses.BusRoutesFragment;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesFragment;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesLoadingCache;
import com.andrewvora.apps.rideatlanta.home.HomeFragment;
import com.andrewvora.apps.rideatlanta.routedetails.bus.BusRouteDetailsFragment;
import com.andrewvora.apps.rideatlanta.routedetails.train.TrainRouteDetailsFragment;
import com.andrewvora.apps.rideatlanta.trains.TrainRoutesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created on 8/12/2017.
 * @author Andrew Vorakrajangthiti
 */
@Module
abstract class FragmentBuilderModule {

	@ContributesAndroidInjector
	abstract FavoriteRoutesLoadingCache contributesFavoriteCacheFragmentInjector();

	@ContributesAndroidInjector
	abstract FavoriteRoutesFragment contributesFavoriteRoutesFragmentInjector();

	@ContributesAndroidInjector
	abstract HomeFragment contributesHomeFragmentInjector();

	@ContributesAndroidInjector
	abstract BusRoutesFragment contributesBusFragmentInjector();

	@ContributesAndroidInjector
	abstract TrainRoutesFragment contributesTrainFragmentInjector();

	@ContributesAndroidInjector
	abstract BusRouteDetailsFragment contributesBusDetailsFragmentInjector();

	@ContributesAndroidInjector
	abstract TrainRouteDetailsFragment contributesTrainDetailsFragmentInjector();
}
