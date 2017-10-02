package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.di.DataModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 5/12/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesLoadingCache extends Fragment implements FavoriteRoutesContract.LoadingCache {

    public static final String TAG = FavoriteRoutesLoadingCache.class.getSimpleName();

    @Inject @Named(DataModule.FAVS_SOURCE) FavoriteRoutesDataSource favsRepo;

    @NonNull private List<FavoriteRouteDataObject> mFavoriteRoutes;
    @Nullable private FavoriteRoutesContract.DataLoadedListener mListener;

    public FavoriteRoutesLoadingCache() {
        this.mFavoriteRoutes = new ArrayList<>();
    }

    public static FavoriteRoutesLoadingCache createInstance() {
        return new FavoriteRoutesLoadingCache();
    }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		AndroidInjection.inject(this);
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
            GetFavRoutesTask getFavRoutesTask = new GetFavRoutesTask(this, favsRepo);

            getFavRoutesTask.execute();
        }
        // use cached results
        else if(mListener != null) {
            mListener.onFavoriteRoutesLoaded(mFavoriteRoutes);
        }
    }

    @Override
    public void setFavoritedRoutes(@NonNull List<FavoriteRouteDataObject> favoritedTrains) {
        mFavoriteRoutes = favoritedTrains;
    }

    List<FavoriteRouteDataObject> getFavoriteRoutes() {
        return mFavoriteRoutes;
    }

    private static class GetFavRoutesTask extends
            AsyncTask<Void, Void, List<FavoriteRouteDataObject>>
    {
        private FavoriteRoutesLoadingCache mHolder;
        private FavoriteRoutesDataSource mFavRoutesRepo;

        GetFavRoutesTask(@Nullable FavoriteRoutesLoadingCache holder,
                         @NonNull FavoriteRoutesDataSource favRoutesRepo)
        {
            mHolder = holder;
            mFavRoutesRepo = favRoutesRepo;
        }

        @Override
        protected List<FavoriteRouteDataObject> doInBackground(Void... params) {
            mFavRoutesRepo.getFavoriteRoutes()
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
						@Override
						public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> routes) {
							if(mHolder.getListener() != null) {
								List<FavoriteRouteDataObject> result = new ArrayList<>();

								for(FavoriteRoute route : routes) {
									result.add(route);
								}

								mHolder.setFavoritedRoutes(result);

								passDataToListener();
							}
						}

						@Override
						public void onError(@io.reactivex.annotations.NonNull Throwable e) {

						}

						@Override
						public void onComplete() {

						}
					});

            return null;
        }

        private void passDataToListener() {
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mHolder != null && mHolder.getListener() != null) {
                        mHolder.getListener().onFavoriteRoutesLoaded(mHolder.getFavoriteRoutes());
                    }
                }
            });
        }
    }
}
