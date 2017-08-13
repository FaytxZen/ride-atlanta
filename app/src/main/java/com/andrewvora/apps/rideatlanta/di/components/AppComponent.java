package com.andrewvora.apps.rideatlanta.di.components;

import com.andrewvora.apps.rideatlanta.RideAtlantaApplication;
import com.andrewvora.apps.rideatlanta.di.builders.ActivityBuilderModule;
import com.andrewvora.apps.rideatlanta.di.AppModule;
import com.andrewvora.apps.rideatlanta.di.DataModule;
import com.andrewvora.apps.rideatlanta.di.NetworkModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created on 8/12/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
@Singleton
@Component(modules = {
		AndroidInjectionModule.class,
		AppModule.class,
		NetworkModule.class,
		DataModule.class,
		ActivityBuilderModule.class
})
public interface AppComponent {
	@Component.Builder
	interface Builder {
		@BindsInstance Builder application(RideAtlantaApplication app);
		AppComponent build();
	}

	void inject(RideAtlantaApplication application);
}
