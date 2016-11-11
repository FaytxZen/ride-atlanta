package com.andrewvora.apps.rideatlanta.buses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.models.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 11/10/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusRoutesAdapter extends RecyclerView.Adapter<BusRoutesAdapter.BusRoutesViewHolder> {

    private List<Bus> mBusList;
    private BusRoutesFragment.BusItemListener mListener;

    public BusRoutesAdapter(@Nullable List<Bus> buses,
                            @Nullable BusRoutesFragment.BusItemListener listener)
    {
        mBusList = buses == null ? new ArrayList<Bus>() : buses;
        mListener = listener;
    }

    @Override
    public BusRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_bus_route, parent, false);

        return new BusRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusRoutesViewHolder holder, int position) {
        Bus bus = mBusList.get(position);

        holder.vehicleIdTextView.setText(String.valueOf(bus.getVehicleNumber()));
        holder.adherenceTextView.setText(bus.getAdherence().toString());
    }

    @Override
    public int getItemCount() {
        return mBusList.size();
    }

    public void setBuses(List<Bus> buses) {
        mBusList = buses;
    }

    static class BusRoutesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bus_vehicle_id_text_view) TextView vehicleIdTextView;
        @BindView(R.id.bus_adherence_text_view) TextView adherenceTextView;

        BusRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
