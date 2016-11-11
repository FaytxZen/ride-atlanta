package com.andrewvora.apps.rideatlanta.trains;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.models.Train;
import com.andrewvora.apps.rideatlanta.data.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.TrainsRepo;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainRoutesPresenter implements TrainRoutesContract.Presenter {

    private Context mContext;
    private TrainRoutesContract.View mView;

    public TrainRoutesPresenter(@NonNull Context context, @NonNull TrainRoutesContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onSaveState(Bundle outState) {

    }

    @Override
    public void onRestoreState(Bundle savedState) {

    }

    @Override
    public void start() {
        loadTrainRoutes();
    }

    @Override
    public void loadTrainRoutes() {
        TrainsRepo trainsRepo = TrainsRepo.getInstance(mContext);
        trainsRepo.reloadTrains();
        trainsRepo.getTrains(new TrainsDataSource.GetTrainRoutesCallback() {
            @Override
            public void onFinished(List<Train> trainList) {
                mView.onTrainRoutesLoaded(trainList);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }
}
