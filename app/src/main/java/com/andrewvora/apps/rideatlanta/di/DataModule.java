package com.andrewvora.apps.rideatlanta.di;

import android.app.Application;

import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.buses.BusesLocalSource;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesLocalSource;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsLocalSource;
import com.andrewvora.apps.rideatlanta.data.remote.buses.BusesRemoteSource;
import com.andrewvora.apps.rideatlanta.data.remote.marta.MartaService;
import com.andrewvora.apps.rideatlanta.data.remote.notifications.NotificationsRemoteSource;
import com.andrewvora.apps.rideatlanta.data.remote.routes.FavoriteRoutesRemoteSource;
import com.andrewvora.apps.rideatlanta.data.remote.trains.TrainsRemoteSource;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.NotificationsRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;
import com.andrewvora.apps.rideatlanta.utils.InputStreamConverter;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterApiClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.andrewvora.apps.rideatlanta.di.NetworkModule.MARTA_API_KEY;

/**
 * Created on 8/7/2017.
 * @author Andrew Vorakrajangthiti
 */
@Module
public class DataModule {
    public static final String TRAIN_SOURCE = "trains_repo";
    public static final String BUS_SOURCE = "buses_repo";
	public static final String FAVS_SOURCE = "favs_repo";
	public static final String NOTIFICATION_SOURCE = "notification_repo";

    private static final String TRAIN_REMOTE_SOURCE = "trains_remote";
    private static final String TRAIN_LOCAL_SOURCE = "trains_local";

    private static final String BUS_REMOTE_SOURCE = "buses_remote";
    private static final String BUS_LOCAL_SOURCE = "buses_local";

	private static final String FAVS_REMOTE_SOURCE = "favs_remote";
	private static final String FAVS_LOCAL_SOURCE = "favs_local";

	private static final String NOTIFICATION_REMOTE_SOURCE = "notification_remote";

	private static final boolean USE_LOCAL = true;

	@Provides
	@Singleton
	RoutePollingHelper providesPollingHelper(@Named(TRAIN_SOURCE) TrainsDataSource trainSource,
											 @Named(BUS_SOURCE) BusesDataSource busSource,
											 @Named(NOTIFICATION_SOURCE) NotificationsDataSource notificationSource)
	{
		return new RoutePollingHelper(busSource, trainSource, notificationSource);
	}

    @Provides
    @Singleton
    @Named(TRAIN_SOURCE)
    TrainsDataSource providesTrainSource(@Named(TRAIN_REMOTE_SOURCE) TrainsDataSource remote,
                                         @Named(TRAIN_LOCAL_SOURCE) TrainsDataSource local)
    {
        return new TrainsRepo(remote, local);
    }

    @Provides
    @Singleton
    @Named(BUS_SOURCE)
    BusesDataSource providesBusSource(@Named(BUS_REMOTE_SOURCE) BusesDataSource remote,
                                      @Named(BUS_LOCAL_SOURCE) BusesDataSource local)
	{
        return new BusesRepo(remote, local);
    }

    @Provides
	@Singleton
	@Named(FAVS_SOURCE)
	FavoriteRoutesDataSource providesFavSource(@Named(FAVS_REMOTE_SOURCE) FavoriteRoutesDataSource remote,
											   @Named(FAVS_LOCAL_SOURCE) FavoriteRoutesDataSource local)
	{
		return new FavoriteRoutesRepo(remote, local);
	}

	@Provides
	@Singleton
	@Named(NOTIFICATION_SOURCE)
	NotificationsDataSource providesNotificationSource(
			@Named(NOTIFICATION_REMOTE_SOURCE) NotificationsDataSource remote)
	{
		return new NotificationsRepo(remote);
	}

    @Provides
    @Singleton
    @Named(TRAIN_REMOTE_SOURCE)
    TrainsDataSource providesTrainRemoteSource(@Named(MARTA_API_KEY) String apiKey,
                                               Gson gson,
                                               Application app,
                                               MartaService service)
    {
        return new TrainsRemoteSource(app, apiKey, service, gson, new InputStreamConverter());
    }

    @Provides
    @Singleton
    @Named(TRAIN_LOCAL_SOURCE)
    TrainsDataSource providesTrainLocalSource(Application app) {
        return new TrainsLocalSource(app);
    }

    @Provides
    @Singleton
    @Named(BUS_REMOTE_SOURCE)
    BusesDataSource providesBusesRemoteSource(Application app, MartaService service, Gson gson) {
        return new BusesRemoteSource(app, service, gson, new InputStreamConverter());
    }

    @Provides
    @Singleton
    @Named(BUS_LOCAL_SOURCE)
    BusesDataSource providesBusesLocalSource(Application app) {
        return new BusesLocalSource(app);
    }

    @Provides
	@Singleton
	@Named(FAVS_REMOTE_SOURCE)
    FavoriteRoutesDataSource providesFavoritesRemoteSource(@Named(BUS_REMOTE_SOURCE) BusesDataSource busRemote,
														   @Named(TRAIN_REMOTE_SOURCE) TrainsDataSource trainRemote)
	{
        return new FavoriteRoutesRemoteSource(busRemote, trainRemote);
    }

	@Provides
	@Singleton
	@Named(FAVS_LOCAL_SOURCE)
	FavoriteRoutesDataSource providesFavoritesLocalSource(Application app) {
		return new FavoriteRoutesLocalSource(app);
	}

	@Provides
	@Singleton
	@Named(NOTIFICATION_REMOTE_SOURCE)
	NotificationsDataSource providesNotificationRemoteSource(TwitterApiClient client) {
		return new NotificationsRemoteSource(client);
	}
}
