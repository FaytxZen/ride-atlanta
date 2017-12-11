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

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private TrainRoutesContract.Presenter presenter;
    private TrainRoutesAdapter trainAdapter;
    private TrainItemListener trainItemListener = new TrainItemListener() {
        @Override
        public void onItemClicked(int position) {
            final Train clickedTrain = trainAdapter.getTrain(position);
            final Intent detailIntent = RouteDetailsActivity.start(clickedTrain);
            startActivityForResult(detailIntent, 0);
        }

        @Override
        public void onFavoriteItem(int position) {
            presenter.favoriteRoute(trainAdapter.getTrain(position));

            // must be called after presenter method
            updateFavoriteStatusOf(trainAdapter.getTrain(position));

            trainAdapter.notifyItemChanged(position);
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
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refreshTrainRoutes();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        trainsRecyclerView.setAdapter(trainAdapter);
        trainsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        trainsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

        if(presenter != null) {
            presenter.onRestoreState(savedInstanceState);
        }

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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(presenter != null) {
            presenter.onSaveState(outState);
        }
    }

    @Override
    public void setPresenter(TrainRoutesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFavoriteStatusOf(@NonNull Train train) {
        String key = train.getFavoriteRouteKey();

        if(train.isFavorited()) {
            trainAdapter.getFavoriteRouteIds().add(key);
        }
        else {
            trainAdapter.getFavoriteRouteIds().remove(key);
        }
    }

    @Override
    public void applyFavorites(List<FavoriteRouteDataObject> favRoutes) {
        Set<String> favRouteIds = new HashSet<>();

        for(FavoriteRouteDataObject route : favRoutes) {
            favRouteIds.add(route.getFavoriteRouteKey());
        }

        trainAdapter.setFavoritedRouteIds(favRouteIds);
        trainAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrainRoutesLoaded(List<Train> trainList) {
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

    interface TrainItemListener {
        void onItemClicked(int position);
        void onFavoriteItem(int position);
    }
}
