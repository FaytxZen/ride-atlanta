package com.andrewvora.apps.rideatlanta.routedetails.bus;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 12/11/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class BusRouteDetailsPresenter implements BusRouteDetailsContract.Presenter {

	@NonNull private BusRouteDetailsContract.View view;
	@NonNull private BusesDataSource busesRepo;
	@NonNull private String routeId;
	@NonNull private String destination;
	@NonNull private CompositeDisposable disposables;

	public BusRouteDetailsPresenter(@NonNull BusRouteDetailsContract.View view,
									@NonNull BusesDataSource repo,
									@NonNull String routeId,
									@NonNull String destination)
	{
		this.view = view;
		this.busesRepo = repo;
		this.routeId = routeId;
		this.destination = destination;
		this.disposables = new CompositeDisposable();
	}

	@Override
	public void start() {
		loadBuses();
	}

	@Override
	public void stop() {
		if (!disposables.isDisposed()) {
			disposables.dispose();
		}
	}

	@Override
	public void loadBuses() {
		disposables.add(busesRepo.getBuses(routeId)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<List<Bus>>() {
					@Override
					public void onNext(List<Bus> buses) {
						view.showBusRoutes(buses);
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
