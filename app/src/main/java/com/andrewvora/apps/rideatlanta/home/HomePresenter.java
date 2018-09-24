package com.andrewvora.apps.rideatlanta.home;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.FavoritesHelper;
import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.InfoAlert;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class HomePresenter implements HomeContract.Presenter {

    private static final int MAX_NOTIFICATIONS = 2;

    @NonNull private HomeContract.View view;
    @NonNull private FavoriteRoutesDataSource favsRepo;
    @NonNull private NotificationsDataSource notificationRepo;
    @NonNull private BusesDataSource busRepo;
    @NonNull private TrainsDataSource trainRepo;
	@NonNull private RoutePollingHelper pollingHelper;
	@NonNull private CompositeDisposable disposables;
	@NonNull private FavoritesHelper favoritesHelper;

	private boolean initialized;

    public HomePresenter(@NonNull HomeContract.View view,
						 @NonNull FavoriteRoutesDataSource favRoutesRepo,
						 @NonNull NotificationsDataSource notificationRepo,
						 @NonNull BusesDataSource busRepo,
						 @NonNull TrainsDataSource trainRepo,
						 @NonNull RoutePollingHelper pollingHelper,
                         @NonNull FavoritesHelper favoritesHelper)
    {
        this.view = view;
        this.favsRepo = favRoutesRepo;
        this.notificationRepo = notificationRepo;
        this.busRepo = busRepo;
        this.trainRepo = trainRepo;
		this.disposables = new CompositeDisposable();
		this.pollingHelper = pollingHelper;
		this.favoritesHelper = favoritesHelper;
    }

    @Override
    public void start() {
    	if (!initialized) {
    		view.showLoadingIndicator();
	    }

		loadHomeItems();
    }

    @Override
    public void stop() {
    	view.hideLoadingIndicator();
		disposables.dispose();
		disposables.clear();
    }

	@Override
	public void loadHomeItems() {
    	// load notifications
		notificationRepo.reloadNotifications();
		final Observable<List<Notification>> notificationStream = notificationRepo.getNotifications();

		// load routes
		favsRepo.reloadRoutes();
		final Observable<List<FavoriteRoute>> routesStream = favsRepo.getFavoriteRoutes();

		// load info alerts
		final InfoAlert seeAndSayInfoItem = new InfoAlert();
		final String infoText = view.getViewContext().getString(R.string.text_see_and_say);
		seeAndSayInfoItem.setInfoText(infoText);

		final List<InfoItemModel> infoItemModels = new ArrayList<>();
		infoItemModels.add(seeAndSayInfoItem);
		final Observable<List<InfoItemModel>> infoAlertStream = Observable.just(infoItemModels);

		disposables.add(Observable.zip(notificationStream, routesStream, infoAlertStream,
				(notifications, favoriteRoutes, infoItems) -> {
					initialized = true;

					final List<HomeItemModel> itemModels = new ArrayList<>();
					itemModels.addAll(notifications.subList(0, MAX_NOTIFICATIONS));
					itemModels.addAll(favoriteRoutes);
					itemModels.addAll(infoItems);
					return itemModels;
				})
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		.subscribeWith(new DisposableObserver<List<HomeItemModel>>() {
			@Override
			public void onNext(List<HomeItemModel> homeItemModels) {
				view.hideLoadingIndicator();
				view.displayItems(homeItemModels);
				startPolling();
				refreshRouteInformationIfCached();
			}

			@Override public void onError(Throwable e) {
				view.hideLoadingIndicator();
			}
			@Override public void onComplete() {
				view.hideLoadingIndicator();
			}
		}));
	}

	@Override
	public void startPolling() {
		startRoutePolling();
		startNotificationPolling();
	}

	private void startRoutePolling() {
		disposables.add(pollingHelper.getBusStream()
				.delay(15, TimeUnit.SECONDS)
				.zipWith(pollingHelper.getTrainStream(), (integer, integer2) -> integer + integer2)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<Integer>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull Integer num) {
						refreshRouteInformationIfCached();
					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onComplete() {
						refreshRouteInformationIfCached();
					}
				}));
	}

	private void startNotificationPolling() {
		disposables.add(pollingHelper.getNotificationStream()
				.delay(15, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<Integer>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull Integer num) {
						// TODO: update notifications
					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

					@Override
					public void onComplete() { }
				}));
	}

	private void refreshRouteInformationIfCached() {
		refreshRouteInformation();
	}

    @Override
    public void refreshRouteInformation() {
        disposables.add(favsRepo.getFavoriteRoutes()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> routes) {
					updateRouteInformation(routes);
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

				@Override
				public void onComplete() { }
			}));
    }

    private void updateRouteInformation(@NonNull final List<FavoriteRoute> routes) {
        refreshBusInformation(routes);
        refreshTrainInformation(routes);
    }

    private void refreshBusInformation(@NonNull final List<FavoriteRoute> routes) {
        final Disposable disposable = Observable.zip(busRepo.getBuses(), favsRepo.getFavoriteRoutes(),
	        (buses, favorites) -> {
        	    favoritesHelper.applyBusesToFavorites(buses, favorites);
                return favorites;
	        })
	        .flatMap((Observable::fromIterable))
	        .toList()
	        .toObservable()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> buses) {
					for(FavoriteRoute route : routes) {
						if(route.isBus()) {
							updateRouteOnView(route);
						}
					}
				}

				@Override public void onError(@io.reactivex.annotations.NonNull Throwable e) { }
				@Override public void onComplete() { }
			});

		disposables.add(disposable);
    }

    private void refreshTrainInformation(@NonNull final List<FavoriteRoute> routes) {
        final Disposable disposable = Observable.zip(trainRepo.getTrains(), favsRepo.getFavoriteRoutes(),
	        (trains, favoriteRoutes) -> {
        	    favoritesHelper.applyTrainsToFavorites(trains, favoriteRoutes);
        	    return favoriteRoutes;
	        })
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> trains) {
					for(FavoriteRoute route : routes) {
						if(!route.isBus()) {
							updateRouteOnView(route);
						}
					}
				}

				@Override public void onError(@io.reactivex.annotations.NonNull Throwable e) { }
				@Override public void onComplete() { }
			});

		disposables.add(disposable);
    }

    private void updateRouteOnView(@NonNull FavoriteRoute favoriteRoute) {
        List<HomeItemModel> itemModels = new ArrayList<>();
        itemModels.add(favoriteRoute);

        view.updateItems(itemModels);
        view.hideLoadingIndicator();
    }
}
