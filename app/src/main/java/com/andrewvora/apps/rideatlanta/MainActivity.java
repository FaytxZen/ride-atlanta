package com.andrewvora.apps.rideatlanta;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.breezebalance.BreezeBalanceActivity;
import com.andrewvora.apps.rideatlanta.buses.BusRoutesContract;
import com.andrewvora.apps.rideatlanta.buses.BusRoutesFragment;
import com.andrewvora.apps.rideatlanta.buses.BusRoutesPresenter;
import com.andrewvora.apps.rideatlanta.data.FavoritesHelper;
import com.andrewvora.apps.rideatlanta.data.RoutePollingHelper;
import com.andrewvora.apps.rideatlanta.data.SharedPrefsManager;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.di.DataModule;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesContract;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesFragment;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesPresenter;
import com.andrewvora.apps.rideatlanta.home.HomeContract;
import com.andrewvora.apps.rideatlanta.home.HomeFragment;
import com.andrewvora.apps.rideatlanta.home.HomePresenter;
import com.andrewvora.apps.rideatlanta.notifications.NotificationsContract;
import com.andrewvora.apps.rideatlanta.notifications.NotificationsFragment;
import com.andrewvora.apps.rideatlanta.notifications.NotificationsPresenter;
import com.andrewvora.apps.rideatlanta.seeandsay.SeeAndSayActivity;
import com.andrewvora.apps.rideatlanta.trains.TrainRoutesContract;
import com.andrewvora.apps.rideatlanta.trains.TrainRoutesFragment;
import com.andrewvora.apps.rideatlanta.trains.TrainRoutesPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasFragmentInjector {

    @BindView(R.id.toolbar_icon) ImageButton toolbarIconView;
    @BindView(R.id.toolbar_title) TextView toolbarTitleView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bottom_bar) BottomNavigationView bottomBar;

	@Inject DispatchingAndroidInjector<Fragment> fragmentInjector;

	@Inject @Named(DataModule.BUS_SOURCE)
	BusesDataSource busRepo;
	@Inject @Named(DataModule.TRAIN_SOURCE)
	TrainsDataSource trainRepo;
	@Inject @Named(DataModule.FAVS_SOURCE)
	FavoriteRoutesDataSource favsRepo;
    @Inject @Named(DataModule.NOTIFICATION_SOURCE)
    NotificationsDataSource notificationRepo;

	@Inject RoutePollingHelper pollingHelper;
	@Inject FavoritesHelper favoritesHelper;

	private MenuItem sortMenuItem;
    private SharedPrefsManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		AndroidInjection.inject(this);
		super.onCreate(savedInstanceState);

        setTitle("");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        prefManager = new SharedPrefsManager(getApplication());

        final int tabId = prefManager.getSelectedTab() == 0 ? R.id.tab_home : prefManager.getSelectedTab();
        applySelectedTab(tabId);

        bottomBar.setSelectedItemId(prefManager.getSelectedTab());
        bottomBar.setOnNavigationItemSelectedListener(item -> {
            applySelectedTab(item.getItemId());
            return true;
        });
    }

    private void applySelectedTab(@IdRes int tabId) {
        switch(tabId) {
            case R.id.tab_home:
                onHomeTabSelected();
                break;

            case R.id.tab_buses:
                onBusesTabSelected();
                break;

            case R.id.tab_trains:
                onTrainsTabSelected();
                break;

            case R.id.tab_fav_routes:
                onFavRoutesTabSelected();
                break;

            case R.id.tab_notifications:
                onNotificationsTabSelected();
                break;
        }

        prefManager.setSelectedTab(tabId);
        updateMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        sortMenuItem = menu.findItem(R.id.menu_sort);
        updateMenu();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_breeze_balance:
                goToCheckBreezeBalancePage();
                break;

            case R.id.menu_report:
                goToSeeSayReportingPage();
                break;

            case R.id.menu_feedback:
                sendFeedback();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        toolbarTitleView.setText(titleId);
    }

    private void onBusesTabSelected() {
        setTitle(R.string.title_tab_buses);
        setToolbarIcon(R.drawable.ic_bus_24dp);

        BusRoutesFragment fragment = (BusRoutesFragment) getFragmentManager()
                .findFragmentByTag(BusRoutesFragment.TAG);
        if(fragment == null) {
            fragment = BusRoutesFragment.newInstance();
        }

        BusRoutesContract.Presenter presenter = new BusRoutesPresenter(
                fragment,
                busRepo,
                favsRepo,
				pollingHelper,
		        favoritesHelper);
        fragment.setPresenter(presenter);

        startFragment(R.id.fragment_container, fragment, BusRoutesFragment.TAG, false);
    }

    private void onTrainsTabSelected() {
        setTitle(R.string.title_tab_trains);
        setToolbarIcon(R.drawable.ic_train_24dp);

        TrainRoutesFragment fragment = (TrainRoutesFragment) getFragmentManager()
                .findFragmentByTag(TrainRoutesFragment.TAG);

        if(fragment == null) {
            fragment = TrainRoutesFragment.newInstance();
        }

        TrainRoutesContract.Presenter presenter = new TrainRoutesPresenter(
                fragment,
                trainRepo,
                favsRepo,
				pollingHelper,
		        favoritesHelper);

        fragment.setPresenter(presenter);

        startFragment(R.id.fragment_container, fragment, TrainRoutesFragment.TAG, false);

    }

    private void onHomeTabSelected() {
        setTitle(R.string.title_tab_home);
        setToolbarIcon(R.drawable.ic_home_24dp);

        HomeFragment fragment = (HomeFragment) getFragmentManager()
                .findFragmentByTag(HomeFragment.TAG);

        if(fragment == null) {
            fragment = HomeFragment.newInstance();
        }

        HomeContract.Presenter presenter = new HomePresenter(
                fragment,
                favsRepo,
                notificationRepo,
                busRepo,
                trainRepo,
				pollingHelper,
		        favoritesHelper);
        fragment.setPresenter(presenter);

        startFragment(R.id.fragment_container, fragment, HomeFragment.TAG, false);
    }

    private void onFavRoutesTabSelected() {
        setTitle(R.string.title_tab_fav_routes);
        setToolbarIcon(R.drawable.ic_favorite_filled_24dp);

        FavoriteRoutesFragment fragment = (FavoriteRoutesFragment) getFragmentManager()
                .findFragmentByTag(FavoriteRoutesFragment.TAG);
        if(fragment == null) {
            fragment = FavoriteRoutesFragment.newInstance();
        }

        FavoriteRoutesContract.Presenter presenter = new FavoriteRoutesPresenter(
                fragment,
                favsRepo,
                busRepo,
                trainRepo,
				pollingHelper,
		        favoritesHelper);
        fragment.setPresenter(presenter);

        startFragment(R.id.fragment_container, fragment, FavoriteRoutesFragment.TAG, false);
    }

    private void goToCheckBreezeBalancePage() {
        Intent openBreezeBalanceSiteIntent = new Intent(this, BreezeBalanceActivity.class);
        startActivity(openBreezeBalanceSiteIntent);
    }

    private void goToSeeSayReportingPage() {
        Intent reportIncidentIntent = new Intent(this, SeeAndSayActivity.class);
        startActivity(reportIncidentIntent);
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.app_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_subject_feedback));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void onNotificationsTabSelected() {
        setTitle(R.string.title_tab_notifications);
        setToolbarIcon(R.drawable.ic_notifications_24dp);

        NotificationsFragment fragment = (NotificationsFragment) getFragmentManager()
                .findFragmentByTag(NotificationsFragment.TAG);
        if(fragment == null) {
            fragment = NotificationsFragment.newInstance();
        }

        NotificationsContract.Presenter presenter = new NotificationsPresenter(
                fragment,
                notificationRepo);
        fragment.setPresenter(presenter);

        startFragment(R.id.fragment_container, fragment, NotificationsFragment.TAG, false);
    }

    private void setToolbarIcon(@DrawableRes int resId) {
        toolbarIconView.setImageDrawable(ContextCompat.getDrawable(this, resId));
    }

    private void startFragment(@IdRes int parentId,
                               @NonNull Fragment fragment,
                               @Nullable String tag,
                               boolean addToBackStack)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(parentId, fragment, tag);

        ft.commit();
    }

    private void updateMenu() {
        if (sortMenuItem != null && bottomBar != null) {
			// TODO: enable this logic after sort is completed
            //final boolean showSortOption =
            //        bottomBar.getSelectedItemId() == R.id.tab_trains ||
            //        bottomBar.getSelectedItemId() == R.id.tab_buses;
            //sortMenuItem.setVisible(showSortOption);
        }
    }

	@Override
	public AndroidInjector<Fragment> fragmentInjector() {
		return fragmentInjector;
	}
}
