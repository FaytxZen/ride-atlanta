package com.andrewvora.apps.rideatlanta.di;

import android.app.Application;

import com.andrewvora.apps.rideatlanta.RideAtlantaApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 8/7/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
@Module
public class AppModule {

    @Provides
    Application providesApplication(RideAtlantaApplication application) {
        return application;
    }
}
