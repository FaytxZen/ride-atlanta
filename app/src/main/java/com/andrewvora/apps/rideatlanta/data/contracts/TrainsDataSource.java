package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface TrainsDataSource {

    Observable<List<Train>> getFreshTrains();
    Observable<List<Train>> getTrains();
    Observable<List<Train>> getTrains(@NonNull Long... trainIds);
    Observable<Train> getTrain(@NonNull Train train);
    Observable<List<Train>> getTrains(@NonNull String station, @NonNull String line);
    Observable<Long> deleteAllTrains();
    Observable<Long> saveTrain(@NonNull Train route);
    void reloadTrains();

    boolean hasCachedData();
}
