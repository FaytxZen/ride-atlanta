package com.andrewvora.apps.rideatlanta.data.contracts;

/**
 * Created by faytx on 1/7/2017.
 * @author Andrew Vorakrajangthiti
 */
public interface RouteItemModel extends HomeItemModel {

    String getName();
    String getDestination();
    String getTimeUntilArrival();
    String getTravelDirection();
    boolean isBus();
}
