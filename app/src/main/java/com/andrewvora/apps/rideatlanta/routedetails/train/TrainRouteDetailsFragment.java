package com.andrewvora.apps.rideatlanta.routedetails.train;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 8/13/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class TrainRouteDetailsFragment extends Fragment implements TrainRouteDetailsContract.View {

	public static final String EXTRA_TRAIN_LINE = "trainLineName";
	public static final String EXTRA_TRAIN_DESTINATION = "trainDestination";

	@BindView(R.id.train_details_recycler_view)
	RecyclerView trainRecyclerView;

	private TrainRouteDetailsContract.Presenter presenter;
	private TrainRouteDetailsAdapter recyclerViewAdapter;

	public static TrainRouteDetailsFragment newInstance(@NonNull String line, @NonNull String destination) {
		final Bundle extras = new Bundle();
		extras.putString(EXTRA_TRAIN_LINE, line);
		extras.putString(EXTRA_TRAIN_DESTINATION, destination);

		final TrainRouteDetailsFragment fragment = new TrainRouteDetailsFragment();
		fragment.setArguments(extras);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recyclerViewAdapter = new TrainRouteDetailsAdapter(new ArrayList<Train>());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_train_route_details, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);

		trainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		trainRecyclerView.setAdapter(recyclerViewAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (presenter != null) {
			presenter.start();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (presenter != null) {
			presenter.stop();
		}
	}

	@Override
	public void setPresenter(TrainRouteDetailsContract.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showTrains(@NonNull List<Train> trains) {
		recyclerViewAdapter.setTrains(trains);
		recyclerViewAdapter.notifyDataSetChanged();
	}
}
