package com.andrewvora.apps.rideatlanta.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class HomePresenter implements HomeContract.Presenter {

    private Context mContext;
    private HomeContract.View mView;

    public HomePresenter(@NonNull Context context, @NonNull HomeContract.View view) {
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

    }

    @Override
    public void loadAlerts() {

    }

    @Override
    public void loadInfoItems() {

    }

    @Override
    public void loadFavoriteRoutes() {

    }
}
