package com.andrewvora.apps.rideatlanta.routedetails.train;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 12/10/2017.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRouteDetailsAdapter extends RecyclerView.Adapter<TrainRouteDetailsAdapter.RouteDetailsViewHolder> {

	@NonNull private List<Train> trains;

	TrainRouteDetailsAdapter(@NonNull List<Train> trains) {
		this.trains = trains;
	}

	public void setTrains(@NonNull List<Train> trains) {
		this.trains = trains;
	}

	@Override
	public RouteDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_train_route_detail, parent, false);
		return new RouteDetailsViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RouteDetailsViewHolder holder, int position) {
		final Train train = trains.get(position);

		final int colorResId = Train.getColorRes(train.getLine());
		final int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);
		holder.routeNameTextView.setBackgroundColor(color);
		holder.routeNameTextView.setText(train.getLine());

		final String destinationText = WordUtils.capitalizeWords(train.getStation());
		holder.destinationTextView.setText(destinationText);

		final int directionResId = WordUtils.getFullDirectionString(train.getDirection());
		holder.directionTextView.setText(directionResId);

		holder.timeTilTextView.setText(train.getWaitingTime());
		holder.trainIdTextView.setText(String.valueOf(train.getTrainId()));

		final String arrivalTime = train.getDisplayableEventTime();
		holder.arrivalTimeTextView.setText(arrivalTime);
	}

	@Override
	public int getItemCount() {
		return trains.size();
	}

	static class RouteDetailsViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.route_name) TextView routeNameTextView;
		@BindView(R.id.route_destination) TextView destinationTextView;
		@BindView(R.id.direction_text_view) TextView directionTextView;
		@BindView(R.id.time_til_text_view) TextView timeTilTextView;
		@BindView(R.id.arrival_time_text_view) TextView arrivalTimeTextView;
		@BindView(R.id.train_id) TextView trainIdTextView;

		RouteDetailsViewHolder(@NonNull View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
