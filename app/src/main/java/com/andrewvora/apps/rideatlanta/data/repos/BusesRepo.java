package com.andrewvora.apps.rideatlanta.data.repos;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Repo class that handles the syncing and fetching of data between the local and remote data
 * sources for {@link Bus}.
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusesRepo implements BusesDataSource {

    @NonNull private Map<String, Bus> cachedBuses;
    @NonNull private BusesDataSource remoteSource;

    // NOTE: currently not leveraging local source
    @NonNull private BusesDataSource localSource;

    private boolean cacheIsDirty;

    public BusesRepo(@NonNull BusesDataSource remoteSource,
                     @NonNull BusesDataSource localSource)
    {
        this.remoteSource = remoteSource;
        this.localSource = localSource;

        cachedBuses = new ConcurrentHashMap<>();
    }

    @Override
    public Observable<List<Bus>> getBuses() {
        if(cacheIsDirty) {
            return getBusRoutesFromRemote();
        }
        else {
            List<Bus> buses = new ArrayList<>(cachedBuses.values());
            return Observable.just(buses).map(new Function<List<Bus>, List<Bus>>() {
				@Override
				public List<Bus> apply(@io.reactivex.annotations.NonNull List<Bus> buses) throws Exception {
					Collections.sort(buses, new BusComparator());
					return buses;
				}
			});
        }
    }

	@Override
	public Observable<List<Bus>> getFreshBuses() {
		return getBusRoutesFromRemote();
	}

	@Override
	public Observable<List<Bus>> getBuses(@NonNull String... routeIds) {
		return Observable.empty();
	}

	@Override
	public Observable<Bus> getBus(@NonNull Bus bus) {
		final Bus cachedRoute = cachedBuses.get(getKeyFor(bus));

		if(cachedRoute != null) {
			return Observable.just(cachedRoute);
		}
		else {
			return remoteSource.getBus(bus).map(new Function<Bus, Bus>() {
				@Override
				public Bus apply(@io.reactivex.annotations.NonNull Bus bus) throws Exception {
					cacheBusRoute(bus);
					return bus;
				}
			});
		}
	}

	@Override
	public Observable<Long> deleteAllBus() {
		remoteSource.deleteAllBus();
		cachedBuses.clear();

		return Observable.just(0L);
	}

	@Override
	public Observable<Long> saveBus(@NonNull Bus route) {
		// only saves the routes locally since we're pulling from a read-only API
		try {
			cacheBusRoute(route);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return Observable.just(0L);
	}

    @Override
    public boolean hasCachedData() {
        return !cachedBuses.isEmpty();
    }

    @Override
    public void reloadBuses() {
        cacheIsDirty = true;
    }

    private Observable<List<Bus>> getBusRoutesFromRemote() {
        return remoteSource.getBuses().map(new Function<List<Bus>, List<Bus>>() {
			@Override
			public List<Bus> apply(@io.reactivex.annotations.NonNull List<Bus> buses) throws Exception {
				reloadCachedBusRoutes(buses);
				return buses;
			}
		});
    }

    private void reloadCachedBusRoutes(final List<Bus> routesList) {
		cachedBuses.clear();

		for(Bus route : routesList) {
			cacheBusRoute(route);
		}

		cacheIsDirty = false;
    }

    private void cacheBusRoute(@NonNull Bus bus) {
        cachedBuses.put(getKeyFor(bus), bus);
    }

    private String getKeyFor(@NonNull Bus bus) {
        return bus.getRouteId();
    }

    private static class BusComparator implements Comparator<Bus> {
        @Override
        public int compare(Bus o1, Bus o2) {
            return o1.getRouteId().compareTo(o2.getRouteId());
        }
    }
}
