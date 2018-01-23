package com.andrewvora.apps.rideatlanta.routedetails;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.di.DataModule;
import com.andrewvora.apps.rideatlanta.routedetails.bus.BusRouteDetailsFragment;
import com.andrewvora.apps.rideatlanta.routedetails.bus.BusRouteDetailsPresenter;
import com.andrewvora.apps.rideatlanta.routedetails.train.TrainRouteDetailsFragment;
import com.andrewvora.apps.rideatlanta.routedetails.train.TrainRouteDetailsPresenter;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

/**
 * Created on 8/13/2017.
 * @author Andrew Vorakrajangthiti
 */
public class RouteDetailsActivity extends AppCompatActivity implements HasFragmentInjector {

	public static final String EXTRA_TYPE = "transitType";
	public static final String EXTRA_ROUTE_ID = "routeId";
	public static final String EXTRA_NAME = "nameOfTheRoute";
	public static final String EXTRA_DESTINATION = "destinationOfTheRoute";

	@BindView(R.id.toolbar) Toolbar toolbar;

	@Inject DispatchingAndroidInjector<Fragment> fragmentInjector;
	@Inject @Named(DataModule.BUS_SOURCE) BusesDataSource busRepo;
	@Inject @Named(DataModule.TRAIN_SOURCE) TrainsDataSource trainRepo;

	private String routeType;
	private String routeId;
	private String name;
	private String destination;

	public static Intent start(@NonNull Context context, @NonNull Bus bus) {
		final Bundle extras = new Bundle();
		extras.putString(EXTRA_TYPE, FavoriteRouteDataObject.TYPE_BUS);
		extras.putString(EXTRA_ROUTE_ID, bus.getRouteId());
		extras.putString(EXTRA_NAME, bus.getName());
		extras.putString(EXTRA_DESTINATION, bus.getDestination());

		final Intent intent = new Intent(context, RouteDetailsActivity.class);
		intent.putExtras(extras);
		return intent;
	}

	public static Intent start(@NonNull Context context, @NonNull Train train) {
		final Bundle extras = new Bundle();
		extras.putString(EXTRA_TYPE, FavoriteRouteDataObject.TYPE_TRAIN);
		extras.putString(EXTRA_ROUTE_ID, train.getRouteId());
		extras.putString(EXTRA_NAME, train.getName());
		extras.putString(EXTRA_DESTINATION, train.getDestination());

		final Intent intent = new Intent(context, RouteDetailsActivity.class);
		intent.putExtras(extras);
		return intent;
	}

	public static Intent start(@NonNull Context context, @NonNull FavoriteRoute route) {
		final Bundle extras = new Bundle();
		extras.putString(EXTRA_TYPE, route.getType());
		extras.putString(EXTRA_ROUTE_ID, route.getRouteId());
		extras.putString(EXTRA_NAME, route.getName());
		extras.putString(EXTRA_DESTINATION, route.getDestination());

		final Intent intent = new Intent(context, RouteDetailsActivity.class);
		intent.putExtras(extras);
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
			routeId = extras.getString(EXTRA_ROUTE_ID);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(EXTRA_DESTINATION, destination);
		outState.putString(EXTRA_ROUTE_ID, routeId);
		outState.putString(EXTRA_NAME, name);
		outState.putString(EXTRA_TYPE, routeType);
	}

	private void initViews() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			final String title = WordUtils.capitalizeWords(destination);
			getSupportActionBar().setTitle(title);
		}

		if (routeType.equals(FavoriteRouteDataObject.TYPE_BUS)) {
			final BusRouteDetailsFragment fragment = getBusFragment();
			fragment.setPresenter(new BusRouteDetailsPresenter(fragment, busRepo, routeId, destination));
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, fragment)
					.commit();
		} else {
			final TrainRouteDetailsFragment fragment = getTrainFragment();
			fragment.setPresenter(new TrainRouteDetailsPresenter(fragment, trainRepo, destination));
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, fragment)
					.commit();
		}
	}

	private BusRouteDetailsFragment getBusFragment () {
		return BusRouteDetailsFragment.newInstance(name, destination);
	}

	private TrainRouteDetailsFragment getTrainFragment() {
		return TrainRouteDetailsFragment.newInstance(name, destination);
	}

	@Override
	public AndroidInjector<Fragment> fragmentInjector() {
		return fragmentInjector;
	}
}
