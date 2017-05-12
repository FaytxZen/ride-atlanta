package com.andrewvora.apps.rideatlanta.buses;

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
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.remote.buses.GetBusesIntentService;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.buses_list) RecyclerView mBusesRecyclerView;
    @BindView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.loading_bus_routes_view) ProgressBar mProgressBar;
    @BindView(R.id.no_bus_routes_view) View mEmptyStateView;

    private BusRoutesContract.Presenter mPresenter;
    private BusRoutesAdapter mBusAdapter;
    private BusItemListener mBusItemListener = new BusItemListener() {
        @Override
        public void onItemClicked(Bus bus) {

        }

        @Override
        public void onFavoriteBus(int position) {
            mPresenter.favoriteRoute(mBusAdapter.getItemAtPosition(position));
            mBusAdapter.notifyItemChanged(position);
        }
    };

    public static BusRoutesFragment newInstance() {
        return new BusRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBusAdapter = new BusRoutesAdapter(null, mBusItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_routes, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mPresenter != null) {
                    mPresenter.refreshBusRoutes();
                }
            }
        });

        mBusesRecyclerView.setAdapter(mBusAdapter);
        mBusesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBusesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

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
    public void onBusRoutesLoaded(List<Bus> routesList) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);
        mBusAdapter.setBuses(routesList);
        mBusAdapter.notifyDataSetChanged();

        if(routesList.isEmpty()) {
            mEmptyStateView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPresenter(BusRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void subscribeReceiver(@NonNull BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GetBusesIntentService.ACTION_BUSES_UPDATED);

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
}
