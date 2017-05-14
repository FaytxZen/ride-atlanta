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
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.remote.buses.GetBusesIntentService;
import com.andrewvora.apps.rideatlanta.data.remote.trains.GetTrainsIntentService;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
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
public class FavoriteRoutesFragment extends Fragment implements FavoriteRoutesContract.View {

    public static final String TAG = FavoriteRoutesFragment.class.getSimpleName();

    interface AdapterCallback {
        void onUnfavorited(int position, @NonNull FavoriteRouteDataObject obj);
    }

    @BindView(R.id.favorite_routes_recycler_view) RecyclerView mFavoriteRoutesRecyclerView;
    @BindView(R.id.no_favorited_routes_view) View mEmptyStateView;

    private FavoriteRoutesContract.Presenter mPresenter;
    private FavoriteRoutesAdapter mFavRoutesAdapter;
    private BusesDataSource mBusRepo;
    private TrainsRepo mTrainRepo;
    private FavoriteRoutesDataSource mFavRouteRepo;

    private AdapterCallback mCallback = new AdapterCallback() {
        @Override
        public void onUnfavorited(int position, @NonNull FavoriteRouteDataObject obj) {
            // remove from database
            updateRouteInDatabase(getViewContext(), obj);

            // update the UI
            mFavRoutesAdapter.notifyItemRemoved(position);

            updateRecyclerView();
        }
    };

    public static FavoriteRoutesFragment newInstance() {
        return new FavoriteRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<FavoriteRouteDataObject> placeholderList = new ArrayList<>();
        mFavRoutesAdapter = new FavoriteRoutesAdapter(placeholderList, mCallback);

        mFavRouteRepo = FavoriteRoutesRepo.getInstance(getViewContext());
        mTrainRepo = TrainsRepo.getInstance(getViewContext());
        mBusRepo = BusesRepo.getInstance(getViewContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);
        ButterKnife.bind(this, view);

        mFavoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFavoriteRoutesRecyclerView.setAdapter(mFavRoutesAdapter);
        mFavoriteRoutesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getViewContext()));

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

        updateRecyclerView();
    }

    @Override
    public void onRouteInformationLoaded(FavoriteRouteDataObject favRoute) {
        final int adapterPosition = mFavRoutesAdapter.getPosition(favRoute);

        if(adapterPosition != FavoriteRoutesAdapter.NEW_INDEX) {
            mFavRoutesAdapter.setFavoriteRoute(adapterPosition, favRoute);

            notifyItemChanged(adapterPosition);
        }
        else {
            int insertedIndex = mFavRoutesAdapter.getItemCount();

            mFavRoutesAdapter.getFavoriteRoutes().add(favRoute);
            mFavRoutesAdapter.notifyItemInserted(insertedIndex);
        }
    }

    private void updateRecyclerView() {
        final boolean adapterIsEmpty = mFavRoutesAdapter.getItemCount() == 0;

        if(adapterIsEmpty) {
            mEmptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyStateView.setVisibility(View.GONE);
        }
    }

    private void notifyItemChanged(int position) {
        if(isAdded() && isResumed()) {
            mFavRoutesAdapter.notifyItemChanged(position);
        }
    }

    private void updateRouteInDatabase(@NonNull Context context,
                                       @NonNull FavoriteRouteDataObject route)
    {
        // update fav routes table
        mFavRouteRepo.deleteRoute(route);

        // update train and bus tables
        if(route.getType().equals(FavoriteRouteDataObject.TYPE_BUS)) {
            unfavoriteInBusTable(context, route);
        }
        else if(route.getType().equals(FavoriteRouteDataObject.TYPE_TRAIN)) {
            unfavoriteInTrainTable(context, route);
        }
    }

    private void unfavoriteInTrainTable(@NonNull final Context context,
                                        @NonNull FavoriteRouteDataObject route)
    {
        // load object
        Train trainArg = new Train();
        trainArg.setTrainId(Long.parseLong(route.getRouteId()));

        mTrainRepo.getTrain(trainArg, new TrainsDataSource.GetTrainRouteCallback() {
                    @Override
                    public void onFinished(Train train) {
                        train.setFavorited(false);

                        mTrainRepo.saveTrain(train);
                    }

                    @Override
                    public void onError(Object error) {

                    }
                });
    }

    private void unfavoriteInBusTable(@NonNull final Context context,
                                      @NonNull FavoriteRouteDataObject route)
    {
        // load object
        Bus busArg = new Bus();
        busArg.setRouteId(route.getRouteId());

        mBusRepo.getBus(busArg, new BusesDataSource.GetBusCallback() {
            @Override
            public void onFinished(Bus bus) {
                bus.setFavorited(false);

                mBusRepo.saveBus(bus);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }
}
