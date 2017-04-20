package com.andrewvora.apps.rideatlanta.data.models;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class Train extends BaseModel implements FavoriteRouteDataObject {

    public static final String RED_LINE = "RED";
    public static final String BLUE_LINE = "BLUE";
    public static final String GOLD_LINE = "GOLD";
    public static final String GREEN_LINE = "GREEN";

    @SerializedName("DESTINATION")
    private String destination;

    @SerializedName("DIRECTION")
    private String direction;

    @SerializedName("EVENT_TIME")
    private String eventTime;

    @SerializedName("LINE")
    private String line;

    @SerializedName("NEXT_ARRIVAL")
    private String nextArrival;

    @SerializedName("STATION")
    private String station;

    @SerializedName("TRAIN_ID")
    private Long trainId;

    @SerializedName("WAITING_SECONDS")
    private Integer waitingSeconds;

    @SerializedName("WAITING_TIME")
    private String waitingTime;

    private boolean favorited;

    @Override
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getNextArrival() {
        return nextArrival;
    }

    public void setNextArrival(String nextArrival) {
        this.nextArrival = nextArrival;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public Integer getWaitingSeconds() {
        return waitingSeconds;
    }

    public void setWaitingSeconds(Integer waitingSeconds) {
        this.waitingSeconds = waitingSeconds;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    @Override
    public String getType() {
        return FavoriteRouteDataObject.TYPE_TRAIN;
    }

    @Override
    public String getRouteId() {
        return getTrainId().toString();
    }

    @Override
    public String getName() {
        return getLine();
    }

    @Override
    public String getTimeTilArrival() {
        return getWaitingTime();
    }

    public static int getColorRes(@NonNull String line) {
        switch(line) {
            case Train.BLUE_LINE:
                return R.color.md_blue_500;

            case Train.GREEN_LINE:
                return R.color.md_green_500;

            case Train.RED_LINE:
                return R.color.md_red_500;

            case Train.GOLD_LINE:
                return R.color.md_amber_500;

            default:
                return R.color.md_grey_500;
        }
    }
}
