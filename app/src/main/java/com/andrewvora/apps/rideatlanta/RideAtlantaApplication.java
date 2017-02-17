package com.andrewvora.apps.rideatlanta;

import android.app.Application;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by faytx on 11/11/2016.
 * @author Andrew Vorakrajangthiti
 */

public class RideAtlantaApplication extends Application {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static BusesDataSource sBusRepo;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static TrainsDataSource sTrainRepo;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static FavoriteRoutesDataSource sFavRouteRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        final String twitterKey = BuildConfig.TWITTER_API_KEY;
        final String twitterSecret = BuildConfig.TWITTER_SECRET;

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(twitterKey, twitterSecret);

        Fabric.with(this, new Twitter(twitterAuthConfig));

        sBusRepo = BusesRepo.getInstance(this);
        sTrainRepo = TrainsRepo.getInstance(this);
        sFavRouteRepo = FavoriteRoutesRepo.getInstance(this);
    }
}
