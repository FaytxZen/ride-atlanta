package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.buses.BusesLocalSource;
import com.andrewvora.apps.rideatlanta.data.remote.buses.BusesRemoteSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repo class that handles the syncing and fetching of data between the local and remote data
 * sources for {@link Bus}.
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusesRepo implements BusesDataSource {

    private static BusesRepo mInstance;

    private Map<String, Bus> mCachedBuses;
    private BusesDataSource mRemoteSource;
    private BusesDataSource mLocalSource;

    private boolean mCacheIsDirty;

    private BusesRepo(@NonNull BusesDataSource remoteSource,
                     @NonNull BusesDataSource localSource)
    {
        mRemoteSource = remoteSource;
        mLocalSource = localSource;
    }

    public static BusesRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            BusesDataSource remoteSource = BusesRemoteSource.getInstance(context);
            BusesDataSource localSource = BusesLocalSource.getInstance(context);

            mInstance = new BusesRepo(remoteSource, localSource);
        }

        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    @Override
    public void getBuses(@NonNull final GetBusesCallback callback) {
        if(mCachedBuses != null && !mCacheIsDirty) {
            callback.onFinished(new ArrayList<>(mCachedBuses.values()));
        }
        else if(mCacheIsDirty) {
            getBusRoutesFromRemote(callback);
        }
        else {
            mLocalSource.getBuses(new GetBusesCallback() {
                @Override
                public void onFinished(List<Bus> buses) {
                    reloadCachedBusRoutes(buses);
                    callback.onFinished(buses);
                }

                @Override
                public void onError(Object error) {
                    callback.onError(error);
                }
            });
        }
    }

    @Override
    public void getBuses(@NonNull GetBusesCallback callback, @NonNull String... routeIds) {
        mLocalSource.getBuses(callback, routeIds);
    }

    @Override
    public void getBus(@NonNull final Bus bus, @NonNull final GetBusCallback callback) {
        mCachedBuses = checkNotNull(mCachedBuses);

        final Bus cachedRoute = mCachedBuses.get(getKeyFor(bus));

        if(cachedRoute != null) {
            callback.onFinished(cachedRoute);
        }
        else {
            mLocalSource.getBus(bus, new GetBusCallback() {
                @Override
                public void onFinished(Bus bus) {
                    callback.onFinished(bus);
                    cacheBusRoute(bus);
                }

                @Override
                public void onError(Object error) {
                    mRemoteSource.getBus(bus, new GetBusCallback() {
                        @Override
                        public void onFinished(Bus bus) {
                            callback.onFinished(bus);
                            cacheBusRoute(bus);
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
    public void deleteAllBus(@Nullable DeleteBusesCallback callback) {
        mLocalSource.deleteAllBus(null);
        mRemoteSource.deleteAllBus(null);

        mCachedBuses = checkNotNull(mCachedBuses);
        mCachedBuses.clear();

        if(callback != null) {
            callback.onDeleted();
        }
    }

    @Override
    public void saveBus(@NonNull Bus route) {
        // only saves the routes locally since we're pulling from a read-only API
        try {
            mLocalSource.saveBus(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadBuses() {
        mCacheIsDirty = true;
    }

    private void getBusRoutesFromRemote(@NonNull final GetBusesCallback callback) {
        mRemoteSource.getBuses(new GetBusesCallback() {
            @Override
            public void onFinished(List<Bus> buses) {

                restoreFavoritedBuses(buses);
                reloadCachedBusRoutes(buses);
                reloadLocalBusRoutes(buses);

                callback.onFinished(buses);
            }

            @Override
            public void onError(Object error) {
                callback.onError(error);
            }
        });
    }

    private void restoreFavoritedBuses(@NonNull final List<Bus> buses) {
        mLocalSource.getBuses(new GetBusesCallback() {
            @Override
            public void onFinished(List<Bus> savedBuses) {
                if(savedBuses != null) {
                    for(Bus bus : buses) {
                        for(Bus savedBus : savedBuses) {
                            if(bus.getRouteId().equals(savedBus.getRouteId())) {
                                bus.setFavorited(savedBus.isFavorited());
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

    private void reloadCachedBusRoutes(final List<Bus> routesList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mCachedBuses = checkNotNull(mCachedBuses);
                mCachedBuses.clear();

                for(Bus route : routesList) {
                    cacheBusRoute(route);
                }

                mCacheIsDirty = false;
            }
        });
    }

    private void reloadLocalBusRoutes(@NonNull final List<Bus> routesList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mLocalSource.deleteAllBus(null);

                for(Bus route : routesList) {
                    mLocalSource.saveBus(route);
                }
            }
        });
    }

    private void cacheBusRoute(@NonNull Bus bus) {
        mCachedBuses = checkNotNull(mCachedBuses);
        mCachedBuses.put(getKeyFor(bus), bus);
    }

    private Map<String, Bus> checkNotNull(@Nullable Map<String, Bus> map) {
        return map == null ? new LinkedHashMap<String, Bus>() : map;
    }

    private String getKeyFor(@NonNull Bus bus) {
        return bus.getRouteId();
    }
}
