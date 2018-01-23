package com.andrewvora.apps.rideatlanta;

import android.app.Activity;
import android.app.Application;

import com.andrewvora.apps.rideatlanta.di.components.DaggerAppComponent;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.fabric.sdk.android.Fabric;

/**
 * Created by faytx on 11/11/2016.
 * @author Andrew Vorakrajangthiti
 */
public class RideAtlantaApplication extends Application implements HasActivityInjector {

	public static final boolean USE_LOCAL = true;

	@Inject
	DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        final String twitterKey = BuildConfig.TWITTER_API_KEY;
        final String twitterSecret = BuildConfig.TWITTER_SECRET;

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(twitterKey, twitterSecret);
        Fabric.with(this, new Twitter(twitterAuthConfig));

		DaggerAppComponent.builder().application(this).build().inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}
