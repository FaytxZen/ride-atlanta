package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 5/12/2017.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesLoadingCache extends Fragment implements FavoriteRoutesContract.LoadingCache {

    public static final String TAG = FavoriteRoutesLoadingCache.class.getSimpleName();

    @Inject @Named(DataModule.FAVS_SOURCE) FavoriteRoutesDataSource favsRepo;

    @NonNull private List<FavoriteRouteDataObject> favRoutes;
    @NonNull private CompositeDisposable disposables;
    @Nullable private FavoriteRoutesContract.DataLoadedListener listener;

    public FavoriteRoutesLoadingCache() {
        this.favRoutes = new ArrayList<>();
        this.disposables = new CompositeDisposable();
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
	public void onDestroy() {
    	if (!disposables.isDisposed()) {
    		disposables.dispose();
		}
		super.onDestroy();
	}

	@Override
    public void setListener(@Nullable FavoriteRoutesContract.DataLoadedListener listener) {
        this.listener = listener;
    }

    @Override
	@Nullable
    public FavoriteRoutesContract.DataLoadedListener getListener() {
        return listener;
    }

    @Override
    public void loadFavoriteRoutes() {
        if(favRoutes.isEmpty()) {
        	final Disposable disposable = favsRepo.getFavoriteRoutes()
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeWith(new DisposableObserver<List<FavoriteRoute>>() {
						@Override
						public void onNext(@io.reactivex.annotations.NonNull List<FavoriteRoute> routes) {
							if(getListener() != null) {
								List<FavoriteRouteDataObject> result = new ArrayList<>();
								result.addAll(routes);
								setFavoritedRoutes(result);

								getListener().onFavoriteRoutesLoaded(getFavoriteRoutes());
							}
						}

						@Override
						public void onError(@io.reactivex.annotations.NonNull Throwable e) {}

						@Override
						public void onComplete() {}
					});

        	disposables.add(disposable);
        }
        // use cached results
        else if(listener != null) {
            listener.onFavoriteRoutesLoaded(favRoutes);
        }
    }

    @Override
    public void setFavoritedRoutes(@NonNull List<FavoriteRouteDataObject> favoritedTrains) {
        favRoutes = favoritedTrains;
    }

    List<FavoriteRouteDataObject> getFavoriteRoutes() {
        return favRoutes;
    }
}
