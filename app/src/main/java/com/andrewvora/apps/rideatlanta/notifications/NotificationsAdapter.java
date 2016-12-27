package com.andrewvora.apps.rideatlanta.notifications;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.models.Notification;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 11/11/2016.
 * @author Andrew Vorakrajangthiti
 */

public class NotificationsAdapter extends
        RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>
{
    private List<Notification> mNotificationList;

    public NotificationsAdapter(@Nullable List<Notification> notificationList) {
        mNotificationList = notificationList == null ?
                new ArrayList<Notification>() :
                notificationList;
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_notification, parent, false);

        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, int position) {
        Notification notification = mNotificationList.get(position);

        holder.messageTextView.setText(notification.getMessage());
        holder.timeStampTextView.setText(notification.getPostedAt());
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public void setNotifications(List<Notification> notifications) {
        mNotificationList = notifications;
    }

    static class NotificationsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notification_msg_text_view) TextView messageTextView;
        @BindView(R.id.notification_msg_timestamp) TextView timeStampTextView;

        NotificationsViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
