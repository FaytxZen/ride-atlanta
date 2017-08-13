package com.andrewvora.apps.rideatlanta.di;

import com.andrewvora.apps.rideatlanta.BuildConfig;
import com.andrewvora.apps.rideatlanta.data.remote.marta.MartaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 8/7/2017.
 * @author Andrew Vorakrajangthiti
 */
@Module
public class NetworkModule {

    public static final String MARTA_API_KEY = "marta_api_key";

    @Provides
    @Named(MARTA_API_KEY)
    String providesApiKey() {
        return BuildConfig.MARTA_API_KEY;
    }

    @Provides
    Gson providesGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    MartaService providesMartaService(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(MartaService.SERVICE_URL)
                .build()
                .create(MartaService.class);
    }

    @Provides
    @Singleton
	TwitterApiClient providesNotificationService() {
        return TwitterCore.getInstance().getApiClient();
    }
}
