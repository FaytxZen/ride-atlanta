package com.andrewvora.apps.rideatlanta.home;

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
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class HomeFragment extends Fragment implements HomeContract.View {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.home_recycler_view) RecyclerView mHomeRecyclerView;

    private HomeContract.Presenter mPresenter;
    private HomeAdapter mHomeAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHomeAdapter = new HomeAdapter(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHomeRecyclerView.setAdapter(mHomeAdapter);

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
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void displayAlerts(@NonNull List<AlertItemModel> alertItems) {
        for(AlertItemModel alertItem : alertItems) {
            int position = mHomeAdapter.addListItem(alertItem);
            notifyItemInserted(position);
        }
    }

    @Override
    public void displayInfoItems(@NonNull List<InfoItemModel> infoItems) {
        for(InfoItemModel infoItem : infoItems) {
            int position = mHomeAdapter.addListItem(infoItem);
            notifyItemInserted(position);
        }
    }

    @Override
    public void displayRouteItems(@NonNull List<RouteItemModel> routeItems) {
        for(RouteItemModel routeItem : routeItems) {
            int position = mHomeAdapter.addListItem(routeItem);
            notifyItemInserted(position);
        }
    }

    private void notifyItemInserted(int position) {
        if(position != HomeAdapter.UNSUCCESSFUL_INSERT) {
            mHomeAdapter.notifyItemInserted(position);
        }
    }
}
