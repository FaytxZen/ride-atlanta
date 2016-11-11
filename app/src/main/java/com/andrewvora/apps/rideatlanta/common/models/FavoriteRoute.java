package com.andrewvora.apps.rideatlanta.common.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoute {

    public static final String TYPE_BUS = "bus";
    public static final String TYPE_TRAIN = "train";

    private String type;
    private String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
