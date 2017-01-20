package com.andrewvora.apps.rideatlanta.data.models;

import com.andrewvora.apps.rideatlanta.common.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.common.HomeItemModel;
import com.andrewvora.apps.rideatlanta.common.RouteItemModel;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoute extends BaseModel implements FavoriteRouteDataObject, RouteItemModel {

    private String routeId;
    private String type;
    private String destination;
    private String timeTilArrival;

    @Override
    public int getViewType() {
        return HomeItemModel.VIEW_TYPE_ROUTE_ITEM;
    }

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

    @Override
    public String getName() {
        return getRouteId();
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String getTimeUntilArrival() {
        return timeTilArrival;
    }

    public void setTimeUntilArrival(String untilArrival) {
        this.timeTilArrival = untilArrival;
    }
}
