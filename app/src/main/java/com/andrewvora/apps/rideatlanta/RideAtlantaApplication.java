package com.andrewvora.apps.rideatlanta;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by faytx on 11/11/2016.
 * @author Andrew Vorakrajangthiti
 */

public class RideAtlantaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final String twitterKey = BuildConfig.TWITTER_API_KEY;
        final String twitterSecret = BuildConfig.TWITTER_SECRET;

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(twitterKey, twitterSecret);

        Fabric.with(this, new Twitter(twitterAuthConfig));
    }
}
