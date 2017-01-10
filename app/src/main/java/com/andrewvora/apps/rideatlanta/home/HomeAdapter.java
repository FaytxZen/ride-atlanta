package com.andrewvora.apps.rideatlanta.home;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.HomeItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 1/7/2017.
 * @author Andrew Vorakrajangthiti
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull private List<HomeItemModel> mListItems;

    public HomeAdapter(@Nullable List<HomeItemModel> listItemModels) {
        mListItems = listItemModels == null ? new ArrayList<HomeItemModel>() : listItemModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HomeItemModel.VIEW_TYPE_ALERT_ITEM) {
            View alertView = getLayoutFor(parent, R.layout.element_home_alert_item);
            return new AlertViewHolder(alertView);
        }
        else if(viewType == HomeItemModel.VIEW_TYPE_INFO_ITEM) {
            View infoView = getLayoutFor(parent, R.layout.element_home_info_item);
            return new InfoViewHolder(infoView);
        }
        else if(viewType == HomeItemModel.VIEW_TYPE_ROUTE_ITEM) {
            View homeView = getLayoutFor(parent, R.layout.element_home_route_item);
            return new RouteViewHolder(homeView);
        }

        String exceptionMsgTemplate = "Invalid viewType. Implement an interface from %s. " +
                "Be sure to use a valid VIEW_TYPE value from %s.";
        String exceptionMsg = String.format(exceptionMsgTemplate,
                HomeItemModel.class.getCanonicalName());

        throw new IllegalArgumentException(exceptionMsg);
    }

    private View getLayoutFor(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof InfoViewHolder) {
            onBindInfoViewHolder((InfoViewHolder) holder, position);
        }
        else if(holder instanceof AlertViewHolder) {
            onBindAlertViewHolder((AlertViewHolder) holder, position);
        }
        else if(holder instanceof RouteViewHolder) {
            onBindRouteViewHolder((RouteViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public List<HomeItemModel> getListItems() {
        return mListItems;
    }

    public int addListItem(@NonNull HomeItemModel model) {
        // determine where to insert
        int indexToInsertAt = determineIndexToInsert(model);
        mListItems.add(indexToInsertAt, model);

        return indexToInsertAt;
    }

    public int determineIndexToInsert(@NonNull HomeItemModel model) {
        return 0;
    }

    private void onBindInfoViewHolder(@NonNull InfoViewHolder holder, int position) {

    }

    private void onBindAlertViewHolder(@NonNull AlertViewHolder holder, int position) {

    }

    private void onBindRouteViewHolder(@NonNull RouteViewHolder holder, int position) {

    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {

        public AlertViewHolder(@NonNull View view) {
            super(view);
        }
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {

        public InfoViewHolder(@NonNull View view) {
            super(view);
        }
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        public RouteViewHolder(@NonNull View view) {
            super(view);
        }
    }
}
