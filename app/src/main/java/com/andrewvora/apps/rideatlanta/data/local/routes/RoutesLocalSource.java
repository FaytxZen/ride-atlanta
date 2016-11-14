package com.andrewvora.apps.rideatlanta.data.local.routes;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.RoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class RoutesLocalSource implements RoutesDataSource {

    private static RoutesLocalSource mInstance;
    private RideAtlantaDbHelper mDbHelper;

    private RoutesLocalSource(@NonNull Context context) {
        mDbHelper = new RideAtlantaDbHelper(context);
    }

    public static RoutesLocalSource getInstance(@NonNull Context context) {
        if(mInstance == null) {
            mInstance = new RoutesLocalSource(context);
        }

        return mInstance;
    }
}
