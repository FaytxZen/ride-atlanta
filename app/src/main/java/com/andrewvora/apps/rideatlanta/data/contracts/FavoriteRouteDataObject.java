package com.andrewvora.apps.rideatlanta.data.contracts;

/**
 * Created by faytx on 12/26/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface FavoriteRouteDataObject {

    String TYPE_BUS = "bus";
    String TYPE_TRAIN = "train";

    String getType();
    String getRouteId();
    String getName();
    String getDestination();
    String getTimeTilArrival();
    String getFavoriteRouteKey();
    String getTravelDirection();
}
