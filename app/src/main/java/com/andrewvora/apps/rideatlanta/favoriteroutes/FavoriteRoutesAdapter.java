package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 12/26/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesAdapter extends
        RecyclerView.Adapter<FavoriteRoutesAdapter.FavoriteRoutesViewHolder>
{
    private List<FavoriteRouteDataObject> mFavoriteRoutesList;
    private Map<String, Integer> mFavoriteRoutesMap;

    public FavoriteRoutesAdapter(@Nullable List<FavoriteRouteDataObject> favoriteRoutes) {
        mFavoriteRoutesList = checkNotNull(favoriteRoutes);
        mFavoriteRoutesMap = new LinkedHashMap<>();

        if(favoriteRoutes != null) {
            addRoutesToMap(favoriteRoutes);
        }
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

    public void setFavoriteRoutes(List<FavoriteRouteDataObject> routes) {
        mFavoriteRoutesList = routes;
        addRoutesToMap(routes);
    }

    public void addFavoriteRoute(FavoriteRouteDataObject route) {
        String key = getMapKeyForRoute(route);

        if(mFavoriteRoutesMap.containsKey(key)) {
            int position = mFavoriteRoutesMap.get(key);
            mFavoriteRoutesList.set(position, route);

        }
        else {
            mFavoriteRoutesList.add(route);

            addRouteToMap(route, mFavoriteRoutesList.size() - 1);
            notifyItemInserted(mFavoriteRoutesList.size() - 1);
        }
    }

    public int getPosition(@NonNull FavoriteRouteDataObject route) {
        return mFavoriteRoutesMap.get(getMapKeyForRoute(route));
    }

    private String getMapKeyForRoute(@NonNull FavoriteRouteDataObject route) {
        return route.getType() + route.getRouteId();
    }

    private void addRoutesToMap(@NonNull List<FavoriteRouteDataObject> routes) {
        for(int i = 0; i < routes.size(); i++) {
            FavoriteRouteDataObject route = routes.get(i);
            addRouteToMap(route, i);
        }
    }

    private void addRouteToMap(@NonNull FavoriteRouteDataObject route, int pos) {
        String key = getMapKeyForRoute(route);
        mFavoriteRoutesMap.put(key, pos);
    }

    private void onBindFavoriteBusRouteHolder(FavoriteRoutesViewHolder holder, int position) {
        FavoriteRouteDataObject favBusRoute = mFavoriteRoutesList.get(position);

        holder.nameTextView.setText(favBusRoute.getName());
        holder.destinationTextView.setText(favBusRoute.getDestination());
        holder.arrivalTimeTextView.setText(favBusRoute.getTimeTilArrival());
    }

    private void onBindFavoriteTrainRouteHolder(FavoriteRoutesViewHolder holder, int position) {
        FavoriteRouteDataObject favTrainRoute = mFavoriteRoutesList.get(position);

        holder.nameTextView.setText(favTrainRoute.getName());
        holder.destinationTextView.setText(favTrainRoute.getDestination());
        holder.arrivalTimeTextView.setText(favTrainRoute.getTimeTilArrival());
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

        @BindView(R.id.route_name) TextView nameTextView;
        @BindView(R.id.route_destination) TextView destinationTextView;
        @BindView(R.id.route_time_until_arrival) TextView arrivalTimeTextView;

        public FavoriteRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
