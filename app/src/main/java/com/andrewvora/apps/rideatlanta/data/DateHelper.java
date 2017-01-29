package com.andrewvora.apps.rideatlanta.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrewvora.apps.rideatlanta.RideAtlantaApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by faytx on 1/22/2017.
 * @author Andrew Vorakrajangthiti
 */
public final class DateHelper {

    public static final String TWITTER_TIME_STAMP_FORMAT = "EEE MMM d H:mm:ss Z yyyy";
    public static final String TIME_STAMP_FORMAT = "MMM d, yyyy, h:mm a";

    private boolean mUseGmtTimeZone;

    private DateHelper() {
        // prevent direct instantiation
    }

    public static DateHelper getInstance() {
        return new DateHelper();
    }

    public DateHelper useGmtTimeZone() {
        mUseGmtTimeZone = true;
        return this;
    }

    public long getTimeAsMilliseconds(String timeStamp, String dateFormat) {
        long timeInMillis = 0L;

        try {
            timeInMillis = getDateFormatter(dateFormat).parse(timeStamp).getTime();
        }
        catch (Exception ex) {
            Log.e(RideAtlantaApplication.class.getSimpleName(), ex.getMessage());
        }

        return timeInMillis;
    }

    public String getRelativeTimeStamp(long currentMillis) {
        final Calendar currentCalendar = Calendar.getInstance();
        final Calendar givenCalendar = new GregorianCalendar();
        givenCalendar.setTimeInMillis(currentMillis);

        if(withinSameHour(givenCalendar, currentCalendar)) {
            final int minutesSince = currentCalendar.get(Calendar.MINUTE) -
                    givenCalendar.get(Calendar.MINUTE);

            return minutesSince == 1 ? minutesSince + " min" : minutesSince + " mins";
        }
        else if(within12Hours(givenCalendar, currentCalendar)) {
            final long hoursSince = TimeUnit.MILLISECONDS.toHours(
                    currentCalendar.getTimeInMillis() - givenCalendar.getTimeInMillis());
            return hoursSince + "h";
        }
        else {
            return getDateFormatter(TIME_STAMP_FORMAT).format(givenCalendar.getTime());
        }
    }

    private boolean within12Hours(@NonNull Calendar cal1, @NonNull Calendar cal2) {
        return TimeUnit.MILLISECONDS.toHours(cal2.getTimeInMillis() - cal1.getTimeInMillis()) <= 12;
    }

    private boolean withinSameHour(@NonNull Calendar cal1, @NonNull Calendar cal2) {
        return cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY);
    }

    private DateFormat getDateFormatter(@NonNull String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

        if(mUseGmtTimeZone) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        return dateFormat;
    }
}
