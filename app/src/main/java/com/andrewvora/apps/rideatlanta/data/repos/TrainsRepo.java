package com.andrewvora.apps.rideatlanta.data.repos;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;

/**
 * Repo class that handles the syncing and fetching of data between the local and remote data
 * sources for {@link Train}.
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainsRepo implements TrainsDataSource {

    private static final String KEY_DELIMITER = "\\$";

    // Note: currently not leveraging the local source
    private TrainsDataSource localSource;
    private TrainsDataSource remoteSource;

    @NonNull
    private Map<String, Train> cachedTrains;
    private boolean cacheIsDirty;

    public TrainsRepo(@NonNull TrainsDataSource remoteSource,
                      @NonNull TrainsDataSource localSource)
    {
        this.localSource = localSource;
        this.remoteSource = remoteSource;

        cachedTrains = new ConcurrentHashMap<>();
    }

    private static String getKeyFor(@NonNull Train train) {
        return  train.getTrainId().toString() + KEY_DELIMITER +
                train.getLine() + KEY_DELIMITER +
				train.getDirection() + KEY_DELIMITER +
                train.getStation();
    }

    @Override
    public boolean hasCachedData() {
        return !cachedTrains.isEmpty();
    }

    @Override
    public Observable<List<Train>> getTrains() {
        if(!cachedTrains.isEmpty() && !cacheIsDirty) {
            List<Train> cachedTrainList = new ArrayList<>(cachedTrains.values());

            return Observable.just(cachedTrainList).map(trains -> {
				Collections.sort(trains, new TrainsComparator());
				return trains;
			});
        }
        else {
            return getTrainsFromRemote();
        }
    }

	@Override
	public Observable<List<Train>> getFreshTrains() {
		return getTrainsFromRemote();
	}

	@Override
    public Observable<List<Train>> getTrains(@NonNull Long... trainIds) {
        return remoteSource.getTrains(trainIds);
    }

    @Override
    public Observable<List<Train>> getTrains(@NonNull String station, @NonNull String line) {
        if(cachedTrains.isEmpty()) {
            return remoteSource.getTrains(station, line);
        }
        else {
            List<Train> matchingTrains = new ArrayList<>();

            for(String key : cachedTrains.keySet()) {
                final Train train = cachedTrains.get(key);
                final boolean matched = train.getStation().equals(station) &&
                        train.getLine().equals(line);

                if(matched) {
                    matchingTrains.add(train);
                }
            }

            return Observable.just(matchingTrains);
        }
    }

	@Override
	public Observable<List<Train>> getTrains(@NonNull final String station) {
		if (cachedTrains.isEmpty()) {
			return remoteSource.getTrains(station);
		} else {
			return Observable.defer(() -> Observable.fromIterable(cachedTrains.values())
					.filter(train -> station.equals(train.getStation()))
					.toSortedList(new TrainsComparator())
					.toObservable());
		}
	}

	@Override
	public Observable<Train> getTrain(@NonNull Train train) {
		final Train cachedTrain = cachedTrains.get(getKeyFor(train));

		if(cachedTrain != null) {
			return Observable.just(cachedTrain);
		}
		else {
			return remoteSource.getTrain(train).map(train1 -> {
				cacheTrain(train1);
				return train1;
			});
		}
	}

	@Override
	public Observable<Long> deleteAllTrains() {
		remoteSource.deleteAllTrains();
		cachedTrains.clear();

		return Observable.just(0L);
	}

	@Override
	public Observable<Long> saveTrain(@NonNull Train route) {
		cacheTrain(route);
		return Observable.just(0L);
	}

    @Override
    public void reloadTrains() {
        cacheIsDirty = true;
    }

    private Observable<List<Train>> getTrainsFromRemote() {
        return remoteSource.getTrains().map(trains -> {
			reloadCachedTrains(trains);
			return trains;
		});
    }

    private void reloadCachedTrains(final List<Train> trainList) {
		cachedTrains.clear();

		for(Train train : trainList) {
			cacheTrain(train);
		}

		cacheIsDirty = false;
    }

    private void cacheTrain(Train train) {
        cachedTrains.put(getKeyFor(train), train);
    }

    private static class TrainsComparator implements Comparator<Train> {
        @Override
        public int compare(Train o1, Train o2) {
            if (o1.getLine().compareTo(o2.getLine()) != 0) {
                return o1.getLine().compareTo(o2.getLine());
            }
            else if (o1.getStation().compareTo(o2.getStation()) != 0) {
                return o1.getStation().compareTo(o2.getStation());
            }
            else if (o1.getDirection().compareTo(o2.getDirection()) != 0) {
            	return o1.getDirection().compareTo(o2.getDirection());
			}
            else {
                return o1.getRouteId().compareTo(o2.getRouteId());
            }
        }
    }
}
