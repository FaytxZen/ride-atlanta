package com.andrewvora.apps.rideatlanta.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.DateHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.InfoItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.seeandsay.SeeAndSayActivity;
import com.andrewvora.apps.rideatlanta.utils.HtmlUtil;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 1/7/2017.
 * @author Andrew Vorakrajangthiti
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull private Map<String, HomeItemModel> mItemMap;
    @NonNull private List<HomeItemModel> mItemList;

    public HomeAdapter(@Nullable List<HomeItemModel> listItemModels) {
        mItemMap = new LinkedHashMap<>();

        if(listItemModels != null) {
            for(HomeItemModel item : listItemModels) {
                mItemMap.put(item.getIdentifier(), item);
            }
        }

        mItemList = new ArrayList<>(mItemMap.values());
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
        return mItemList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public int addListItem(@NonNull HomeItemModel model) {
        if(mItemMap.containsKey(model.getIdentifier())) {
            int position = 0;

            for(int i = 0; i < getItemCount(); i++) {
                if(mItemList.get(i).getIdentifier().equals(model.getIdentifier())) {
                    position = i;
                    break;
                }
            }

            // update lists and maps
            mItemMap.put(model.getIdentifier(), model);
            mItemList.set(position, model);

            // this is to differentiate between inserting and updating
            // if negative, it means we're updating
            // MIN_VALUE means the item was not found
            return (position + 1) * -1;
        }
        else {
            // determine where to insert
            int indexToInsertAt = determineIndexToInsert(model);
            mItemMap.put(model.getIdentifier(), model);
            mItemList.add(indexToInsertAt, model);

            return indexToInsertAt;
        }
    }

    private int determineIndexToInsert(@NonNull HomeItemModel model) {
        if(model.getViewType() == HomeItemModel.VIEW_TYPE_ALERT_ITEM) {

            for(int i = 0; i < mItemList.size(); i++) {
                HomeItemModel homeItem = mItemList.get(i);
                final boolean isNotRoute = homeItem.getViewType() !=
                        HomeItemModel.VIEW_TYPE_ROUTE_ITEM;

                if(isNotRoute) {
                    return i;
                }
            }

            return 0;
        }
        else if(model.getViewType() == HomeItemModel.VIEW_TYPE_INFO_ITEM) {
            return getItemCount();
        }
        else {
            return 0;
        }
    }

    private void onBindInfoViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItemModel infoItemModel = (InfoItemModel) mItemList.get(position);
        Context context = holder.itemView.getContext();

        holder.infoTextView.setText(infoItemModel.getInfoText());

        String infoButtonText = infoItemModel.getActionText(context);
        holder.infoButton.setText(infoButtonText);

        holder.infoButton.setOnClickListener(getClickListenerFor(infoItemModel.getActionType()));
    }

    private void onBindAlertViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertItemModel alertItemModel = (AlertItemModel) mItemList.get(position);

        String decodedMsg = HtmlUtil.getDecodedHtml(alertItemModel.getAlertMessage());

        holder.messageTextView.setText(decodedMsg);

        final DateHelper dateHelper = DateHelper.getInstance();
        final String timeStamp = alertItemModel.getTimeStamp();
        final long timeInMillis = dateHelper.getTimeAsMilliseconds(
                timeStamp, DateHelper.TWITTER_TIME_STAMP_FORMAT);
        final String formattedTimeStamp = dateHelper.getRelativeTimeStamp(timeInMillis);

        holder.timeStampTextView.setText(formattedTimeStamp);
    }

    private void onBindRouteViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteItemModel routeItemModel = (RouteItemModel) mItemList.get(position);
        Context context = holder.itemView.getContext();

        String destination = WordUtils.capitalizeWords(routeItemModel.getDestination());
        holder.destinationTextView.setText(destination);

        if(routeItemModel.isBus()) {
            int adherence = Bus.parseAdherence(routeItemModel.getTimeUntilArrival());
            String arrivalTime = Bus.getFormattedAdherence(context, adherence);

            holder.timeTilArrivalTextView.setText(arrivalTime);

            holder.nameTextView.setText(routeItemModel.getName());
            holder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bus_white_24dp, 0, 0, 0);
        }
        else {
            holder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_train_white_24dp, 0, 0, 0);

            int color = ContextCompat.getColor(context, Train.getColorRes(routeItemModel.getName()));
            holder.nameTextView.setBackgroundColor(color);

            final String arrivalTime = Train.getFormattedTimeTilArrival(context, routeItemModel.getTimeUntilArrival());

            holder.timeTilArrivalTextView.setText(arrivalTime);
        }
    }
    private View getLayoutFor(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    private View.OnClickListener getClickListenerFor(final int actionType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(actionType) {
                    case InfoItemModel.SEE_AND_SAY:
                        Context context = view.getContext();
                        Intent startSeeAndSayIntent = new Intent(context, SeeAndSayActivity.class);
                        context.startActivity(startSeeAndSayIntent);
                        break;

                    case InfoItemModel.TIP_ABOUT_FAVORITES:
                        break;
                }
            }
        };
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.alert_time_stamp) TextView timeStampTextView;
        @BindView(R.id.alert_message) TextView messageTextView;

        AlertViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.info_message) TextView infoTextView;
        @BindView(R.id.info_action_button) Button infoButton;

        InfoViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.route_name) TextView nameTextView;
        @BindView(R.id.route_destination) TextView destinationTextView;
        @BindView(R.id.route_time_until_arrival) TextView timeTilArrivalTextView;

        RouteViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
