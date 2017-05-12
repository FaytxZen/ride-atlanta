package com.andrewvora.apps.rideatlanta.notifications;

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
import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsFragment extends Fragment implements NotificationsContract.View {

    public static final String TAG = NotificationsFragment.class.getSimpleName();

    @BindView(R.id.notifications_list) RecyclerView mNotificationsRecyclerView;
    @BindView(R.id.loading_notifications_view) ProgressBar mLoadingView;
    @BindView(R.id.notifications_refresh_layout) SwipeRefreshLayout mNotificationsRefreshLayout;

    private NotificationsContract.Presenter mPresenter;
    private NotificationsAdapter mNotificationsAdapter;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Notification> placeholderList = new ArrayList<>();
        mNotificationsAdapter = new NotificationsAdapter(placeholderList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);

        mNotificationsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mPresenter != null) {
                    mPresenter.loadNotifications();
                }
            }
        });

        mNotificationsRecyclerView.setAdapter(mNotificationsAdapter);
        mNotificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNotificationsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

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
    public void setPresenter(NotificationsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onNotificationsLoaded(List<Notification> notificationList) {
        mLoadingView.setVisibility(View.GONE);
        mNotificationsRefreshLayout.setRefreshing(false);

        mNotificationsAdapter.setNotifications(notificationList);
        mNotificationsAdapter.notifyDataSetChanged();
    }
}
