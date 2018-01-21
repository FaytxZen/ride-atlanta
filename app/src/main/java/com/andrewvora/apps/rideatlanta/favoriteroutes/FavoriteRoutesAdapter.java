package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles and displays {@link FavoriteRouteDataObject} for {@link FavoriteRoutesFragment}.
 *
 * Created by faytx on 12/26/2016.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesAdapter extends
        RecyclerView.Adapter<FavoriteRoutesAdapter.FavoriteRoutesViewHolder>
{
    public static final int NEW_INDEX = -1;

    @NonNull private List<FavoriteRouteDataObject> mFavoriteRoutesList;
    @NonNull private FavoriteRoutesFragment.AdapterCallback mListener;

    FavoriteRoutesAdapter(@NonNull List<FavoriteRouteDataObject> favoriteRoutes,
                          @NonNull FavoriteRoutesFragment.AdapterCallback listener)
    {
        mFavoriteRoutesList = favoriteRoutes;
        mListener = listener;
    }

    @Override
    public FavoriteRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_favorite_route, parent, false);

        return new FavoriteRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteRoutesViewHolder holder, int position) {
        final FavoriteRouteDataObject favoriteRoute = mFavoriteRoutesList.get(position);

        String destination = WordUtils.capitalizeWords(favoriteRoute.getDestination());
        holder.destinationTextView.setText(destination);

        holder.nameTextView.setText(favoriteRoute.getName());

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

    public void setFavoriteRoutes(@NonNull List<FavoriteRouteDataObject> routes) {
        mFavoriteRoutesList = routes;
    }

    public List<FavoriteRouteDataObject> getFavoriteRoutes() {
        return mFavoriteRoutesList;
    }

    public void setFavoriteRoute(int position, @NonNull FavoriteRouteDataObject route) {
        mFavoriteRoutesList.set(position, route);
    }

    public int getPosition(@NonNull FavoriteRouteDataObject route) {
        Iterator<FavoriteRouteDataObject> it = mFavoriteRoutesList.iterator();
        int index = 0, result = NEW_INDEX;

        while(it.hasNext()) {
            FavoriteRouteDataObject curRoute = it.next();

            if(curRoute.getFavoriteRouteKey().equals(route.getFavoriteRouteKey())) {
                result = index;
                break;
            }

            index++;
        }

        return result;
    }

    private void onBindFavoriteBusRouteHolder(FavoriteRoutesViewHolder holder, int position) {
        final FavoriteRouteDataObject favBusRoute = mFavoriteRoutesList.get(position);
        final Context context = holder.itemView.getContext();

        final int adherence = Bus.parseAdherence(favBusRoute.getTimeTilArrival());
        final String arrivalTime = Bus.getFormattedAdherence(context, adherence);
        holder.arrivalTimeTextView.setText(arrivalTime);

        holder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bus_white_24dp, 0, 0, 0);
    }

    private void onBindFavoriteTrainRouteHolder(FavoriteRoutesViewHolder holder, int position) {
        final FavoriteRouteDataObject favTrainRoute = mFavoriteRoutesList.get(position);
        final Context context = holder.itemView.getContext();

        final String timeTilArrival = favTrainRoute.getTimeTilArrival();
        holder.arrivalTimeTextView.setText(Train.getFormattedTimeTilArrival(context, timeTilArrival));

        holder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_train_white_24dp, 0, 0, 0);

        final int color = ContextCompat.getColor(context, Train.getColorRes(favTrainRoute.getName()));
        holder.nameTextView.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return mFavoriteRoutesList.size();
    }

    private View.OnClickListener getUnfavoriteClickListener(final FavoriteRoutesViewHolder holder) {
        return view -> {
            final int position = holder.getAdapterPosition();

            if(position < getItemCount()) {
					FavoriteRouteDataObject route = mFavoriteRoutesList.get(position);

					// remove from list
					mFavoriteRoutesList.remove(position);

					// alert listener
					mListener.onUnfavorited(position, route);
            }
        };
    }

    static class FavoriteRoutesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.route_name) TextView nameTextView;
        @BindView(R.id.route_destination) TextView destinationTextView;
        @BindView(R.id.route_time_until_arrival) TextView arrivalTimeTextView;
        @BindView(R.id.favorite_button) View favoriteButton;

        FavoriteRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
