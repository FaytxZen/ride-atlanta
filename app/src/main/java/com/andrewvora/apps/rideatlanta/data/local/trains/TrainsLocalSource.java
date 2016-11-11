package com.andrewvora.apps.rideatlanta.data.local.trains;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.common.models.Train;
import com.andrewvora.apps.rideatlanta.data.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsLocalSource implements TrainsDataSource {

    private static TrainsLocalSource mInstance;
    private RideAtlantaDbHelper mDbHelper;

    private TrainsLocalSource(@NonNull Context context) {
        mDbHelper = new RideAtlantaDbHelper(context);
    }

    public static TrainsLocalSource getInstance(@NonNull Context context) {

        if(mInstance == null) {
            mInstance = new TrainsLocalSource(context);
        }

        return mInstance;
    }

    @Override
    public void getTrains(@NonNull GetTrainRoutesCallback callback) {

    }

    @Override
    public void getTrain(@NonNull Long trainId, @NonNull GetTrainRouteCallback callback) {

    }

    @Override
    public void deleteAllTrains(@Nullable DeleteTrainRoutesCallback callback) {

    }

    @Override
    public void saveTrain(@NonNull Train route) {

    }

    @Override
    public void reloadTrains() {
        // refreshing handled in the TrainRepo
    }
}
