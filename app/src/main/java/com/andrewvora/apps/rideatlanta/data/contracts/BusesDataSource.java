package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface BusesDataSource {

    interface GetBusesCallback {
        void onFinished(List<Bus> buses);
        void onError(Object error);
    }

    interface GetBusCallback {
        void onFinished(Bus bus);
        void onError(Object error);
    }

    interface DeleteBusesCallback {
        void onDeleted();
        void onError(Object error);
    }

    void getBuses(@NonNull GetBusesCallback callback);
    void getBuses(@NonNull GetBusesCallback callback, @NonNull String... routeIds);
    void getBus(@NonNull String routeId, @NonNull GetBusCallback callback);
    void deleteAllBus(@Nullable DeleteBusesCallback callback);
    void saveBus(@NonNull Bus route);
    void reloadBuses();
}
