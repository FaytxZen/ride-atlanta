package com.andrewvora.apps.rideatlanta.data;

import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Helps common operations involving {@link FavoriteRoute} and other data models.
 * Created on 1/22/2018.
 * @author Andrew Vorakrajangthiti
 */
public class FavoritesHelper {

	@Inject
	FavoritesHelper() {

	}

	public void applyFavoritesToBuses(List<FavoriteRoute> favoriteRoutes, List<Bus> buses) {
		final Set<String> favoritedIds = new HashSet<>();
		for (FavoriteRoute favorite : favoriteRoutes) {
			favoritedIds.add(favorite.getRouteId() + favorite.getDestination() + favorite.getTravelDirection());
		}

		for (Bus bus : buses) {
			final String key = bus.getRouteId() + bus.getDestination() + bus.getTravelDirection();
			bus.setFavorited(favoritedIds.contains(key));
		}
	}

	public void applyFavoritesToTrains(List<FavoriteRoute> favoriteRoutes, List<Train> trains) {
		final Set<String> favoritedIds = new HashSet<>();
		for (FavoriteRoute favorite : favoriteRoutes) {
			favoritedIds.add(favorite.getRouteId() + favorite.getDestination() + favorite.getTravelDirection());
		}

		for (Train train : trains) {
			final String key = train.getRouteId() + train.getDestination() + train.getTravelDirection();
			train.setFavorited(favoritedIds.contains(key));
		}
	}

	public void applyTrainsToFavorites(List<Train> trains, List<FavoriteRoute> favoriteRoutes) {
		final Map<String, List<Train>> trainMap = new HashMap<>();
		for (Train train : trains) {
			final String key = train.getName() + train.getDestination() + train.getTravelDirection();
			if (trainMap.containsKey(key)) {
				trainMap.get(key).add(train);
			} else {
				List<Train> list = new ArrayList<>();
				list.add(train);
				trainMap.put(key, list);
			}
		}

		for (FavoriteRoute route: favoriteRoutes) {
			final String key = route.getName() + route.getDestination() + route.getTravelDirection();
			if (trainMap.containsKey(key)) {
				final String arrivalTimes = Train.combineArrivalTimes(trainMap.get(key));
				route.setTimeUntilArrival(arrivalTimes);
			}
		}
	}

	public void applyBusesToFavorites(List<Bus> buses, List<FavoriteRoute> favoriteRoutes) {
		final Map<String, Bus> busMap = new HashMap<>();
		for (Bus bus : buses) {
			final String key = bus.getName() + bus.getDestination() + bus.getTravelDirection();
			busMap.put(key, bus);
		}

		for (FavoriteRoute route: favoriteRoutes) {
			final String key = route.getName() + route.getDestination() + route.getTravelDirection();
			if (busMap.containsKey(key)) {
				route.setName(busMap.get(key).getName());
				route.setDestination(busMap.get(key).getDestination());
				route.setTimeUntilArrival(busMap.get(key).getTimeTilArrival());
			}
		}
	}
}
