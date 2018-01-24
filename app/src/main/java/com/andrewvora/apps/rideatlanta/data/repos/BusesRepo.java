package com.andrewvora.apps.rideatlanta.data.repos;

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
import io.reactivex.ObservableSource;
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

	private boolean cacheIsDirty;

    public BusesRepo(@NonNull BusesDataSource remoteSource) {
        this.remoteSource = remoteSource;

	    cachedBuses = new ConcurrentHashMap<>();
    }

    @Override
    public Observable<List<Bus>> getBuses() {
        if(cachedBuses.isEmpty() || cacheIsDirty) {
            return getBusRoutesFromRemote().map(buses -> {
            	Collections.sort(buses, new BusComparator());
            	return buses;
            });
        }
        else {
            final List<Bus> busList = new ArrayList<>(cachedBuses.values());
            return Observable.just(busList).map(buses -> {
            	Collections.sort(buses, new BusComparator());
            	return buses;
			});
        }
    }

	@Override
	public Observable<List<Bus>> getFreshBuses() {
		return getBusRoutesFromRemote();
	}

	@Override
	public Observable<List<Bus>> getBuses(@NonNull String... routeIds) {
		return getBuses()
				.flatMap((Function<List<Bus>, ObservableSource<Bus>>) Observable::fromIterable)
				.filter(bus -> {
					for (String id : routeIds) {
						if (id.equals(bus.getRouteId())) {
							return true;
						}
					}

					return false;
				})
				.toSortedList(new BusComparator())
				.toObservable();
	}

	@Override
	public Observable<Bus> getBus(@NonNull Bus bus) {
		final Bus cachedRoute = cachedBuses.get(getKeyFor(bus));

		if(cachedRoute != null) {
			return Observable.just(cachedRoute);
		}
		else {
			return remoteSource.getBus(bus).map(bus1 -> {
				cacheBusRoute(bus1);
				return bus1;
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
        return remoteSource.getBuses().map(buses -> {
			reloadCachedBusRoutes(buses);
			return buses;
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
        return bus.getRouteId() + " " + bus.getDestination();
    }

    private static class BusComparator implements Comparator<Bus> {
        @Override
        public int compare(Bus o1, Bus o2) {
        	try {
        		final Long o1Id = Long.parseLong(o1.getRouteId());
        		final Long o2Id = Long.parseLong(o2.getRouteId());
        		return o1Id.compareTo(o2Id);
			} catch (NumberFormatException nfe) {
        		return o1.getName().compareTo(o2.getName());
			}
        }
    }
}
