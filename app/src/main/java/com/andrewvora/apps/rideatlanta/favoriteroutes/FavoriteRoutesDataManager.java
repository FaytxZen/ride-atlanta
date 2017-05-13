package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 5/12/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesDataManager extends Fragment implements FavoriteRoutesContract.DataHolder {

    public static final String TAG = FavoriteRoutesDataManager.class.getSimpleName();

    @NonNull private List<FavoriteRouteDataObject> mFavoriteRoutes;
    @Nullable private FavoriteRoutesContract.DataLoadedListener mListener;

    public FavoriteRoutesDataManager() {
        this.mFavoriteRoutes = new ArrayList<>();
    }

    public static FavoriteRoutesDataManager createInstance() {
        return new FavoriteRoutesDataManager();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void setListener(FavoriteRoutesContract.DataLoadedListener listener) {
        mListener = listener;
    }

    @Override
    public FavoriteRoutesContract.DataLoadedListener getListener() {
        return mListener;
    }

    @Override
    public void loadFavoriteRoutes() {
        if(mFavoriteRoutes.isEmpty()) {
            GetFavRoutesTask getFavRoutesTask = new GetFavRoutesTask(
                    this,
                    FavoriteRoutesRepo.getInstance(getActivity().getApplication()));

            getFavRoutesTask.execute();
        }
        // use cached results
        else if(mListener != null) {
            mListener.onLoaded(mFavoriteRoutes);
        }
    }

    @Override
    public void setFavoritedRoutes(@NonNull List<FavoriteRouteDataObject> favoritedTrains) {
        mFavoriteRoutes = favoritedTrains;
    }

    @Override
    public void addFavoritedRoute(@NonNull FavoriteRouteDataObject route) {
        mFavoriteRoutes.add(route);
    }

    @Override
    public void removeFavoriteRoute(@NonNull FavoriteRouteDataObject route) {
        for(FavoriteRouteDataObject curRoute : mFavoriteRoutes) {
            if(route.getRouteId().equals(curRoute.getRouteId())) {
                mFavoriteRoutes.remove(curRoute);
                break;
            }
        }
    }

    private static class GetFavRoutesTask extends
            AsyncTask<Void, Void, List<FavoriteRouteDataObject>>
    {
        private FavoriteRoutesContract.DataHolder mHolder;
        private FavoriteRoutesDataSource mFavRoutesRepo;

        GetFavRoutesTask(@Nullable FavoriteRoutesContract.DataHolder holder,
                         @NonNull FavoriteRoutesDataSource favRoutesRepo)
        {
            mHolder = holder;
            mFavRoutesRepo = favRoutesRepo;
        }

        @Override
        protected List<FavoriteRouteDataObject> doInBackground(Void... params) {
            mFavRoutesRepo.getFavoriteRoutes(new FavoriteRoutesDataSource.GetFavoriteRoutesCallback() {
                @Override
                public void onFinished(List<FavoriteRoute> favRoutes) {
                    if(mHolder.getListener() != null) {
                        List<FavoriteRouteDataObject> result = new ArrayList<>();

                        for(FavoriteRoute route : favRoutes) {
                            result.add(route);
                        }

                        mHolder.setFavoritedRoutes(result);
                        mHolder.getListener().onLoaded(result);
                    }
                }

                @Override
                public void onError(Object error) {

                }
            });

            return null;
        }
    }
}
