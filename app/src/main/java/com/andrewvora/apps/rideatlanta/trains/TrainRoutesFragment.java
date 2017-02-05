package com.andrewvora.apps.rideatlanta.trains;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesFragment extends Fragment implements TrainRoutesContract.View {

    public static final String TAG = TrainRoutesFragment.class.getSimpleName();

    @BindView(R.id.trains_list) RecyclerView mTrainsRecyclerView;
    @BindView(R.id.loading_train_routes_view) ProgressBar mProgressBar;
    @BindView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.no_trains_running_view) View mEmptyStateView;

    private TrainRoutesContract.Presenter mPresenter;
    private TrainRoutesAdapter mTrainAdapter;
    private TrainItemListener mTrainItemListener = new TrainItemListener() {
        @Override
        public void onItemClicked(int position) {

        }

        @Override
        public void onFavoriteItem(int position) {
            mPresenter.favoriteRoute(mTrainAdapter.getTrain(position));
            mTrainAdapter.notifyItemChanged(position);
        }
    };

    public static TrainRoutesFragment newInstance() {
        return new TrainRoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrainAdapter = new TrainRoutesAdapter(null, mTrainItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_routes, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshTrainRoutes();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mTrainsRecyclerView.setAdapter(mTrainAdapter);
        mTrainsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(mPresenter != null) {
            mPresenter.onRestoreState(savedInstanceState);
        }

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mPresenter != null) {
            mPresenter.onSaveState(outState);
        }
    }

    @Override
    public void setPresenter(TrainRoutesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onTrainRoutesLoaded(List<Train> trainList) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);

        mTrainAdapter.setTrains(trainList);
        mTrainAdapter.notifyDataSetChanged();

        if(trainList.isEmpty()) {
            mEmptyStateView.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyStateView.setVisibility(View.GONE);
        }
    }

    public interface TrainItemListener {
        void onItemClicked(int position);
        void onFavoriteItem(int position);
    }
}
