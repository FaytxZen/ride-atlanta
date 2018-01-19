package com.andrewvora.apps.rideatlanta.routedetails.train;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 12/10/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class TrainRouteDetailsAdapter extends RecyclerView.Adapter<TrainRouteDetailsAdapter.RouteDetailsViewHolder> {

	@NonNull private List<Train> trains;

	public TrainRouteDetailsAdapter(@NonNull List<Train> trains) {
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

		holder.routeNameTextView.setText(train.getName());
		holder.directionTextView.setText(train.getDirection());
		holder.adherenceTextView.setText(train.getTimeTilArrival());
	}

	@Override
	public int getItemCount() {
		return trains.size();
	}

	static class RouteDetailsViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.route_name) TextView routeNameTextView;
		@BindView(R.id.direction_text_view) TextView directionTextView;
		@BindView(R.id.time_text_view) TextView adherenceTextView;

		RouteDetailsViewHolder(@NonNull View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
