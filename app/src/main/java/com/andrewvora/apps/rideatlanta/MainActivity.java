package com.andrewvora.apps.rideatlanta;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
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
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_icon) ImageButton mToolbarIconView;
    @BindView(R.id.toolbar_title) TextView mToolbarTitleView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.bottom_bar) BottomBar mBottomBar;

    private BaseView mActiveView;
    private BasePresenter mActivePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch(tabId) {
                    case R.id.tab_buses:
                        onBusesTabSelected();
                        break;

                    case R.id.tab_trains:
                        onTrainsTabSelected();
                        break;

                    case R.id.tab_home:
                        onHomeTabSelected();
                        break;

                    case R.id.tab_fav_routes:
                        onFavRoutesTabSelected();
                        break;

                    case R.id.tab_notifications:
                        onNotificationsTabSelected();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        mToolbarTitleView.setText(titleId);
    }

    private void onBusesTabSelected() {
        setTitle(R.string.title_tab_buses);
        setToolbarIcon(R.drawable.ic_bus_24dp);

        BusRoutesFragment fragment = (BusRoutesFragment) getFragmentManager()
                .findFragmentByTag(BusRoutesFragment.TAG);
        if(fragment == null) {
            fragment = BusRoutesFragment.newInstance();
        }

        BusRoutesContract.Presenter presenter = new BusRoutesPresenter(this, fragment);
        fragment.setPresenter(presenter);
        setActiveViewAndPresenter(fragment, presenter);

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

        TrainRoutesContract.Presenter presenter = new TrainRoutesPresenter(this, fragment);
        fragment.setPresenter(presenter);
        setActiveViewAndPresenter(fragment, presenter);

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

        HomeContract.Presenter presenter = new HomePresenter(this, fragment);
        fragment.setPresenter(presenter);
        setActiveViewAndPresenter(fragment, presenter);

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

        FavoriteRoutesContract.Presenter presenter = new FavoriteRoutesPresenter(this, fragment);
        fragment.setPresenter(presenter);
        setActiveViewAndPresenter(fragment, presenter);

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

    private void onNotificationsTabSelected() {
        setTitle(R.string.title_tab_notifications);
        setToolbarIcon(R.drawable.ic_notifications_24dp);

        NotificationsFragment fragment = (NotificationsFragment) getFragmentManager()
                .findFragmentByTag(NotificationsFragment.TAG);
        if(fragment == null) {
            fragment = NotificationsFragment.newInstance();
        }

        NotificationsContract.Presenter presenter = new NotificationsPresenter(this, fragment);
        fragment.setPresenter(presenter);
        setActiveViewAndPresenter(fragment, presenter);

        startFragment(R.id.fragment_container, fragment, NotificationsFragment.TAG, false);
    }

    private void setToolbarIcon(@DrawableRes int resId) {
        mToolbarIconView.setImageDrawable(ContextCompat.getDrawable(this, resId));
    }

    private void setActiveViewAndPresenter(BaseView view, BasePresenter presenter) {
        mActivePresenter = presenter;
        mActiveView = view;
    }

    private void startFragment(@IdRes int parentId, @NonNull Fragment fragment,
                               @Nullable String tag, boolean addToBackStack)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(parentId, fragment, tag);
        ft.commit();
    }
}
