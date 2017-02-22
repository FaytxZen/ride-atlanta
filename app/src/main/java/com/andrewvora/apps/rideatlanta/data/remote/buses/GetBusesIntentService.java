package com.andrewvora.apps.rideatlanta.data.remote.buses;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;

import java.util.List;

/**
 * Created by faytx on 2/21/2017.
 * @author Andrew Vorakrajangthiti
 */
public class GetBusesIntentService extends IntentService {

    public static final String SERVICE_NAME = GetBusesIntentService.class.getSimpleName();
    public static final String ACTION_BUSES_UPDATED = "BusesCacheHasBeenUpdated";

    public GetBusesIntentService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        BusesDataSource busesDataSource = BusesRepo.getInstance(getApplicationContext());
        busesDataSource.reloadBuses();
        busesDataSource.getBuses(new BusesDataSource.GetBusesCallback() {
            @Override
            public void onFinished(List<Bus> buses) {
                onBusesUpdated();
            }

            @Override
            public void onError(Object error) { }
        });
    }

    private void onBusesUpdated() {
        Intent busesUpdatedIntent = new Intent(ACTION_BUSES_UPDATED);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(busesUpdatedIntent);
    }
}
