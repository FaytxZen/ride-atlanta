package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesPresenter implements FavoriteRoutesContract.Presenter {

    private Context mContext;
    private FavoriteRoutesContract.View mView;

    public FavoriteRoutesPresenter(@NonNull Context context,
                                   @NonNull FavoriteRoutesContract.View view)
    {
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
        loadFavoriteRoutes();
    }

    @Override
    public void loadFavoriteRoutes() {
        FavoriteRoutesRepo repo = FavoriteRoutesRepo.getInstance(mContext);

        repo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
            @Override
            public void onFinished(List<FavoriteRoute> favRoutes) {

            }

            @Override
            public void onError(Object error) {

            }
        });
    }
}
