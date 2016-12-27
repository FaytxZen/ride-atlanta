package com.andrewvora.apps.rideatlanta.common.models;

import com.andrewvora.apps.rideatlanta.common.FavoriteRouteDataObject;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoute extends BaseModel implements FavoriteRouteDataObject {

    private String routeId;
    private String type;

    @Override
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
