package com.andrewvora.apps.rideatlanta.routedetails.bus;

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
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 8/13/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class BusRouteDetailsFragment extends Fragment implements BusRouteDetailsContract.View {

	public static final String EXTRA_BUS_ID = "BUS_ID";
	public static final String EXTRA_BUS_DESTINATION = "BUS_DESTINATION";

	@BindView(R.id.bus_details_recycler_view)
	RecyclerView busRecyclerView;

	private BusRouteDetailsContract.Presenter presenter;
	private BusRouteDetailsAdapter recyclerViewAdapter;

	public static BusRouteDetailsFragment newInstance(@NonNull String id, @NonNull String destination) {
		final BusRouteDetailsFragment fragment = new BusRouteDetailsFragment();
		final Bundle arguments = new Bundle();
		arguments.putString(EXTRA_BUS_ID, id);
		arguments.putString(EXTRA_BUS_DESTINATION, destination);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recyclerViewAdapter = new BusRouteDetailsAdapter(new ArrayList<>());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_bus_route_details, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);

		busRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		busRecyclerView.setAdapter(recyclerViewAdapter);
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
	public void setPresenter(BusRouteDetailsContract.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showBusRoutes(@NonNull List<Bus> buses) {
		recyclerViewAdapter.setData(buses);
		recyclerViewAdapter.notifyDataSetChanged();
	}
}
