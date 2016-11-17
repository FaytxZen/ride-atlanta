package com.andrewvora.apps.rideatlanta.common.models;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoute extends BaseModel {

    public static final String TYPE_BUS = "bus";
    public static final String TYPE_TRAIN = "train";

    private String routeId;
    private String type;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
