package com.andrewvora.apps.rideatlanta.buses;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesContract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesPresenter implements
        BusRoutesContract.Presenter,
        FavoriteRoutesContract.DataLoadedListener
{
    @NonNull private BusRoutesContract.View busView;
    @NonNull private BusesDataSource busRepo;
    @NonNull private FavoriteRoutesDataSource favRouteRepo;
    @NonNull private FavoriteRoutesContract.LoadingCache favRouteCache;
	@NonNull private RoutePollingHelper pollingHelper;
    @NonNull private CompositeDisposable disposables;

    public BusRoutesPresenter(@NonNull BusRoutesContract.View view,
                              @NonNull BusesDataSource busRepo,
                              @NonNull FavoriteRoutesDataSource routesRepo,
                              @NonNull FavoriteRoutesContract.LoadingCache routesDataManager,
							  @NonNull RoutePollingHelper pollingHelper)
    {
        this.busView = view;
        this.busRepo = busRepo;
        this.favRouteRepo = routesRepo;
        this.favRouteCache = routesDataManager;
		this.disposables = new CompositeDisposable();
		this.pollingHelper = pollingHelper;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        favRouteCache.setListener(this);
        favRouteCache.loadFavoriteRoutes();

        loadBusRoutes();
    }

    @Override
    public void stop() {
        favRouteCache.setListener(null);

        disposables.dispose();
    }

    @Override
    public void onFavoriteRoutesLoaded(@NonNull List<FavoriteRouteDataObject> favRoutes) {
        busView.applyFavorites(favRoutes);
    }

    @Override
    public void loadBusRoutes() {
        useCachedDataIfAvailable();

        final Disposable disposable = busRepo.getBuses()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<List<Bus>>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull List<Bus> buses) {
						updateView(buses);
					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

					@Override
					public void onComplete() { }
				});

		disposables.add(disposable);
    }

    @Override
    public void refreshBusRoutes() {
        busRepo.reloadBuses();

        final Disposable disposable = busRepo.getBuses()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableObserver<List<Bus>>() {
                @Override
                public void onNext(@io.reactivex.annotations.NonNull List<Bus> buses) {
                    updateView(buses);
                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {}

                @Override
                public void onComplete() {}
            });

        disposables.add(disposable);
    }

    @Override
    public void favoriteRoute(@NonNull final Bus bus) {
        // toggle favorited value
        bus.setFavorited(!bus.isFavorited());

        final Disposable disposable = busRepo.saveBus(bus)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Long>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Long id) {
					// set repo to get fresh data
					favRouteRepo.reloadRoutes();
					favRouteCache.setFavoritedRoutes(new ArrayList<FavoriteRouteDataObject>());

					FavoriteRoute favoriteRoute = new FavoriteRoute(bus);

					if(bus.isFavorited()) {
						favRouteRepo.saveRoute(favoriteRoute);
					}
					else {
						favRouteRepo.deleteRoute(favoriteRoute);
					}
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

				@Override
				public void onComplete() { }
			});

		disposables.add(disposable);
    }

    private void updateView(List<Bus> buses) {
        busView.onBusRoutesLoaded(buses);
    }

    private boolean hasNoCachedData() {
        return !busRepo.hasCachedData();
    }

    private void useCachedDataIfAvailable() {
        if(hasNoCachedData()) {
            busRepo.reloadBuses();
        }
    }

	@Override
	public void startPolling() {
		disposables.add(pollingHelper.getBusStream()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Integer>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Integer integer) {
					if(busRepo.hasCachedData()) {
						loadBusRoutes();
					}
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) {

				}

				@Override
				public void onComplete() {

				}
			}));
	}
}
