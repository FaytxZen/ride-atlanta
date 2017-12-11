package com.andrewvora.apps.rideatlanta.home;

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

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class HomeFragment extends Fragment implements HomeContract.View, HomeAdapter.Listener {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.home_recycler_view) RecyclerView homeRecyclerView;

    private HomeContract.Presenter presenter;
    private HomeAdapter homeAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeAdapter = new HomeAdapter(new ArrayList<HomeItemModel>(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeRecyclerView.setAdapter(homeAdapter);

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
    public void setPresenter(HomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayAlerts(@NonNull List<AlertItemModel> alertItems) {
        for(AlertItemModel alertItem : alertItems) {
            int position = homeAdapter.addListItem(alertItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public void displayInfoItems(@NonNull List<InfoItemModel> infoItems) {
        for(InfoItemModel infoItem : infoItems) {
            int position = homeAdapter.addListItem(infoItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public void displayRouteItems(@NonNull List<RouteItemModel> routeItems) {
        for(RouteItemModel routeItem : routeItems) {
            int position = homeAdapter.addListItem(routeItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }

    private void updateItemInAdapter(int position) {
        if(position >= 0) {
            homeAdapter.notifyItemInserted(position);
            homeRecyclerView.smoothScrollToPosition(0);
        }
        else {
            homeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void openRouteInfo(FavoriteRoute route) {
        final Intent detailsIntent = RouteDetailsActivity.start(route);
        startActivityForResult(detailsIntent, 0);
    }
}
