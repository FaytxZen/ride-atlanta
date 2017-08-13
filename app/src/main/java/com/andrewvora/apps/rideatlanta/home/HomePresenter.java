package com.andrewvora.apps.rideatlanta.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.InfoAlert;
import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
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

    public HomePresenter(@NonNull HomeContract.View view,
						 @NonNull FavoriteRoutesDataSource favRoutesRepo,
						 @NonNull NotificationsDataSource notificationRepo,
						 @NonNull BusesDataSource busRepo,
						 @NonNull TrainsDataSource trainRepo,
						 @NonNull RoutePollingHelper pollingHelper)
    {
        this.view = view;
        this.favsRepo = favRoutesRepo;
        this.notificationRepo = notificationRepo;
        this.busRepo = busRepo;
        this.trainRepo = trainRepo;
		this.disposables = new CompositeDisposable();
		this.pollingHelper = pollingHelper;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        loadInfoItems();
        loadAlerts();
        loadFavoriteRoutes();
    }

    @Override
    public void stop() {
		disposables.dispose();
		disposables.clear();
    }

    @Override
    public void loadAlerts() {
        final boolean hasNoCachedData = !notificationRepo.hasCachedData();

        if(hasNoCachedData) {
            notificationRepo.reloadNotifications();
        }

        notificationRepo.getNotifications(new NotificationsDataSource.GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
                List<AlertItemModel> alertItems = new ArrayList<>();
                for(int i = 0; i < MAX_NOTIFICATIONS && i < notifications.size(); i++) {
                    alertItems.add(notifications.get(i));
                }

                view.displayAlerts(alertItems);
            }

            @Override
            public void onError(Object error) { }
        });
    }

    @Override
    public void loadInfoItems() {
        // create item for See & Say
        InfoAlert seeAndSayInfoItem = new InfoAlert();
        String infoText = view.getViewContext().getString(R.string.text_see_and_say);
        seeAndSayInfoItem.setInfoText(infoText);

        List<InfoItemModel> infoItemModels = new ArrayList<>();
        infoItemModels.add(seeAndSayInfoItem);

        view.displayInfoItems(infoItemModels);
    }

    @Override
    public void loadFavoriteRoutes() {
        favsRepo.reloadRoutes();
        favsRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                List<RouteItemModel> routeItems = new ArrayList<>();
                for(FavoriteRoute route : favRoutes) {
                    routeItems.add(route);
                }

                view.displayRouteItems(routeItems);

				refreshRouteInformationIfCached();
            }

            @Override
            public void onError(Object error) { }
        });
    }

	private void refreshRouteInformationIfCached() {
		if(busRepo.hasCachedData() || trainRepo.hasCachedData()) {
			refreshRouteInformation();
		}
	}

    @Override
    public void refreshRouteInformation() {
        favsRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                updateRouteInformation(favRoutes);
            }

            @Override
            public void onError(Object error) { }
        });
    }

    private void updateRouteInformation(@NonNull final List<FavoriteRoute> routes) {
        refreshBusInformation(routes);
        refreshTrainInformation(routes);
    }

    private void refreshBusInformation(@NonNull final List<FavoriteRoute> routes) {
        final Disposable disposable = busRepo.getBuses()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<Bus>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<Bus> buses) {
					Map<String, Bus> busMap = new HashMap<>();

					for (Bus bus : buses) {
						busMap.put(bus.getFavoriteRouteKey(), bus);
					}

					for(FavoriteRoute route : routes) {
						if(route.isBus() && busMap.containsKey(route.getFavoriteRouteKey())) {
							Bus bus = busMap.get(route.getFavoriteRouteKey());

							route.setName(bus.getName());
							route.setDestination(bus.getDestination());
							route.setTimeUntilArrival(bus.getTimeTilArrival());

							updateRouteOnView(route);
						}
					}
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) {

				}

				@Override
				public void onComplete() {

				}
			});

		disposables.add(disposable);
    }

    private void refreshTrainInformation(@NonNull final List<FavoriteRoute> routes) {
        final Disposable disposable = trainRepo.getTrains()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<Train>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<Train> trains) {
					Map<String, List<Train>> trainMap = new HashMap<>();

					for(Train train : trains) {
						String key = train.getFavoriteRouteKey();

						if(trainMap.containsKey(key)) {
							trainMap.get(key).add(train);
						}
						else {
							List<Train> matching = new ArrayList<>();
							matching.add(train);

							trainMap.put(key, matching);
						}
					}

					for(FavoriteRoute route : routes) {
						if(!route.isBus() && trainMap.containsKey(route.getFavoriteRouteKey())) {
							List<Train> list = trainMap.get(route.getFavoriteRouteKey());
							String arrivalTime = Train.combineArrivalTimes(list);

							route.setTimeUntilArrival(arrivalTime);

							updateRouteOnView(route);
						}
					}
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) {

				}

				@Override
				public void onComplete() {

				}
			});

		disposables.add(disposable);
    }

    private void updateRouteOnView(@NonNull FavoriteRoute favoriteRoute) {
        List<RouteItemModel> routeItemModels = new ArrayList<>();
        routeItemModels.add(favoriteRoute);

        view.displayRouteItems(routeItemModels);

        // only buses will change their names
        if(favoriteRoute.isBus()) {
            updateRouteOnDatabase(favoriteRoute);
        }
    }

    private void updateRouteOnDatabase(@NonNull final FavoriteRoute favoriteRoute) {
        // make sure this doesn't get marked as a new record
        if(favoriteRoute.getId() == null) {
            favoriteRoute.setId(1L);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                favsRepo.saveRoute(favoriteRoute);
            }
        });
    }

	@Override
	public void startPolling() {
		startRoutePolling();
		startNotificationPolling();
	}

	private void startRoutePolling() {
		disposables.add(pollingHelper.getBusStream()
				.zipWith(pollingHelper.getTrainStream(), new BiFunction<Integer, Integer, Integer>() {
					@Override
					public Integer apply(@io.reactivex.annotations.NonNull Integer integer,
										 @io.reactivex.annotations.NonNull Integer integer2) throws Exception
					{
						return integer + integer2;
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<Integer>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull Integer num) {
						refreshRouteInformationIfCached();
					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

					@Override
					public void onComplete() {
						refreshRouteInformationIfCached();
					}
				}));
	}

	private void startNotificationPolling() {
		disposables.add(pollingHelper.getNotificationStream()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<Integer>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull Integer num) {

					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

					@Override
					public void onComplete() { }
				}));
	}
}
