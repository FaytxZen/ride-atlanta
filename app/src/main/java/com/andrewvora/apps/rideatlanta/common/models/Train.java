package com.andrewvora.apps.rideatlanta.common.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class Train {

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
}
