package com.andrewvora.apps.rideatlanta.home;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.routedetails.RouteDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class HomeFragment extends Fragment implements HomeContract.View, HomeAdapter.Listener {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.home_recycler_view) RecyclerView homeRecyclerView;
    @BindView(R.id.loading_indicator) ProgressBar progressBar;

    private HomeContract.Presenter presenter;
    private HomeAdapter homeAdapter;
    private Unbinder unbinder;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeAdapter = new HomeAdapter(new ArrayList<>(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeRecyclerView.setAdapter(homeAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(presenter != null) {
            presenter.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(presenter != null) {
            presenter.stop();
        }
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

	@Override
	public void displayItems(@NonNull List<HomeItemModel> models) {
		homeAdapter.setItems(models);
		homeAdapter.notifyDataSetChanged();
	}

    @Override
    public void updateItems(@NonNull List<HomeItemModel> homeItems) {
        for(HomeItemModel homeItem : homeItems) {
            int position = homeAdapter.addListItem(homeItem);
            updateItemInAdapter(position);
        }
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }

    private void updateItemInAdapter(int position) {
    	if (homeRecyclerView == null) {
    		return;
	    }

        if(position >= 0) {
            homeAdapter.notifyItemInserted(position);
            homeRecyclerView.smoothScrollToPosition(0);
        }
        else {
            homeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void openRouteInfo(FavoriteRoute route) {
        final Intent detailsIntent = RouteDetailsActivity.start(getActivity(), route);
        startActivityForResult(detailsIntent, 0);
    }

	@Override
	public void showLoadingIndicator() {
    	if (progressBar != null) {
		    progressBar.setIndeterminate(true);
		    progressBar.setVisibility(View.VISIBLE);
	    }
	}

	@Override
	public void hideLoadingIndicator() {
    	if (progressBar != null) {
		    progressBar.setIndeterminate(false);
		    progressBar.setVisibility(View.GONE);
	    }
	}
}
