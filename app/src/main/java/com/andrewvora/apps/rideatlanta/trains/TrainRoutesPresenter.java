package com.andrewvora.apps.rideatlanta.trains;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesPresenter implements
        TrainRoutesContract.Presenter,
        FavoriteRoutesContract.DataLoadedListener
{
    @NonNull private TrainRoutesContract.View view;
    @NonNull private TrainsDataSource trainRepo;
    @NonNull private FavoriteRoutesDataSource favDataSource;
    @NonNull private FavoriteRoutesContract.LoadingCache favCache;
	@NonNull private CompositeDisposable disposables;
	@NonNull private RoutePollingHelper pollingHelper;

    public TrainRoutesPresenter(@NonNull TrainRoutesContract.View view,
                                @NonNull TrainsDataSource trainRepo,
                                @NonNull FavoriteRoutesDataSource favRouteRepo,
                                @NonNull FavoriteRoutesContract.LoadingCache favRouteDataManager,
								@NonNull RoutePollingHelper pollingHelper)
    {
        this.view = view;
        this.trainRepo = trainRepo;
        this.favDataSource = favRouteRepo;
        this.favCache = favRouteDataManager;
		this.disposables = new CompositeDisposable();
		this.pollingHelper = pollingHelper;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        favCache.setListener(this);
        favCache.loadFavoriteRoutes();

        loadTrainRoutes();
		startPolling();
    }

    @Override
    public void stop() {
        favCache.setListener(null);
        favCache.loadFavoriteRoutes();
    }

    @Override
    public void onFavoriteRoutesLoaded(@NonNull List<FavoriteRouteDataObject> favRoutes) {
        view.applyFavorites(favRoutes);
    }

    @Override
    public void loadTrainRoutes() {
        useCachedDataIfAvailable(trainRepo);

        final Disposable disposable = trainRepo.getTrains()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableObserver<List<Train>>() {
                @Override
                public void onNext(@io.reactivex.annotations.NonNull List<Train> trains) {
					updateViews(trains);
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

    @Override
    public void refreshTrainRoutes() {
        trainRepo.reloadTrains();

		final Disposable disposable = trainRepo.getTrains()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<List<Train>>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull List<Train> trains) {
						updateViews(trains);
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

    @Override
    public void favoriteRoute(@NonNull final Train route) {
        route.setFavorited(!route.isFavorited());

        final Disposable disposable = trainRepo.saveTrain(route)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Long>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Long id) {
					favDataSource.reloadRoutes();
					favCache.setFavoritedRoutes(new ArrayList<FavoriteRouteDataObject>());

					FavoriteRoute favoriteRoute = new FavoriteRoute(route);

					if(route.isFavorited()) {
						favDataSource.saveRoute(favoriteRoute);
					}
					else {
						favDataSource.deleteRoute(favoriteRoute);
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

    private void updateViews(List<Train> trains) {
        view.onTrainRoutesLoaded(trains);
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

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) {

				}

				@Override
				public void onComplete() {

				}
			}));
	}
}
