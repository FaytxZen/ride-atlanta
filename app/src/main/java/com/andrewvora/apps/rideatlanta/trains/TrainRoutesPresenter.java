package com.andrewvora.apps.rideatlanta.trains;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.FavoritesHelper;
import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;

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
public class TrainRoutesPresenter implements TrainRoutesContract.Presenter
{
    @NonNull private TrainRoutesContract.View view;
    @NonNull private TrainsDataSource trainRepo;
    @NonNull private FavoriteRoutesDataSource favDataSource;
	@NonNull private CompositeDisposable disposables;
	@NonNull private FavoritesHelper favoritesHelper;
	@NonNull private RoutePollingHelper pollingHelper;

	private boolean isRefreshing;

    public TrainRoutesPresenter(@NonNull TrainRoutesContract.View view,
                                @NonNull TrainsDataSource trainRepo,
                                @NonNull FavoriteRoutesDataSource favRouteRepo,
								@NonNull RoutePollingHelper pollingHelper,
                                @NonNull FavoritesHelper favoritesHelper)
    {
        this.view = view;
        this.trainRepo = trainRepo;
        this.favDataSource = favRouteRepo;
		this.disposables = new CompositeDisposable();
		this.pollingHelper = pollingHelper;
		this.favoritesHelper = favoritesHelper;
    }

    @Override
    public void start() {
        loadTrainRoutes();
		startPolling();
    }

    @Override
    public void stop() {
        if (!disposables.isDisposed()) {
        	disposables.dispose();
        }
    }

    @Override
    public void loadTrainRoutes() {
        useCachedDataIfAvailable(trainRepo);
		disposables.add(getTrains());
    }

    @Override
    public void refreshTrainRoutes() {
    	isRefreshing = true;
        trainRepo.reloadTrains();
		disposables.add(getTrains());
    }

    private Disposable getTrains() {
    	return Observable.zip(trainRepo.getTrains(), favDataSource.getFavoriteRoutes(),
			    (trains, favorites) -> {
    		        favoritesHelper.applyFavoritesToTrains(favorites, trains);
    		        return trains;
			    })
			    .subscribeOn(Schedulers.io())
			    .observeOn(AndroidSchedulers.mainThread())
			    .subscribeWith(new DisposableObserver<List<Train>>() {
				    @Override
				    public void onNext(@io.reactivex.annotations.NonNull List<Train> trains) {
				    	view.hideLoadingView();
					    view.onTrainRoutesLoaded(trains);

					    if (trains.isEmpty()) {
					    	view.showEmptyState();
					    } else {
					    	view.hideEmptyState();
					    }
				    }

				    @Override public void onError(@io.reactivex.annotations.NonNull Throwable e) {
				    	view.hideLoadingView();
				    	view.showLoadingError();

				    	if (!isRefreshing) {
				    		view.showEmptyState();
					    }
				    }
				    @Override public void onComplete() {
				    	isRefreshing = false;
				    }
			    });
    }

    @Override
    public void favoriteRoute(int position, @NonNull final Train route) {
        route.setFavorited(!route.isFavorited());

        final Disposable disposable = trainRepo.saveTrain(route)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Long>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Long id) {
					favDataSource.reloadRoutes();
					final FavoriteRoute favoriteRoute = new FavoriteRoute(route);
					if(route.isFavorited()) {
						favDataSource.saveRoute(favoriteRoute);
					} else {
						favDataSource.deleteRoute(favoriteRoute);
					}

					view.onRouteUpdated(position, route);
				}

				@Override public void onError(@io.reactivex.annotations.NonNull Throwable e) {
					view.showFavoriteError();
				}
				@Override public void onComplete() { }
			});

		disposables.add(disposable);
    }

    private boolean hasNoCachedData() {
        return !trainRepo.hasCachedData();
    }

    private void useCachedDataIfAvailable(TrainsDataSource repo) {
        if(hasNoCachedData()) {
            repo.reloadTrains();
        }
    }

	@Override
	public void startPolling() {
		disposables.add(pollingHelper.getTrainStream()
			.delay(15, TimeUnit.SECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Integer>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Integer num) {
					if(trainRepo.hasCachedData()) {
						loadTrainRoutes();
					}
				}

				@Override public void onError(@io.reactivex.annotations.NonNull Throwable e) { }
				@Override public void onComplete() { }
			}));
	}
}
