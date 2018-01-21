package com.andrewvora.apps.rideatlanta.data.remote.buses;

import android.app.Application;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.RideAtlantaApplication;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.remote.marta.MartaService;
import com.andrewvora.apps.rideatlanta.utils.InputStreamConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusesRemoteSource implements BusesDataSource {

    private MartaService service;
    private Application app;
	private Gson gson;
	private InputStreamConverter converter;

    public BusesRemoteSource(@NonNull Application app,
							 @NonNull MartaService service,
							 @NonNull Gson gson,
							 @NonNull InputStreamConverter converter) {
        this.service = service;
        this.app = app;
        this.gson = gson;
        this.converter = converter;
    }

	@Override
	public Observable<List<Bus>> getBuses() {
    	if (RideAtlantaApplication.USE_LOCAL) {
    		return Observable.defer(() -> {
				final InputStream busesFileStream = app.getResources().openRawResource(R.raw.buses);
				final String busesJsonStr = converter.getString(busesFileStream);
				final List<Bus> buses = gson.fromJson(busesJsonStr, new TypeToken<List<Bus>>(){}.getType());
				Collections.sort(buses, new BusComparator());
				return Observable.just(buses);
			});
		} else return Observable.defer(() -> service.getBuses().map(buses -> {
			Collections.sort(buses, new BusComparator());
			return buses;
		}));
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
