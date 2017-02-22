package com.andrewvora.apps.rideatlanta.data.remote.trains;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;

import java.util.List;

/**
 * Created by faytx on 2/21/2017.
 * @author Andrew Vorakrajangthiti
 */
public class GetTrainsIntentService extends IntentService {

    public static final String SERVICE_NAME = GetTrainsIntentService.class.getSimpleName();
    public static final String ACTION_TRAINS_UPDATED = "TrainCacheUpdated";

    public GetTrainsIntentService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TrainsDataSource trainsDataSource = TrainsRepo.getInstance(getApplicationContext());

        trainsDataSource.reloadTrains();
        trainsDataSource.getTrains(new TrainsDataSource.GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                onTrainsUpdated();
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void onTrainsUpdated() {
        Intent trainsUpdatedIntent = new Intent(ACTION_TRAINS_UPDATED);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(trainsUpdatedIntent);
    }
}
