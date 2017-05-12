package com.andrewvora.apps.rideatlanta.trains;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.remote.trains.GetTrainsIntentService;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesFragment extends Fragment implements TrainRoutesContract.View {

    public static final String TAG = TrainRoutesFragment.class.getSimpleName();

    @BindView(R.id.trains_list) RecyclerView mTrainsRecyclerView;
    @BindView(R.id.loading_train_routes_view) ProgressBar mProgressBar;
    @BindView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.no_trains_running_view) View mEmptyStateView;

    private TrainRoutesContract.Presenter mPresenter;
    private TrainRoutesAdapter mTrainAdapter;
    private TrainItemListener mTrainItemListener = new TrainItemListener() {
        @Override
        public void onItemClicked(int position) {

        }

        @Override
        public void onFavoriteItem(int position) {
            mPresenter.favoriteRoute(mTrainAdapter.getTrain(position));
            mTrainAdapter.notifyItemChanged(position);
        }
    };

    public static TrainRoutesFragment newInstance() {
        return new TrainRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Train> placeholderList = new ArrayList<>();
        mTrainAdapter = new TrainRoutesAdapter(placeholderList, mTrainItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_routes, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshTrainRoutes();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mTrainsRecyclerView.setAdapter(mTrainAdapter);
        mTrainsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrainsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

        if(mPresenter != null) {
            mPresenter.onRestoreState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mPresenter != null) {
            mPresenter.stop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mPresenter != null) {
            mPresenter.onSaveState(outState);
        }
    }

    @Override
    public void setPresenter(TrainRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onTrainRoutesLoaded(List<Train> trainList) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        mTrainAdapter.setTrains(trainList);
        mTrainAdapter.notifyDataSetChanged();

        if(trainList.isEmpty()) {
            mEmptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public void subscribeReceiver(@NonNull BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GetTrainsIntentService.ACTION_TRAINS_UPDATED);

        LocalBroadcastManager.getInstance(getViewContext())
                .registerReceiver(receiver, intentFilter);
    }

    @Override
    public void unsubscribeReceiver(@NonNull BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(getViewContext())
                .unregisterReceiver(receiver);
    }

    @Override
    public Context getViewContext() {
        return getActivity().getApplication();
    }

    public interface TrainItemListener {
        void onItemClicked(int position);
        void onFavoriteItem(int position);
    }
}
