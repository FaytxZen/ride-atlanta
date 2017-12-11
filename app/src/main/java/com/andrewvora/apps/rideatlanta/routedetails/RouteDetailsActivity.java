package com.andrewvora.apps.rideatlanta.routedetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.routedetails.bus.BusRouteDetailsFragment;
import com.andrewvora.apps.rideatlanta.routedetails.train.TrainRouteDetailsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Created on 8/13/2017.
 * @author Andrew Vorakrajangthiti
 */
public class RouteDetailsActivity extends AppCompatActivity {

	public static final String EXTRA_TYPE = "transitType";
	public static final String EXTRA_NAME = "nameOfTheRoute";
	public static final String EXTRA_DESTINATION = "destinationOfTheRoute";

	@BindView(R.id.toolbar) Toolbar toolbar;

	private String routeType;
	private String name;
	private String destination;

	public static Intent start(@NonNull Bus bus) {
		final Intent intent = new Intent();

		return intent;
	}

	public static Intent start(@NonNull Train train) {
		final Intent intent = new Intent();

		return intent;
	}

	public static Intent start(@NonNull FavoriteRoute route) {
		final Intent intent = new Intent();

		return intent;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		AndroidInjection.inject(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_details);
		ButterKnife.bind(this);

		final Bundle extras = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		initExtras(extras);
		initViews();
	}

	private void initExtras(Bundle extras) {
		if (extras != null) {
			routeType = extras.getString(EXTRA_TYPE);
			name = extras.getString(EXTRA_NAME);
			destination = extras.getString(EXTRA_DESTINATION);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initViews() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		if (routeType.equals(FavoriteRouteDataObject.TYPE_BUS)) {
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, getBusFragment())
					.commit();
		} else {
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, getTrainFragment())
					.commit();
		}
	}

	private BusRouteDetailsFragment getBusFragment () {
		return BusRouteDetailsFragment.newInstance(name, destination);
	}

	private TrainRouteDetailsFragment getTrainFragment() {
		return TrainRouteDetailsFragment.newInstance(name, destination);
	}
}
