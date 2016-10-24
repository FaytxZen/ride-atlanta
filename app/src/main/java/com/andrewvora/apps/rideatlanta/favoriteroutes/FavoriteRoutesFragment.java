package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;

import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesFragment extends Fragment implements FavoriteRoutesContract.View {

    public static final String TAG = FavoriteRoutesFragment.class.getSimpleName();

    private FavoriteRoutesContract.Presenter mPresenter;

    public static FavoriteRoutesFragment newInstance() {
        return new FavoriteRoutesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(FavoriteRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
