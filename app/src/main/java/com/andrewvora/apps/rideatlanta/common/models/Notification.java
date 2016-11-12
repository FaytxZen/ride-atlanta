package com.andrewvora.apps.rideatlanta.common.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class Notification {

    @SerializedName("id")
    private String id;

    @SerializedName("date")
    private String date;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
