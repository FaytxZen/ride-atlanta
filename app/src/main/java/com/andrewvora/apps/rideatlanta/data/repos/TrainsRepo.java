package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsLocalSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.remote.trains.TrainsRemoteSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repo class that handles the syncing and fetching of data between the local and remote data
 * sources for {@link Train}.
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainsRepo implements TrainsDataSource {

    public static final String KEY_DELIMITER = "\\$";

    private static TrainsRepo mInstance;

    // Note: currently not leveraging the local source
    private TrainsDataSource mLocalSource;
    private TrainsDataSource mRemoteSource;

    @NonNull
    private Map<String, Train> mCachedTrains;
    private boolean mCacheIsDirty;

    private TrainsRepo(@NonNull TrainsDataSource remoteSource,
                       @NonNull TrainsDataSource localSource)
    {
        mLocalSource = localSource;
        mRemoteSource = remoteSource;

        mCachedTrains = new ConcurrentHashMap<>();
    }

    public static TrainsRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            TrainsDataSource remoteSource = TrainsRemoteSource.getInstance(context);
            TrainsDataSource localSource = TrainsLocalSource.getInstance(context);
            mInstance = new TrainsRepo(remoteSource, localSource);
        }

        return mInstance;
    }

    public static String getKeyFor(@NonNull Train train) {
        return  train.getTrainId().toString() + KEY_DELIMITER +
                train.getLine() + KEY_DELIMITER +
                train.getStation();
    }

    @Override
    public boolean hasCachedData() {
        return !mCachedTrains.isEmpty();
    }

    @Override
    public void getTrains(@NonNull final GetTrainRoutesCallback callback) {
        if(!mCachedTrains.isEmpty() && !mCacheIsDirty) {
            List<Train> cachedTrainList = new ArrayList<>(mCachedTrains.values());
            Collections.sort(cachedTrainList, new TrainsComparator());

            callback.onFinished(cachedTrainList);
        }
        else if(mCacheIsDirty) {
            getTrainsFromRemote(callback);
        }
    }

    @Override
    public void getTrains(@NonNull String station, @NonNull String line, @NonNull GetTrainRoutesCallback callback) {

        if(mCachedTrains.isEmpty()) {
            callback.onFinished(new ArrayList<Train>());
        }
        else {
            List<Train> matchingTrains = new ArrayList<>();

            for(String key : mCachedTrains.keySet()) {
                final Train train = mCachedTrains.get(key);
                final boolean matched = train.getStation().equals(station) &&
                        train.getLine().equals(line);

                if(matched) {
                    matchingTrains.add(train);
                }
            }

            callback.onFinished(matchingTrains);
        }
    }

    @Override
    public void getTrains(@NonNull GetTrainRoutesCallback callback, @NonNull Long... trainIds) {
        mRemoteSource.getTrains(callback, trainIds);
    }

    @Override
    public void getTrain(@NonNull final Train train, @NonNull final GetTrainRouteCallback callback)
    {
        final Train cachedTrain = mCachedTrains.get(getKeyFor(train));

        if(cachedTrain != null) {
            callback.onFinished(cachedTrain);
        }
        else {
            mRemoteSource.getTrain(train, new GetTrainRouteCallback() {
                @Override
                public void onFinished(Train train) {
                    callback.onFinished(train);
                    cacheTrain(train);
                }

                @Override
                public void onError(Object error) {
                    callback.onError(error);
                }
            });
        }
    }

    @Override
    public void deleteAllTrains(@Nullable DeleteTrainRoutesCallback callback) {
        mRemoteSource.deleteAllTrains(null);

        mCachedTrains.clear();

        if(callback != null) {
            callback.onDeleted();
        }
    }

    @Override
    public void saveTrain(@NonNull Train route) {
        cacheTrain(route);
    }

    @Override
    public void reloadTrains() {
        mCacheIsDirty = true;
    }

    private void getTrainsFromRemote(@NonNull final GetTrainRoutesCallback callback) {
        mRemoteSource.getTrains(new GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                reloadCachedTrains(trainList);

                callback.onFinished(trainList);
            }

            @Override
            public void onError(Object error) {
                callback.onError(error);
            }
        });
    }

    private void reloadCachedTrains(final List<Train> trainList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mCachedTrains.clear();

                for(Train train : trainList) {
                    cacheTrain(train);
                }

                mCacheIsDirty = false;
            }
        });
    }

    private void cacheTrain(Train train) {
        mCachedTrains.put(getKeyFor(train), train);
    }

    private static class TrainsComparator implements Comparator<Train> {
        @Override
        public int compare(Train o1, Train o2) {
            if(o1.getLine().compareTo(o2.getLine()) != 0) {
                return o1.getLine().compareTo(o2.getLine());
            }
            else if(o1.getStation().compareTo(o2.getStation()) != 0) {
                return o1.getStation().compareTo(o2.getStation());
            }
            else {
                return o1.getRouteId().compareTo(o2.getRouteId());
            }
        }
    }
}
