package com.andrewvora.apps.rideatlanta.data.models;

import com.andrewvora.apps.rideatlanta.data.contracts.AlertItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class Notification extends BaseModel implements AlertItemModel {

    @SerializedName("notificationId")
    private String notificationId;

    @SerializedName("postedAt")
    private String postedAt;

    @SerializedName("message")
    private String message;

    @Override
    public int getViewType() {
        return HomeItemModel.VIEW_TYPE_ALERT_ITEM;
    }

    @Override
    public String getTimeStamp() {
        return getPostedAt();
    }

    @Override
    public String getAlertMessage() {
        return getMessage();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
