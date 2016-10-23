package com.andrewvora.apps.rideatlanta;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_icon) ImageButton mToolbarIconView;
    @BindView(R.id.toolbar_title) TextView mToolbarTitleView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.bottom_bar) BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void setTitle(@StringRes int titleId) {
        mToolbarTitleView.setText(titleId);
    }

    private void onBusesTabSelected() {
        setTitle(R.string.title_tab_buses);
        setToolbarIcon(R.drawable.ic_bus_24dp);
    }

    private void onTrainsTabSelected() {
        setTitle(R.string.title_tab_trains);
        setToolbarIcon(R.drawable.ic_train_24dp);
    }

    private void onHomeTabSelected() {
        setTitle(R.string.title_tab_home);
        setToolbarIcon(R.drawable.ic_home_24dp);
    }

    private void onFavRoutesTabSelected() {
        setTitle(R.string.title_tab_fav_routes);
        setToolbarIcon(R.drawable.ic_favorite_24dp);
    }

    private void onNotificationsTabSelected() {
        setTitle(R.string.title_tab_notifications);
        setToolbarIcon(R.drawable.ic_notifications_24dp);
    }

    private void setToolbarIcon(@DrawableRes int resId) {
        mToolbarIconView.setImageDrawable(ContextCompat.getDrawable(this, resId));
    }

    private void startFragment(@IdRes int parentId, @NonNull Fragment fragment,
                               @Nullable String tag)
    {

    }
}
