package com.andrewvora.apps.rideatlanta.notifications;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.DateHelper;
import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.utils.HtmlUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles and manages the display of {@link Notification} objects to
 * {@link NotificationsFragment}.
 *
 * Created by faytx on 11/11/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsAdapter extends
        RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>
{
    @NonNull
    private List<Notification> mNotificationList;

    NotificationsAdapter(@NonNull List<Notification> notificationList) {
        mNotificationList = notificationList;
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_notification, parent, false);

        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, int position) {
        final Notification notification = mNotificationList.get(position);

        // set message
        String decodedMsg = HtmlUtil.getDecodedHtml(notification.getMessage());
        holder.messageTextView.setText(decodedMsg);

        // set timestamp
        final DateHelper dateHelper = DateHelper.getInstance();
        final long timeInMillis = dateHelper.getTimeAsMilliseconds(
                notification.getPostedAt(),
                DateHelper.TWITTER_TIME_STAMP_FORMAT);
        final String timeToDisplay = dateHelper.getRelativeTimeStamp(timeInMillis);

        holder.timeStampTextView.setText(timeToDisplay);
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public void setNotifications(@NonNull List<Notification> notifications) {
        mNotificationList = notifications;
    }

    @NonNull
    public List<Notification> getNotifications() {
        return mNotificationList;
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
