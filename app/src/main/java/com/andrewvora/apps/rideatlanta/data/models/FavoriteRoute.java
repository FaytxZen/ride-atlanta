package com.andrewvora.apps.rideatlanta.data.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;
import com.andrewvora.apps.rideatlanta.data.contracts.RouteItemModel;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoute extends BaseModel implements FavoriteRouteDataObject, RouteItemModel, Parcelable {

    private String routeId;
    private String type;
    private String name;
    private String destination;
    private String direction;
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
    public boolean isBus() {
        return FavoriteRouteDataObject.TYPE_BUS.equals(getType());
    }

    @Override
    public String getTravelDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String getFavoriteRouteKey() {
        return isBus() ? getName() : getName() + " " + getDestination() + " " + getTravelDirection();
    }

    @Override
    public String getIdentifier() {
        return getType().equals(TYPE_TRAIN) ?
                getName() + getDestination() + getTravelDirection() :
                getRouteId();
    }

    /*------------------------------------*
     * Generated Parcelable Code
     *------------------------------------*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.routeId);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.destination);
        dest.writeString(this.direction);
        dest.writeString(this.timeTilArrival);
        dest.writeValue(this.id);
    }

    protected FavoriteRoute(Parcel in) {
        this.routeId = in.readString();
        this.type = in.readString();
        this.name = in.readString();
        this.destination = in.readString();
        this.direction = in.readString();
        this.timeTilArrival = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<FavoriteRoute> CREATOR = new Parcelable.Creator<FavoriteRoute>() {
        @Override
        public FavoriteRoute createFromParcel(Parcel source) {
            return new FavoriteRoute(source);
        }

        @Override
        public FavoriteRoute[] newArray(int size) {
            return new FavoriteRoute[size];
        }
    };
}
