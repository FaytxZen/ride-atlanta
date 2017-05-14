package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.buses.BusesLocalSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.remote.buses.BusesRemoteSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repo class that handles the syncing and fetching of data between the local and remote data
 * sources for {@link Bus}.
 *
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusesRepo implements BusesDataSource {

    private static BusesRepo mInstance;

    @NonNull private Map<String, Bus> mCachedBuses;
    @NonNull private BusesDataSource mRemoteSource;

    // NOTE: currently not leveraging local source
    @NonNull private BusesDataSource mLocalSource;

    private boolean mCacheIsDirty;

    private BusesRepo(@NonNull BusesDataSource remoteSource,
                     @NonNull BusesDataSource localSource)
    {
        mRemoteSource = remoteSource;
        mLocalSource = localSource;

        mCachedBuses = new ConcurrentHashMap<>();
    }

    public static BusesRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            BusesDataSource remoteSource = BusesRemoteSource.getInstance(context);
            BusesDataSource localSource = BusesLocalSource.getInstance(context);

            mInstance = new BusesRepo(remoteSource, localSource);
        }

        return mInstance;
    }

    @Override
    public void getBuses(@NonNull final GetBusesCallback callback) {
        if(mCacheIsDirty) {
            getBusRoutesFromRemote(callback);
        }
        else {
            List<Bus> buses = new ArrayList<>(mCachedBuses.values());
            Collections.sort(buses, new BusComparator());

            callback.onFinished(buses);
        }
    }

    @Override
    public void getBuses(@NonNull GetBusesCallback callback, @NonNull String... routeIds) {

    }

    @Override
    public void getBus(@NonNull final Bus bus, @NonNull final GetBusCallback callback) {
        final Bus cachedRoute = mCachedBuses.get(getKeyFor(bus));

        if(cachedRoute != null) {
            callback.onFinished(cachedRoute);
        }
        else {
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
    }

    @Override
    public void deleteAllBus(@Nullable DeleteBusesCallback callback) {
        mRemoteSource.deleteAllBus(null);

        mCachedBuses.clear();

        if(callback != null) {
            callback.onDeleted();
        }
    }

    @Override
    public void saveBus(@NonNull Bus route) {
        // only saves the routes locally since we're pulling from a read-only API
        try {
            cacheBusRoute(route);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasCachedData() {
        return !mCachedBuses.isEmpty();
    }

    @Override
    public void reloadBuses() {
        mCacheIsDirty = true;
    }

    private void getBusRoutesFromRemote(@NonNull final GetBusesCallback callback) {
        mRemoteSource.getBuses(new GetBusesCallback() {
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

    private void reloadCachedBusRoutes(final List<Bus> routesList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mCachedBuses.clear();

                for(Bus route : routesList) {
                    cacheBusRoute(route);
                }

                mCacheIsDirty = false;
            }
        });
    }

    private void cacheBusRoute(@NonNull Bus bus) {
        mCachedBuses.put(getKeyFor(bus), bus);
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
