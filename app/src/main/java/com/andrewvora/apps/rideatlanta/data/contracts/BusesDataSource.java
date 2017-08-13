package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface BusesDataSource {

    Observable<List<Bus>> getFreshBuses();
    Observable<List<Bus>> getBuses();
    Observable<List<Bus>> getBuses(@NonNull String... routeIds);
    Observable<Bus> getBus(@NonNull Bus bus);
    Observable<Long> deleteAllBus();
    Observable<Long> saveBus(@NonNull Bus route);
    void reloadBuses();

    boolean hasCachedData();
}
