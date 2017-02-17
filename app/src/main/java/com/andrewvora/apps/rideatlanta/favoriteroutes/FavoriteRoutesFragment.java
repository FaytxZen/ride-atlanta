package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;

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
    public void setPresenter(FavoriteRoutesContract.Presenter presenter) {
        mPresenter = presenter;
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
        int curPosition = mFavRoutesAdapter.getPosition(favRoute);
        mFavRoutesAdapter.addFavoriteRoute(favRoute);

        if(curPosition < 0) {
            curPosition = mFavRoutesAdapter.getPosition(favRoute);
        }

        notifyItemChanged(curPosition);
    }

    private void notifyItemChanged(int position) {
        if(isAdded()) {
            mFavRoutesAdapter.notifyItemInserted(position);
        }
    }
}
