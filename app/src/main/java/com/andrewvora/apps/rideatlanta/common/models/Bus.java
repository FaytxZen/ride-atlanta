package com.andrewvora.apps.rideatlanta.common.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class Bus {
    @SerializedName("ADHERENCE")
    private Integer adherence;

    @SerializedName("BLOCKID")
    private Integer blockId;

    @SerializedName("BLOCK_ABBR")
    private String blockAbbr;

    @SerializedName("DIRECTION")
    private String direction;

    @SerializedName("LATITUDE")
    private String latitude;

    @SerializedName("LONGITUDE")
    private String longitude;

    @SerializedName("MSGTIME")
    private String msgTime;

    @SerializedName("ROUTE")
    private String routeId;

    @SerializedName("STOPID")
    private Long stopId;

    @SerializedName("TIMEPOINT")
    private String timePoint;

    @SerializedName("TRIPID")
    private Long tripId;

    @SerializedName("VEHICLE")
    private Long vehicleNumber;

    public Integer getAdherence() {
        return adherence;
    }

    public void setAdherence(Integer adherence) {
        this.adherence = adherence;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public String getBlockAbbr() {
        return blockAbbr;
    }

    public void setBlockAbbr(String blockAbbr) {
        this.blockAbbr = blockAbbr;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public String getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(Long vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
