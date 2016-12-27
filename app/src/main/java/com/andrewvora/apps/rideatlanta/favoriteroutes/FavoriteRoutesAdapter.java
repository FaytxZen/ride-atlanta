package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.FavoriteRouteDataObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by faytx on 12/26/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesAdapter extends
        RecyclerView.Adapter<FavoriteRoutesAdapter.FavoriteRoutesViewHolder>
{
    private List<FavoriteRouteDataObject> mFavoriteRoutesList;

    public FavoriteRoutesAdapter(@Nullable List<FavoriteRouteDataObject> favoriteRoutes) {
        mFavoriteRoutesList = checkNotNull(favoriteRoutes);
    }

    @Override
    public FavoriteRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_favorite_route, parent, false);

        return new FavoriteRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteRoutesViewHolder holder, int position) {
        FavoriteRouteDataObject favoriteRoute = mFavoriteRoutesList.get(position);

        final boolean isBusRoute = favoriteRoute.getType() != null &&
                favoriteRoute.getType().equals(FavoriteRouteDataObject.TYPE_BUS);

        final boolean isTrainRoute = favoriteRoute.getType() != null &&
                favoriteRoute.getType().equals(FavoriteRouteDataObject.TYPE_TRAIN);

        if(isBusRoute) {
            onBindFavoriteBusRouteHolder(holder, position);
        }
        else if(isTrainRoute) {
            onBindFavoriteTrainRouteHolder(holder, position);
        }
    }

    private void onBindFavoriteBusRouteHolder(FavoriteRoutesViewHolder holder, int position) {

    }

    private void onBindFavoriteTrainRouteHolder(FavoriteRoutesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mFavoriteRoutesList.size();
    }

    private List<FavoriteRouteDataObject> checkNotNull(List<FavoriteRouteDataObject> favoriteRouteList) {
        if(favoriteRouteList == null) {
            favoriteRouteList = new ArrayList<>();
        }

        return favoriteRouteList;
    }

    public static class FavoriteRoutesViewHolder extends RecyclerView.ViewHolder {



        public FavoriteRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
