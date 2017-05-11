package com.andrewvora.apps.rideatlanta.home;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;
import com.andrewvora.apps.rideatlanta.data.remote.buses.GetBusesIntentService;
import com.andrewvora.apps.rideatlanta.data.remote.trains.GetTrainsIntentService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class HomeFragment extends Fragment implements HomeContract.View {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.home_recycler_view) RecyclerView mHomeRecyclerView;

    private HomeContract.Presenter mPresenter;
    private HomeAdapter mHomeAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHomeAdapter = new HomeAdapter(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHomeRecyclerView.setAdapter(mHomeAdapter);

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
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void displayAlerts(@NonNull List<AlertItemModel> alertItems) {
        for(AlertItemModel alertItem : alertItems) {
            int position = mHomeAdapter.addListItem(alertItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public void displayInfoItems(@NonNull List<InfoItemModel> infoItems) {
        for(InfoItemModel infoItem : infoItems) {
            int position = mHomeAdapter.addListItem(infoItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public void displayRouteItems(@NonNull List<RouteItemModel> routeItems) {
        for(RouteItemModel routeItem : routeItems) {
            int position = mHomeAdapter.addListItem(routeItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }

    @Override
    public void subscribeReceiver(@NonNull BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GetBusesIntentService.ACTION_BUSES_UPDATED);
        intentFilter.addAction(GetTrainsIntentService.ACTION_TRAINS_UPDATED);

        LocalBroadcastManager.getInstance(getViewContext())
                .registerReceiver(receiver, intentFilter);
    }

    @Override
    public void unsubscribeReceiver(@NonNull BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(getViewContext())
                .unregisterReceiver(receiver);
    }

    private void updateItemInAdapter(int position) {
        if(position >= 0) {
            mHomeAdapter.notifyItemInserted(position);
            mHomeRecyclerView.smoothScrollToPosition(0);
        }
        else {
            mHomeAdapter.notifyDataSetChanged();
        }
    }
}
