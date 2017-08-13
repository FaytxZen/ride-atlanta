package com.andrewvora.apps.rideatlanta.data.remote.buses;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.remote.marta.MartaService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusesRemoteSource implements BusesDataSource {

    private MartaService service;

    public BusesRemoteSource(@NonNull MartaService service) {
        this.service = service;
    }

	@Override
	public Observable<List<Bus>> getBuses() {
		return service.getBuses().map(new Function<List<Bus>, List<Bus>>() {
					@Override
					public List<Bus> apply(@io.reactivex.annotations.NonNull List<Bus> buses) throws Exception {
						Collections.sort(buses, new BusComparator());
						return buses;
					}
				});
	}

	@Override
	public Observable<List<Bus>> getFreshBuses() {
		return getBuses();
	}

	@Override
	public Observable<List<Bus>> getBuses(@NonNull String... routeIds) {
		return Observable.empty();
	}

	@Override
	public Observable<Bus> getBus(@NonNull Bus bus) {
		return Observable.empty();
	}

	@Override
	public Observable<Long> deleteAllBus() {
		return Observable.empty();
	}

    @Override
    public Observable<Long> saveBus(@NonNull Bus route) {
		return Observable.empty();
    }

    @Override
    public void reloadBuses() {

    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    private static class BusComparator implements Comparator<Bus> {
		@Override
		public int compare(Bus b1, Bus b2) {
			return b1.getRouteId().compareTo(b2.getRouteId());
		}
	}
}
