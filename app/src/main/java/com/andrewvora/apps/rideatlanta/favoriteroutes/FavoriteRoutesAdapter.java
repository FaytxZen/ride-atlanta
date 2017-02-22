package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.repos.BusesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.FavoriteRoutesRepo;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;

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
    public static final int NEW_INDEX = -1;

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

        holder.nameTextView.setText(favoriteRoute.getName());
        holder.destinationTextView.setText(favoriteRoute.getDestination());
        holder.arrivalTimeTextView.setText(favoriteRoute.getTimeTilArrival());
        holder.favoriteButton.setSelected(true);
        holder.favoriteButton.setOnClickListener(getUnfavoriteClickListener(holder));

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

    public void setFavoriteRoute(FavoriteRouteDataObject route) {
        String key = getMapKeyForRoute(route);

        if(mFavoriteRoutesMap.containsKey(key)) {
            int position = mFavoriteRoutesMap.get(key);
            mFavoriteRoutesList.set(position, route);
        }
    }

    public int getPosition(@NonNull FavoriteRouteDataObject route) {
        String key = getMapKeyForRoute(route);

        return mFavoriteRoutesMap.containsKey(key) ?
                mFavoriteRoutesMap.get(key) :
                NEW_INDEX;
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
    }

    private void onBindFavoriteTrainRouteHolder(FavoriteRoutesViewHolder holder, int position) {
        FavoriteRouteDataObject favTrainRoute = mFavoriteRoutesList.get(position);
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

    private View.OnClickListener getUnfavoriteClickListener(final FavoriteRoutesViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                FavoriteRouteDataObject route = mFavoriteRoutesList
                        .get(position);

                // remove from list
                mFavoriteRoutesList.remove(position);

                // remove from map
                mFavoriteRoutesMap.remove(getMapKeyForRoute(route));

                // remove from database
                updateRouteInDatabase(view.getContext(), route);

                // update the UI
                notifyItemRemoved(position);
            }
        };
    }

    private void updateRouteInDatabase(@NonNull Context context,
                                       @NonNull FavoriteRouteDataObject route)
    {
        FavoriteRoutesRepo.getInstance(context).deleteRoute(route);

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
        Train trainArg = new Train();
        trainArg.setTrainId(Long.parseLong(route.getRouteId()));

        TrainsRepo.getInstance(context)
            .getTrain(trainArg, new TrainsDataSource.GetTrainRouteCallback() {
            @Override
            public void onFinished(Train train) {
                train.setFavorited(false);
                TrainsRepo.getInstance(context).saveTrain(train);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void unfavoriteInBusTable(@NonNull final Context context,
                                      @NonNull FavoriteRouteDataObject route)
    {
        Bus busArg = new Bus();
        busArg.setRouteId(route.getRouteId());

        BusesRepo.getInstance(context).getBus(busArg, new BusesDataSource.GetBusCallback() {
            @Override
            public void onFinished(Bus bus) {
                bus.setFavorited(false);
                BusesRepo.getInstance(context).saveBus(bus);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    public static class FavoriteRoutesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.route_name) TextView nameTextView;
        @BindView(R.id.route_destination) TextView destinationTextView;
        @BindView(R.id.route_time_until_arrival) TextView arrivalTimeTextView;
        @BindView(R.id.favorite_button) View favoriteButton;

        public FavoriteRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
