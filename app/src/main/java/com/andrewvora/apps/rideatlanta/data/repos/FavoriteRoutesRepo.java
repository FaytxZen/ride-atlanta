package com.andrewvora.apps.rideatlanta.data.repos;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesRepo implements FavoriteRoutesDataSource {

    @NonNull private FavoriteRoutesDataSource remoteSource;
    @NonNull private FavoriteRoutesDataSource localSource;
    @NonNull private Map<String, FavoriteRoute> cachedRoutes;

    private boolean cacheIsDirty;

    public FavoriteRoutesRepo(@NonNull FavoriteRoutesDataSource remoteSource,
                               @NonNull FavoriteRoutesDataSource localSource)
    {
        this.remoteSource = remoteSource;
        this.localSource = localSource;

        cachedRoutes = new ConcurrentHashMap<>();
    }

    @Override
    public Observable<List<FavoriteRoute>> getFavoriteRoutes() {
        if(!cachedRoutes.isEmpty() && !cacheIsDirty) {
			final List<FavoriteRoute> routes = new ArrayList<>(cachedRoutes.values());
            return Observable.just(routes);
        }
        else {
			return localSource.getFavoriteRoutes().map(routes -> {
				reloadCachedRoutes(routes);
				return routes;
			});
        }
    }

	@Override
	public Observable<FavoriteRoute> getFavoriteRoute(@NonNull String routeId) {
		final FavoriteRoute cachedRoute = cachedRoutes.get(routeId);

		if(cachedRoute != null) {
			return Observable.just(cachedRoute);
		}
		else {
			return localSource.getFavoriteRoute(routeId)
					.map(favoriteRoute -> {
						cacheRoute(favoriteRoute);
						return null;
					});
		}
	}

    @Override
    public Observable<Long> saveRoute(@NonNull FavoriteRoute route) {
        // this app only saves favorite routes locally
        try {
            return localSource.saveRoute(route);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Observable.empty();
    }

    @Override
    public Observable<Long> deleteAllRoutes() {
        localSource.deleteAllRoutes();
        remoteSource.deleteAllRoutes();

        cachedRoutes.clear();

		return Observable.just(1L);
    }

    @Override
    public Observable<Long> deleteRoute(@NonNull FavoriteRouteDataObject route) {
        localSource.deleteRoute(route);
        cachedRoutes.remove(getMapKeyFor(route));

		return Observable.just(1L);
    }

    @Override
    public boolean hasCachedData() {
        return !cachedRoutes.isEmpty();
    }

    @Override
    public void reloadRoutes() {
        cacheIsDirty = true;
    }

    private void reloadCachedRoutes(@NonNull List<FavoriteRoute> favoriteRoutes) {
        cachedRoutes.clear();

        for(FavoriteRoute route : favoriteRoutes) {
            cacheRoute(route);
        }

        cacheIsDirty = false;
    }

    private void cacheRoute(FavoriteRoute route) {
        cachedRoutes.put(getMapKeyFor(route), route);
    }

    private String getMapKeyFor(@NonNull FavoriteRouteDataObject route) {
        return route.getRouteId() + " " + route.getDestination() + " " + route.getTravelDirection();
    }
}
