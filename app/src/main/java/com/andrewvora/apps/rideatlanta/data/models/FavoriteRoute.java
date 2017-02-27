package com.andrewvora.apps.rideatlanta.data.models;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoute extends BaseModel implements FavoriteRouteDataObject, RouteItemModel {

    private String routeId;
    private String type;
    private String name;
    private String destination;
    private String timeTilArrival;

    public FavoriteRoute() {

    }

    public FavoriteRoute(@NonNull Train train) {
        routeId = train.getRouteId();
        type = FavoriteRouteDataObject.TYPE_TRAIN;
        name = train.getLine();
        destination = train.getStation();
        timeTilArrival = train.getTimeTilArrival();
    }

    public FavoriteRoute(@NonNull Bus bus) {
        routeId = bus.getRouteId();
        type = FavoriteRouteDataObject.TYPE_BUS;
        name = bus.getName();
        destination = bus.getDestination();
        timeTilArrival = bus.getTimeTilArrival();
    }

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
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String getTimeTilArrival() {
        return getTimeUntilArrival();
    }

    @Override
    public String getIdentifier() {
        return getType().equals(TYPE_TRAIN) ?
                getName() + getDestination() :
                getRouteId();
    }
}
