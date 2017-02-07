package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsLocalSource;
import com.andrewvora.apps.rideatlanta.data.remote.trains.TrainsRemoteSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repo class that handles the syncing and fetching of data between the local and remote data
 * sources for {@link Train}.
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsRepo implements TrainsDataSource {

    private static TrainsRepo mInstance;

    private TrainsDataSource mLocalSource;
    private TrainsDataSource mRemoteSource;

    private Map<String, Train> mCachedTrains;
    private boolean mCacheIsDirty;

    private TrainsRepo(@NonNull TrainsDataSource remoteSource,
                       @NonNull TrainsDataSource localSource)
    {
        mLocalSource = localSource;
        mRemoteSource = remoteSource;
    }

    public static TrainsRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            TrainsDataSource remoteSource = TrainsRemoteSource.getInstance(context);
            TrainsDataSource localSource = TrainsLocalSource.getInstance(context);
            mInstance = new TrainsRepo(remoteSource, localSource);
        }

        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    @Override
    public void getTrains(@NonNull final GetTrainRoutesCallback callback) {
        if(mCachedTrains != null && !mCacheIsDirty) {
            callback.onFinished(new ArrayList<>(mCachedTrains.values()));
        }
        else if(mCacheIsDirty) {
            getTrainsFromRemote(callback);
        }
        else {
            mLocalSource.getTrains(new GetTrainRoutesCallback() {
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
    }

    @Override
    public void getTrains(@NonNull GetTrainRoutesCallback callback, @NonNull Long... trainIds) {
        mLocalSource.getTrains(callback, trainIds);
    }

    @Override
    public void getTrain(@NonNull final Train train, @NonNull final GetTrainRouteCallback callback)
    {
        mCachedTrains = checkNotNull(mCachedTrains);
        final Train cachedTrain = mCachedTrains.get(getKeyFor(train));

        if(cachedTrain != null) {
            callback.onFinished(cachedTrain);
        }
        else {
            mLocalSource.getTrain(train, new GetTrainRouteCallback() {
                @Override
                public void onFinished(Train train) {
                    callback.onFinished(train);
                    cacheTrain(train);
                }

                @Override
                public void onError(Object error) {
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
            });
        }
    }

    @Override
    public void deleteAllTrains(@Nullable DeleteTrainRoutesCallback callback) {
        mLocalSource.deleteAllTrains(null);
        mRemoteSource.deleteAllTrains(null);

        mCachedTrains = checkNotNull(mCachedTrains);
        mCachedTrains.clear();

        if(callback != null) {
            callback.onDeleted();
        }
    }

    @Override
    public void saveTrain(@NonNull Train route) {
        // only save trains locally
        mLocalSource.saveTrain(route);
    }

    @Override
    public void reloadTrains() {
        mCacheIsDirty = true;
    }

    private void getTrainsFromRemote(@NonNull final GetTrainRoutesCallback callback) {
        mRemoteSource.getTrains(new GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                restoreFavoritedTrains(trainList);
                reloadCachedTrains(trainList);
                reloadLocalTrains(trainList);

                callback.onFinished(trainList);
            }

            @Override
            public void onError(Object error) {
                callback.onError(error);
            }
        });
    }

    private void restoreFavoritedTrains(@NonNull final List<Train> favoritedTrains) {
        mLocalSource.getTrains(new GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                if(trainList != null) {
                    for(Train train : favoritedTrains) {
                        for(Train localTrain : trainList) {
                            boolean matched = train.getStation().equals(localTrain.getStation()) &&
                                    train.getLine().equals(localTrain.getLine());

                            if(matched) {
                                train.setFavorited(localTrain.isFavorited());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void reloadCachedTrains(final List<Train> trainList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mCachedTrains = checkNotNull(mCachedTrains);
                mCachedTrains.clear();

                for(Train train : trainList) {
                    cacheTrain(train);
                }

                mCacheIsDirty = false;
            }
        });
    }

    private void reloadLocalTrains(final List<Train> trainList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mLocalSource.deleteAllTrains(null);

                for(Train train : trainList) {
                    mLocalSource.saveTrain(train);
                }
            }
        });
    }

    private void cacheTrain(Train train) {
        mCachedTrains = checkNotNull(mCachedTrains);
        mCachedTrains.put(getKeyFor(train), train);
    }

    private Map<String, Train> checkNotNull(Map<String, Train> trainMap) {
        if(trainMap == null) {
            trainMap = new LinkedHashMap<>();
        }

        return trainMap;
    }

    private String getKeyFor(@NonNull Train train) {
        return train.getLine() + train.getStation() + train.getDirection();
    }
}
