package com.andrewvora.apps.rideatlanta.trains;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesFragment extends Fragment implements TrainRoutesContract.View {

    public static final String TAG = TrainRoutesFragment.class.getSimpleName();

    @BindView(R.id.trains_list) RecyclerView trainsRecyclerView;
    @BindView(R.id.loading_train_routes_view) ProgressBar progressBar;
    @BindView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_trains_running_view) View emptyStateView;

    private Unbinder unbinder;
    private TrainRoutesContract.Presenter presenter;
    private TrainRoutesAdapter trainAdapter;
    private TrainItemListener trainItemListener = new TrainItemListener() {
        @Override
        public void onItemClicked(int position) {
            final Train clickedTrain = trainAdapter.getTrain(position);
            final Intent detailIntent = RouteDetailsActivity.start(getActivity(), clickedTrain);
            startActivityForResult(detailIntent, 0);
        }

        @Override
        public void onFavoriteItem(int position) {
            presenter.favoriteRoute(position, trainAdapter.getTrain(position));
        }
    };

    public static TrainRoutesFragment newInstance() {
        return new TrainRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Train> placeholderList = new ArrayList<>();
        trainAdapter = new TrainRoutesAdapter(placeholderList, trainItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_routes, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
			presenter.refreshTrainRoutes();
			swipeRefreshLayout.setRefreshing(false);
		});

        trainsRecyclerView.setAdapter(trainAdapter);
        trainsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        trainsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

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
    public void setPresenter(TrainRoutesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onTrainRoutesLoaded(List<Train> trainList) {
    	if (swipeRefreshLayout == null) {
    		return;
	    }

        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        trainAdapter.setTrains(trainList);
        trainAdapter.notifyDataSetChanged();

        if(trainList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            emptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public Context getViewContext() {
        return getActivity().getApplication();
    }

	@Override
	public void onRouteUpdated(int position, @NonNull Train train) {
    	trainAdapter.getTrains().set(position, train);
		trainAdapter.notifyItemChanged(position);
	}

	@Override
	public void showLoadingError() {
		Toast.makeText(getViewContext(), R.string.error_loading_trains, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showFavoriteError() {
		Toast.makeText(getViewContext(), R.string.error_could_not_favorite_train, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void hideLoadingView() {
		swipeRefreshLayout.setRefreshing(false);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void showEmptyState() {
    	trainsRecyclerView.setVisibility(View.GONE);
		emptyStateView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideEmptyState() {
    	trainsRecyclerView.setVisibility(View.VISIBLE);
		emptyStateView.setVisibility(View.GONE);
	}

	interface TrainItemListener {
        void onItemClicked(int position);
        void onFavoriteItem(int position);
    }
}
