package com.andrewvora.apps.rideatlanta.buses;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.models.Bus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusRoutesFragment extends Fragment implements BusRoutesContract.View {

    public static final String TAG = BusRoutesFragment.class.getSimpleName();

    @BindView(R.id.buses_list) RecyclerView mBusesRecyclerView;

    private BusRoutesContract.Presenter mPresenter;
    private BusRoutesAdapter mBusAdapter;
    private BusItemListener mBusItemListener = new BusItemListener() {
        @Override
        public void onItemClicked(Bus bus) {

        }
    };

    public static BusRoutesFragment newInstance() {
        return new BusRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBusAdapter = new BusRoutesAdapter(null, mBusItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_routes, container, false);
        ButterKnife.bind(this, view);

        mBusesRecyclerView.setAdapter(mBusAdapter);
        mBusesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        mBusAdapter.setBuses(routesList);
        mBusAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(BusRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public interface BusItemListener {
        void onItemClicked(Bus bus);
    }
}
