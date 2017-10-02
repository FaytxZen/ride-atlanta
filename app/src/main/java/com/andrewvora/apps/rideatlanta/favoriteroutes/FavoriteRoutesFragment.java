package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.di.DataModule;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesFragment extends Fragment implements FavoriteRoutesContract.View {

    public static final String TAG = FavoriteRoutesFragment.class.getSimpleName();

    interface AdapterCallback {
        void onUnfavorited(int position, @NonNull FavoriteRouteDataObject obj);
    }

    @BindView(R.id.favorite_routes_recycler_view) RecyclerView favoriteRoutesRecyclerView;
    @BindView(R.id.no_favorited_routes_view) View emptyStateView;

    private FavoriteRoutesContract.Presenter presenter;
    private FavoriteRoutesAdapter favRoutesAdapter;
	private CompositeDisposable disposables = new CompositeDisposable();

    @Inject @Named(DataModule.BUS_SOURCE)
	BusesDataSource busRepo;
    @Inject @Named(DataModule.TRAIN_SOURCE)
	TrainsDataSource trainRepo;
    @Inject @Named(DataModule.FAVS_SOURCE)
	FavoriteRoutesDataSource favRoutesRepo;

    private AdapterCallback adapterCallback = new AdapterCallback() {
        @Override
        public void onUnfavorited(int position, @NonNull FavoriteRouteDataObject obj) {
			onRouteUnfavorited(position, obj);
        }
    };

    public static FavoriteRoutesFragment newInstance() {
        return new FavoriteRoutesFragment();
    }

    private void onRouteUnfavorited(final int position, @NonNull final FavoriteRouteDataObject obj) {
		disposables.add(Completable.fromAction(new Action() {
			@Override
			public void run() throws Exception {
				updateRouteInDatabase(obj);
			}
		})
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		.subscribeWith(new DisposableCompletableObserver() {
			@Override
			public void onComplete() {
				// update the UI
				favRoutesAdapter.notifyItemRemoved(position);
				updateRecyclerView();
			}

			@Override
			public void onError(@io.reactivex.annotations.NonNull Throwable e) { }
		}));
	}

	@Override
	public void onAttach(Context context) {
		AndroidInjection.inject(this);
		super.onAttach(context);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<FavoriteRouteDataObject> placeholderList = new ArrayList<>();
        favRoutesAdapter = new FavoriteRoutesAdapter(placeholderList, adapterCallback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);
        ButterKnife.bind(this, view);

        favoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteRoutesRecyclerView.setAdapter(favRoutesAdapter);
        favoriteRoutesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(presenter != null) {
            presenter.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(presenter != null) {
            presenter.stop();
        }

        disposables.dispose();
		disposables.clear();
    }

    @Override
    public void setPresenter(FavoriteRoutesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getViewContext() {
        return getActivity().getApplication();
    }

    @Override
    public void onFavoriteRoutesLoaded(List<FavoriteRouteDataObject> favRoutes) {
        favRoutesAdapter.setFavoriteRoutes(favRoutes);
        favRoutesAdapter.notifyDataSetChanged();

        updateRecyclerView();
    }

    @Override
    public void onRouteInformationLoaded(FavoriteRouteDataObject favRoute) {
        final int adapterPosition = favRoutesAdapter.getPosition(favRoute);

        if(adapterPosition != FavoriteRoutesAdapter.NEW_INDEX) {
            favRoutesAdapter.setFavoriteRoute(adapterPosition, favRoute);

            notifyItemChanged(adapterPosition);
        }
        else {
            int insertedIndex = favRoutesAdapter.getItemCount();

            favRoutesAdapter.getFavoriteRoutes().add(favRoute);
            favRoutesAdapter.notifyItemInserted(insertedIndex);
        }
    }

    private void updateRecyclerView() {
        final boolean adapterIsEmpty = favRoutesAdapter.getItemCount() == 0;

        if(adapterIsEmpty) {
            emptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            emptyStateView.setVisibility(View.GONE);
        }
    }

    private void notifyItemChanged(int position) {
        if(isAdded() && isResumed()) {
            favRoutesAdapter.notifyItemChanged(position);
        }
    }

    private void updateRouteInDatabase(@NonNull FavoriteRouteDataObject route) {
        // update fav routes table
        favRoutesRepo.deleteRoute(route);

        // update train and bus tables
        if(route.getType().equals(FavoriteRouteDataObject.TYPE_BUS)) {
            unfavoriteInBusTable(route);
        }
        else if(route.getType().equals(FavoriteRouteDataObject.TYPE_TRAIN)) {
            unfavoriteInTrainTable(route);
        }
    }

    private void unfavoriteInTrainTable(@NonNull FavoriteRouteDataObject route) {
        // load object
        Train trainArg = new Train();
        trainArg.setTrainId(Long.parseLong(route.getRouteId()));

        disposables.add(trainRepo.getTrain(trainArg)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableObserver<Train>() {
					@Override
					public void onNext(@io.reactivex.annotations.NonNull Train train) {
						train.setFavorited(false);
						trainRepo.saveTrain(train);
					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

					@Override
					public void onComplete() { }
				}));
    }

    private void unfavoriteInBusTable(@NonNull FavoriteRouteDataObject route) {
        // load object
        Bus busArg = new Bus();
        busArg.setRouteId(route.getRouteId());

        disposables.add(busRepo.getBus(busArg)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeWith(new DisposableObserver<Bus>() {
				@Override
				public void onNext(@io.reactivex.annotations.NonNull Bus bus) {
					bus.setFavorited(false);

					busRepo.saveBus(bus);
				}

				@Override
				public void onError(@io.reactivex.annotations.NonNull Throwable e) { }

				@Override
				public void onComplete() { }
			}));
    }
}
