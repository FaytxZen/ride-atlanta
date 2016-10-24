package com.andrewvora.apps.rideatlanta.data.local.trains;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.common.models.Train;
import com.andrewvora.apps.rideatlanta.data.TrainsDataSource;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsLocalSource implements TrainsDataSource {

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

    }
}
