package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface TrainsDataSource {

    interface GetTrainRoutesCallback {
        void onFinished(List<Train> trainList);
        void onError(Object error);
    }

    interface GetTrainRouteCallback {
        void onFinished(Train train);
        void onError(Object error);
    }

    interface DeleteTrainRoutesCallback {
        void onDeleted();
        void onError(Object error);
    }

    void getTrains(@NonNull GetTrainRoutesCallback callback);
    void getTrains(@NonNull GetTrainRoutesCallback callback, @NonNull Long... trainIds);
    void getTrain(@NonNull Train train, @NonNull GetTrainRouteCallback callback);
    void getTrains(@NonNull String station, @NonNull String line, @NonNull GetTrainRoutesCallback callback);
    void deleteAllTrains(@Nullable DeleteTrainRoutesCallback callback);
    void saveTrain(@NonNull Train route);
    void reloadTrains();
}
