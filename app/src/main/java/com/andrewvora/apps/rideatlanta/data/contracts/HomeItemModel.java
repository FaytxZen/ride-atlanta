package com.andrewvora.apps.rideatlanta.data.contracts;

/**
 * Created by faytx on 1/7/2017.
 * @author Andrew Vorakrajangthiti
 */
public interface HomeItemModel {

    int VIEW_TYPE_INFO_ITEM = 0;
    int VIEW_TYPE_ALERT_ITEM = 1;
    int VIEW_TYPE_ROUTE_ITEM = 2;

    int getViewType();
}
