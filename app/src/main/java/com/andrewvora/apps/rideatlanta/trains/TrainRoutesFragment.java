package com.andrewvora.apps.rideatlanta.trains;

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

public class TrainRoutesFragment extends Fragment implements TrainRoutesContract.View {

    public static final String TAG = TrainRoutesFragment.class.getSimpleName();

    private TrainRoutesContract.Presenter mPresenter;

    public static TrainRoutesFragment newInstance() {
        return new TrainRoutesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_routes, container, false);
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
    public void setPresenter(TrainRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
