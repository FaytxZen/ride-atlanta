package com.andrewvora.apps.rideatlanta.data;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created on 8/12/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class RoutePollingHelper {

	private static final long NOTIFICATION_INTERVAL = TimeUnit.MINUTES.toSeconds(5);
	private static final long BUS_INTERVAL = TimeUnit.SECONDS.toSeconds(30);
	private static final long TRAIN_INTERVAL = TimeUnit.SECONDS.toSeconds(30);

	private BusesDataSource busesDataSource;
	private TrainsDataSource trainsDataSource;
	private NotificationsDataSource notificationsDataSource;

	public RoutePollingHelper(BusesDataSource bd, TrainsDataSource td, NotificationsDataSource nd) {
		this.busesDataSource = bd;
		this.trainsDataSource = td;
		this.notificationsDataSource = nd;
	}

	public Observable<Integer> getBusStream() {
		return Observable.interval(BUS_INTERVAL, TimeUnit.SECONDS)
				.map(new Function<Long, Integer>() {
					@Override
					public Integer apply(@NonNull Long aLong) throws Exception {
						busesDataSource.getFreshBuses().blockingFirst();

						return 1;
					}
				});
	}

	public Observable<Integer> getTrainStream() {
		return Observable.interval(TRAIN_INTERVAL, TimeUnit.SECONDS)
				.map(new Function<Long, Integer>() {
					@Override
					public Integer apply(@NonNull Long aLong) throws Exception {
						trainsDataSource.getFreshTrains().blockingFirst();

						return 1;
					}
				});
	}

	public Observable<Integer> getNotificationStream() {
		return Observable.interval(NOTIFICATION_INTERVAL, TimeUnit.SECONDS)
				.map(new Function<Long, Integer>() {
					@Override
					public Integer apply(@NonNull Long aLong) throws Exception {
						notificationsDataSource.getFreshNotifications().blockingFirst();

						return 1;
					}
				});
	}
}
