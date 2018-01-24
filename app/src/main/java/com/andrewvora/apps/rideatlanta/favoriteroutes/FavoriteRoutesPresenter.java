package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.FavoritesHelper;
import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
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
	@NonNull private FavoritesHelper favoritesHelper;

    public FavoriteRoutesPresenter(@NonNull FavoriteRoutesContract.View view,
                                   @NonNull FavoriteRoutesDataSource favRepo,
                                   @NonNull BusesDataSource busRepo,
                                   @NonNull TrainsDataSource trainRepo,
                                   @NonNull RoutePollingHelper pollingHelper,
                                   @NonNull FavoritesHelper favoritesHelper)
    {
        this.view = view;
        this.favRoutesRepo = favRepo;
        this.busRepo = busRepo;
        this.trainRepo = trainRepo;
		this.pollingHelper = pollingHelper;
		this.favoritesHelper = favoritesHelper;
		this.disposables = new CompositeDisposable();
    }

    @Override
    public void start() {
        loadFavoriteRoutes();
		startPolling();
    }

    @Override
    public void stop() {
		disposables.dispose();
		disposables.clear();
    }

    @Override
    public void loadFavoriteRoutes() {
        disposables.add(favRoutesRepo.getFavoriteRoutes()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> routes) {

					// load saved routes
					final List<FavoriteRouteDataObject> resultList = new ArrayList<>(routes);

					// display on UI
					view.onFavoriteRoutesLoaded(resultList);

					refreshRouteInformation();
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

				@Override
				public void onComplete() { }
			}));
    }

    @Override
    public void refreshRouteInformation() {
        disposables.add(Observable.zip(favRoutesRepo.getFavoriteRoutes(), busRepo.getBuses(), trainRepo.getTrains(),
	        (favoriteRoutes, buses, trains) -> {
        	    favoritesHelper.applyBusesToFavorites(buses, favoriteRoutes);
        	    favoritesHelper.applyTrainsToFavorites(trains, favoriteRoutes);
                return favoriteRoutes;
	        })
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> routes) {
					List<FavoriteRouteDataObject> favoriteRoutes = new ArrayList<>(routes);
					view.onFavoriteRoutesLoaded(favoriteRoutes);
				}

				@Override public void onError(@io.reactivex.annotations.NonNull Throwable e) { }
				@Override public void onComplete() { }
			}));
    }

	@Override
	public void startPolling() {
		disposables.add(pollingHelper.getBusStream()
			.zipWith(pollingHelper.getTrainStream(), (integer, integer2) -> integer + integer2)
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

	@Override
	public void removeRouteFromFavorites(int position, @NonNull FavoriteRouteDataObject route) {
		disposables.add(favRoutesRepo.deleteRoute(route)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<Long>() {
					@Override
					public void onNext(Long aLong) {
						view.onRouteUpdated(position, route);
					}

					@Override
					public void onError(Throwable e) { }

					@Override
					public void onComplete() { }
				}));
	}

	@Override
	public void routeClicked(int position, @NonNull FavoriteRouteDataObject route) {
		view.openRouteDetails(route);
	}
}
