package com.andrewvora.apps.rideatlanta.buses;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.FavoritesHelper;
import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

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
public class BusRoutesPresenter implements BusRoutesContract.Presenter
{
    @NonNull private BusRoutesContract.View busView;
    @NonNull private BusesDataSource busRepo;
    @NonNull private FavoriteRoutesDataSource favRouteRepo;
	@NonNull private RoutePollingHelper pollingHelper;
    @NonNull private CompositeDisposable disposables;
    @NonNull private FavoritesHelper favoritesHelper;

    private boolean isRefreshing;

    public BusRoutesPresenter(@NonNull BusRoutesContract.View view,
                              @NonNull BusesDataSource busRepo,
                              @NonNull FavoriteRoutesDataSource routesRepo,
							  @NonNull RoutePollingHelper pollingHelper,
                              @NonNull FavoritesHelper favoritesHelper)
    {
        this.busView = view;
        this.busRepo = busRepo;
        this.favRouteRepo = routesRepo;
		this.disposables = new CompositeDisposable();
		this.pollingHelper = pollingHelper;
		this.favoritesHelper = favoritesHelper;
    }

    @Override
    public void start() {
        loadBusRoutes();
		startPolling();
    }

    @Override
    public void stop() {
        if (!disposables.isDisposed()) {
        	disposables.dispose();
        }
    }

    @Override
    public void loadBusRoutes() {
        useCachedDataIfAvailable();
		disposables.add(getBuses());
    }

    @Override
    public void refreshBusRoutes() {
    	isRefreshing = true;
        busRepo.reloadBuses();
        disposables.add(getBuses());
    }

    private Disposable getBuses() {
	    return Observable.zip(busRepo.getBuses(), favRouteRepo.getFavoriteRoutes(),
			    (buses, favorites) -> {
				    favoritesHelper.applyFavoritesToBuses(favorites, buses);
				    return buses;
			    })
			    .subscribeOn(Schedulers.io())
			    .observeOn(AndroidSchedulers.mainThread())
			    .subscribeWith(new DisposableObserver<List<Bus>>() {
				    @Override
				    public void onNext(@io.reactivex.annotations.NonNull List<Bus> buses) {
					    updateView(buses);
				    }

				    @Override
				    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
				    	busView.hideLoadingView();
					    busView.refreshError(e);

					    if (!isRefreshing) {
					    	busView.showEmptyState();
					    }
				    }

				    @Override public void onComplete() {
				    	isRefreshing = false;
				    }
			    });
    }

    @Override
    public void favoriteRoute(int position, @NonNull final Bus bus) {
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

					final FavoriteRoute favoriteRoute = new FavoriteRoute(bus);
					if(bus.isFavorited()) {
						favRouteRepo.saveRoute(favoriteRoute);
					} else {
						favRouteRepo.deleteRoute(favoriteRoute);
					}

					busView.onRouteUpdated(position, bus);
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) {
					busView.favoriteError();
				}

				@Override
				public void onComplete() { }
			});

		disposables.add(disposable);
    }

    private void updateView(List<Bus> buses) {
    	busView.hideLoadingView();
        busView.onBusRoutesLoaded(buses);

        if (buses.isEmpty()) {
        	busView.showEmptyState();
        } else {
        	busView.hideEmptyState();
        }
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
			.delay(15, TimeUnit.SECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Integer>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Integer integer) {
					if(busRepo.hasCachedData()) {
						loadBusRoutes();
					}
				}

				@Override public void onError(@io.reactivex.annotations.NonNull Throwable e) { }
				@Override public void onComplete() { }
			}));
	}
}
