package com.andrewvora.apps.rideatlanta.buses;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.models.Bus;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusRoutesFragment extends Fragment implements BusRoutesContract.View {

    public static final String TAG = BusRoutesFragment.class.getSimpleName();

    private BusRoutesContract.Presenter mPresenter;

    public static BusRoutesFragment newInstance() {
        return new BusRoutesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_routes, container, false);
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
    public void onBusRoutesLoaded(List<Bus> routesList) {

    }

    @Override
    public void setPresenter(BusRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
