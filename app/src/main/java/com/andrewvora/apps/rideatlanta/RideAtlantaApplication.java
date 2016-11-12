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

    private TwitterAuthConfig mTwitterAuthConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        final String twitterKey = getString(R.string.twitter_key);
        final String twitterSecret = getString(R.string.twitter_secret);
        mTwitterAuthConfig = new TwitterAuthConfig(twitterKey, twitterSecret);
        Fabric.with(this, new Twitter(mTwitterAuthConfig));
    }
}
