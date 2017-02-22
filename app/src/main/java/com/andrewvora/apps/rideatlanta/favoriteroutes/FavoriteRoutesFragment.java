package com.andrewvora.apps.rideatlanta.favoriteroutes;

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
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.remote.buses.GetBusesIntentService;
import com.andrewvora.apps.rideatlanta.data.remote.trains.GetTrainsIntentService;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesFragment extends Fragment implements FavoriteRoutesContract.View {

    public static final String TAG = FavoriteRoutesFragment.class.getSimpleName();

    @BindView(R.id.favorite_routes_recycler_view) RecyclerView mFavoriteRoutesRecyclerView;
    @BindView(R.id.no_favorited_routes_view) View mEmptyStateView;

    private FavoriteRoutesContract.Presenter mPresenter;
    private FavoriteRoutesAdapter mFavRoutesAdapter;

    public static FavoriteRoutesFragment newInstance() {
        return new FavoriteRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFavRoutesAdapter = new FavoriteRoutesAdapter(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);
        ButterKnife.bind(this, view);

        mFavoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFavoriteRoutesRecyclerView.setAdapter(mFavRoutesAdapter);

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
    public void setPresenter(FavoriteRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getViewContext() {
        return getActivity().getApplication();
    }

    @Override
    public TrainsDataSource getTrainDataSource() {
        return TrainsRepo.getInstance(getViewContext());
    }

    @Override
    public BusesDataSource getBusesDataSource() {
        return BusesRepo.getInstance(getViewContext());
    }

    @Override
    public FavoriteRoutesDataSource getFavoritesDataSource() {
        return FavoriteRoutesRepo.getInstance(getViewContext());
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

    @Override
    public void onFavoriteRoutesLoaded(List<FavoriteRouteDataObject> favRoutes) {
        mFavRoutesAdapter.setFavoriteRoutes(favRoutes);
        mFavRoutesAdapter.notifyDataSetChanged();

        if(favRoutes.isEmpty()) {
            mEmptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRouteInformationLoaded(FavoriteRouteDataObject favRoute) {
        final boolean isExistingRoute = mFavRoutesAdapter.getPosition(favRoute) !=
                FavoriteRoutesAdapter.NEW_INDEX;

        mFavRoutesAdapter.setFavoriteRoute(favRoute);

        int position = mFavRoutesAdapter.getPosition(favRoute);
        if(isExistingRoute) {
            notifyItemChanged(position);
        }
    }

    private void notifyItemChanged(int position) {
        if(isAdded() && isResumed()) {
            mFavRoutesAdapter.notifyItemChanged(position);
        }
    }
}
