package com.andrewvora.apps.rideatlanta.data.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */
public class Train extends BaseModel implements FavoriteRouteDataObject, Cloneable, Parcelable {

    private static final String RED_LINE = "RED";
    private static final String BLUE_LINE = "BLUE";
    private static final String GOLD_LINE = "GOLD";
    private static final String GREEN_LINE = "GREEN";

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
        return getStation();
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
    public String getFavoriteRouteKey() {
        return getName() + " " + getDestination();
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

    @Override
    public String getTravelDirection() {
        return getDirection();
    }

    public static String getFormattedTimeTilArrival(@NonNull Context context, @NonNull String tta) {
        final String unknownValue = String.valueOf(Integer.MIN_VALUE);

        if(tta.contains(unknownValue) || tta.isEmpty()) {
            return context.getString(R.string.text_adherence_unknown);
        }
        else {
            return tta;
        }
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

    public static String combineArrivalTimes(@NonNull List<Train> trainList) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < trainList.size(); i++) {
            if(i != 0) {
                sb.append(", ");
            }

            sb.append(trainList.get(i).getTimeTilArrival());
        }

        return sb.toString();
    }

    public Train getCopy() {
        Train train = new Train();
        train.setDestination(getDestination());
        train.setDirection(getDirection());
        train.setEventTime(getEventTime());
        train.setLine(getLine());
        train.setNextArrival(getNextArrival());
        train.setStation(getStation());
        train.setTrainId(getTrainId());
        train.setWaitingSeconds(getWaitingSeconds());
        train.setWaitingTime(getWaitingTime());
        train.setFavorited(isFavorited());

        return train;
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
		dest.writeString(this.destination);
		dest.writeString(this.direction);
		dest.writeString(this.eventTime);
		dest.writeString(this.line);
		dest.writeString(this.nextArrival);
		dest.writeString(this.station);
		dest.writeValue(this.trainId);
		dest.writeValue(this.waitingSeconds);
		dest.writeString(this.waitingTime);
		dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
		dest.writeValue(this.id);
	}

	public Train() {
	}

	protected Train(Parcel in) {
		this.destination = in.readString();
		this.direction = in.readString();
		this.eventTime = in.readString();
		this.line = in.readString();
		this.nextArrival = in.readString();
		this.station = in.readString();
		this.trainId = (Long) in.readValue(Long.class.getClassLoader());
		this.waitingSeconds = (Integer) in.readValue(Integer.class.getClassLoader());
		this.waitingTime = in.readString();
		this.favorited = in.readByte() != 0;
		this.id = (Long) in.readValue(Long.class.getClassLoader());
	}

	public static final Creator<Train> CREATOR = new Creator<Train>() {
		@Override
		public Train createFromParcel(Parcel source) {
			return new Train(source);
		}

		@Override
		public Train[] newArray(int size) {
			return new Train[size];
		}
	};
}
