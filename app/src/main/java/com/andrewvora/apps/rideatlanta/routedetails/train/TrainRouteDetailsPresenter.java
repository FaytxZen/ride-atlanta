package com.andrewvora.apps.rideatlanta.routedetails.train;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 12/10/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class TrainRouteDetailsPresenter implements TrainRouteDetailsContract.Presenter {

	@NonNull private final TrainRouteDetailsContract.View view;
	@NonNull private final TrainsDataSource trainsRepo;
	@NonNull private final CompositeDisposable disposables;
	@NonNull private final String station;

	public TrainRouteDetailsPresenter(@NonNull TrainRouteDetailsContract.View view,
									  @NonNull TrainsDataSource repo,
									  @NonNull String station)
	{
		this.view = view;
		this.trainsRepo = repo;
		this.disposables = new CompositeDisposable();
		this.station = station;
	}

	@Override
	public void start() {
		loadTrains();
	}

	@Override
	public void stop() {
		if (!disposables.isDisposed()) {
			disposables.dispose();
		}
	}

	@Override
	public void loadTrains() {
		disposables.add(trainsRepo.getTrains(station)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<List<Train>>() {
				@Override
				public void onNext(List<Train> trains) {
					view.showTrains(trains);
				}

				@Override
				public void onError(Throwable e) {

				}

				@Override
				public void onComplete() {

				}
			}));
	}
}
