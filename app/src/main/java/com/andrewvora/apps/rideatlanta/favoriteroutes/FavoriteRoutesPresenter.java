package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
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
 * Contains business logic for {@link FavoriteRoutesContract.View}
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesPresenter implements FavoriteRoutesContract.Presenter {

    @NonNull private FavoriteRoutesContract.View view;
    @NonNull private FavoriteRoutesDataSource favRoutesRepo;
    @NonNull private BusesDataSource busRepo;
    @NonNull private TrainsDataSource trainRepo;
	@NonNull private RoutePollingHelper pollingHelper;
	@NonNull private CompositeDisposable disposables;

    public FavoriteRoutesPresenter(@NonNull FavoriteRoutesContract.View view,
								   @NonNull FavoriteRoutesDataSource favRepo,
								   @NonNull BusesDataSource busRepo,
								   @NonNull TrainsDataSource trainRepo,
								   @NonNull RoutePollingHelper pollingHelper)
    {
        this.view = view;
        this.favRoutesRepo = favRepo;
        this.busRepo = busRepo;
        this.trainRepo = trainRepo;
		this.pollingHelper = pollingHelper;

		this.disposables = new CompositeDisposable();
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        loadFavoriteRoutes();
    }

    @Override
    public void stop() {
		disposables.dispose();
		disposables.clear();
    }

    @Override
    public void loadFavoriteRoutes() {
        favRoutesRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                List<FavoriteRouteDataObject> routes = new ArrayList<>();

                // load saved routes
                for(FavoriteRoute route : favRoutes) {
                    routes.add(route);
                }

                // display on UI
                view.onFavoriteRoutesLoaded(routes);

                if(trainRepo.hasCachedData() || busRepo.hasCachedData()) {
                    refreshRouteInformation();
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    @Override
    public void refreshRouteInformation() {
        favRoutesRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {
                updateRouteInformation(favRoutes);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void updateRouteInformation(@NonNull final List<FavoriteRoute> routes) {
        final Disposable busDisposable = busRepo.getBuses()
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

							updateRouteOnDatabase(route);
							view.onRouteInformationLoaded(route);
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

		disposables.add(busDisposable);

        final Disposable trainDisposable = trainRepo.getTrains()
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

							updateRouteOnDatabase(route);
							view.onRouteInformationLoaded(route);
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

		disposables.add(trainDisposable);
    }

	@Override
	public void startPolling() {
		disposables.add(pollingHelper.getBusStream()
			.zipWith(pollingHelper.getTrainStream(), new BiFunction<Integer, Integer, Integer>() {
				@Override
				public Integer apply(@io.reactivex.annotations.NonNull Integer integer, @io.reactivex.annotations.NonNull Integer integer2) throws Exception {
					return integer + integer2;
				}
			})
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Integer>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Integer integer) {
					refreshRouteInformation();
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

				@Override
				public void onComplete() { }
			}));
	}

	private void updateRouteOnDatabase(@NonNull final FavoriteRoute favoriteRoute) {
        // make sure it's not identified as a new route
        if(favoriteRoute.getId() == null) {
            favoriteRoute.setId(1L);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                favRoutesRepo.saveRoute(favoriteRoute);
            }
        });
    }
}
