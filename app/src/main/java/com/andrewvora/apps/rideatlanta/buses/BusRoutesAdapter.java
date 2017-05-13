package com.andrewvora.apps.rideatlanta.buses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Used with {@link BusRoutesFragment}.
 *
 * Created by faytx on 11/10/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesAdapter extends RecyclerView.Adapter<BusRoutesAdapter.BusRoutesViewHolder> {

    @NonNull private List<Bus> mBusList;
    @NonNull private Set<String> mFavoriteRouteIds;
    @Nullable private BusRoutesFragment.BusItemListener mListener;

    public BusRoutesAdapter(@Nullable List<Bus> buses,
                            @Nullable BusRoutesFragment.BusItemListener listener)
    {
        mBusList = buses == null ? new ArrayList<Bus>() : buses;
        mListener = listener;
        mFavoriteRouteIds = new HashSet<>();
    }

    @Override
    public BusRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_bus_route, parent, false);

        return new BusRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BusRoutesViewHolder holder, int position) {
        final Bus bus = mBusList.get(position);

        holder.vehicleIdTextView.setText(String.valueOf(bus.getRouteId()));

        holder.directionTextView.setText(bus.getDirection());

        String busDestination = WordUtils.capitalizeWords(bus.getTimePoint());
        holder.destinationTextView.setText(busDestination);

        // determine bus schedule adherence
        // non-zero is minutes late, zero is on-time
        final Context context = holder.itemView.getContext();
        String status = Bus.getFormattedAdherence(context, bus.getAdherence());

        holder.statusTextView.setText(status);

        if(mFavoriteRouteIds.contains(bus.getName())) {
            holder.favoriteButton.setSelected(true);
        }
        else {
            holder.favoriteButton.setSelected(bus.isFavorited());
        }

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onFavoriteBus(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBusList.size();
    }

    Bus getItemAtPosition(int position) {
        return mBusList.get(position);
    }

    public void setFavoriteRoutes(@NonNull Set<String> favRouteIds) {
        mFavoriteRouteIds = favRouteIds;
    }

    public void setBuses(@Nullable List<Bus> buses) {
        mBusList = buses == null ? new ArrayList<Bus>() : buses;
    }

    static class BusRoutesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bus_route_text_view) TextView vehicleIdTextView;
        @BindView(R.id.bus_destination_text_view) TextView destinationTextView;
        @BindView(R.id.bus_direction_text_view) TextView directionTextView;
        @BindView(R.id.bus_status_text_view) TextView statusTextView;
        @BindView(R.id.bus_favorite_button) ImageView favoriteButton;

        BusRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
