package com.andrewvora.apps.rideatlanta.data.local.buses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.common.models.Bus;
import com.andrewvora.apps.rideatlanta.data.BusesDataSource;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusesLocalSource implements BusesDataSource {

    @Override
    public void getBuses(@NonNull GetBusesCallback callback) {

    }

    @Override
    public void getBus(@NonNull String routeId, @NonNull GetBusCallback callback) {

    }

    @Override
    public void deleteAllBus(@Nullable DeleteBusesCallback callback) {

    }

    @Override
    public void saveBus(@NonNull Bus route) {

    }

    @Override
    public void reloadBuses() {

    }
}
