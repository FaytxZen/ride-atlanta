package com.andrewvora.apps.rideatlanta.routedetails.bus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 12/10/2017.
 * @author Andrew Vorakrajangthiti
 */
public class BusRouteDetailsAdapter extends RecyclerView.Adapter<BusRouteDetailsAdapter.BusViewHolder> {

	@NonNull
	private List<Bus> buses;

	BusRouteDetailsAdapter(@NonNull List<Bus> buses) {
		this.buses = buses;
	}

	@Override
	public BusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_bus_route_detail, parent, false);
		return new BusViewHolder(view);
	}

	@Override
	public void onBindViewHolder(BusViewHolder holder, int position) {
		final Bus bus = buses.get(position);

		holder.busNameTextView.setText(String.valueOf(bus.getRouteId()));
		holder.directionTextView.setText(bus.getDirection());
		final String busDestination = WordUtils.capitalizeWords(bus.getTimePoint());
		holder.destinationTextView.setText(busDestination);

		final Context context = holder.itemView.getContext();
		final String status = Bus.getFormattedAdherence(context, bus.getAdherence());
		holder.timeTilArrivalTextView.setText(status);

		final String vehicleId = context.getString(R.string.bus_vehicle_id_template, bus.getVehicleNumber());
		holder.vehicleIdTextView.setText(vehicleId);
	}

	@Override
	public int getItemCount() {
		return buses.size();
	}

	public void setData(@NonNull List<Bus> buses) {
		this.buses = buses;
	}

	static class BusViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.route_name) TextView busNameTextView;
		@BindView(R.id.route_destination) TextView destinationTextView;
		@BindView(R.id.route_direction) TextView directionTextView;
		@BindView(R.id.route_time_until_arrival) TextView timeTilArrivalTextView;
		@BindView(R.id.route_vehicle_id) TextView vehicleIdTextView;

		BusViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
