package com.andrewvora.apps.rideatlanta.buses;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andrewvora.apps.rideatlanta.BuildConfig;
import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesFragment extends Fragment implements BusRoutesContract.View {

    public static final String TAG = BusRoutesFragment.class.getSimpleName();

    interface BusItemListener {
        void onItemClicked(Bus bus);
        void onFavoriteBus(int position);
    }

    @BindView(R.id.buses_list) RecyclerView busesRecyclerView;
    @BindView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.loading_bus_routes_view) ProgressBar progressBar;
    @BindView(R.id.no_bus_routes_view) View emptyStateView;

    private Unbinder unbinder;
    private BusRoutesContract.Presenter presenter;
    private BusRoutesAdapter busAdapter;
    private BusItemListener busItemListener = new BusItemListener() {
        @Override
        public void onItemClicked(Bus bus) {
            final Intent detailIntent = RouteDetailsActivity.start(getActivity(), bus);
            startActivityForResult(detailIntent, 0);
        }

        @Override
        public void onFavoriteBus(int position) {
            presenter.favoriteRoute(position, busAdapter.getItemAtPosition(position));
            busAdapter.notifyItemChanged(position);
        }
    };

    public static BusRoutesFragment newInstance() {
        return new BusRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busAdapter = new BusRoutesAdapter(null, busItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_routes, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
			if(presenter != null) {
				presenter.refreshBusRoutes();
			}
		});

        busesRecyclerView.setAdapter(busAdapter);
        busesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        busesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

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
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
    public void onBusRoutesLoaded(List<Bus> routesList) {
    	if (swipeRefreshLayout == null) {
    		return;
	    }
        busAdapter.setBuses(routesList);
        busAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(BusRoutesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getViewContext() {
        return getActivity().getApplication();
    }

    @Override
    public void refreshError(@NonNull Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.d(BusRoutesFragment.class.getSimpleName(), e.getMessage());
        }
        Toast.makeText(getViewContext(), R.string.error_refresh_buses, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onRouteUpdated(int position, @NonNull Bus bus) {
		busAdapter.getItemAtPosition(position).setFavorited(bus.isFavorited());
		busAdapter.notifyItemChanged(position);
	}

	@Override
	public void favoriteError() {
		Toast.makeText(getViewContext(), R.string.error_could_not_favorite_bus, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void hideLoadingView() {
    	if (isAdded()) {
		    swipeRefreshLayout.setRefreshing(false);
		    progressBar.setVisibility(View.GONE);
	    }
	}

	@Override
	public void showEmptyState() {
		emptyStateView.setVisibility(View.VISIBLE);
		busesRecyclerView.setVisibility(View.GONE);
	}

	@Override
	public void hideEmptyState() {
    	if (isAdded()) {
		    emptyStateView.setVisibility(View.GONE);
		    busesRecyclerView.setVisibility(View.VISIBLE);
	    }
	}
}
