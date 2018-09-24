package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.di.DataModule;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesFragment extends Fragment implements FavoriteRoutesContract.View {

    public static final String TAG = FavoriteRoutesFragment.class.getSimpleName();

    interface AdapterCallback {
        void onFavoriteClicked(int position, @NonNull FavoriteRouteDataObject obj);
        void onItemClicked(int position, @NonNull FavoriteRouteDataObject obj);
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
	    public void onFavoriteClicked(int position, @NonNull FavoriteRouteDataObject obj) {
		    if (presenter != null) {
			    presenter.removeRouteFromFavorites(position, obj);
		    }
	    }

	    @Override
	    public void onItemClicked(int position, @NonNull FavoriteRouteDataObject obj) {
			if (presenter != null) {
				presenter.routeClicked(position, obj);
			}
	    }
    };
    private Unbinder unbinder;

    public static FavoriteRoutesFragment newInstance() {
        return new FavoriteRoutesFragment();
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
        unbinder = ButterKnife.bind(this, view);

        favoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteRoutesRecyclerView.setAdapter(favRoutesAdapter);
        favoriteRoutesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

        return view;
    }

	@Override
	public void onStart() {
		super.onStart();
		if(presenter != null) {
			presenter.start();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(presenter != null) {
			presenter.stop();
		}

		disposables.dispose();
		disposables.clear();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
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

    private void updateRecyclerView() {
    	if (emptyStateView == null) { return; }

        final boolean adapterIsEmpty = favRoutesAdapter.getItemCount() == 0;

        if(adapterIsEmpty) {
            emptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            emptyStateView.setVisibility(View.GONE);
        }
    }

	@Override
	public void onRouteUpdated(int position, @NonNull FavoriteRouteDataObject route) {
		favRoutesAdapter.getFavoriteRoutes().remove(position);
		favRoutesAdapter.notifyItemRemoved(position);

		updateRecyclerView();
	}

	@Override
	public void openRouteDetails(@NonNull FavoriteRouteDataObject route) {
		final Intent intent = RouteDetailsActivity.start(getViewContext(), route);
		getViewContext().startActivity(intent);
	}

	@Override
	public void showLoadingError() {
		Toast.makeText(getViewContext(), R.string.error_load_favorites, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showUnfavoriteError() {
		Toast.makeText(getViewContext(), R.string.error_unfavorite, Toast.LENGTH_SHORT).show();
	}
}
