package com.andrewvora.apps.rideatlanta.data.local.trains;

import com.andrewvora.apps.rideatlanta.data.local.common.BaseDbContract;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface TrainsDbContract {

    final class TrainsTable implements BaseDbContract {
        public static final String TABLE_NAME = "trains";

        public static final String COLUMN_TRAIN_ID = "train_id";
        public static final String COLUMN_DESTINATION = "destination";
        public static final String COLUMN_DIRECTION = "direction";
        public static final String COLUMN_EVENT_TIME = "event_time";
        public static final String COLUMN_LINE = "line";
        public static final String COLUMN_NEXT_ARRIVAL = "next_arrival";
        public static final String COLUMN_STATION = "station";
        public static final String COLUMN_WAITING_SECONDS = "waiting_seconds";
        public static final String COLUMN_WAITING_TIME = "waiting_time";
        public static final String COLUMN_FAVORITED = "favorited";

        public static final String CREATE_STATEMENT = String.format(
                "CREATE TABLE %s (%s %s PRIMARY KEY AUTOINCREMENT, %s %s, %s %s, %s %s,"+
                        " %s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s)",
                TABLE_NAME,
                _ID, TYPE_INT,
                COLUMN_TRAIN_ID, TYPE_INT,
                COLUMN_DESTINATION, TYPE_STRING,
                COLUMN_DIRECTION, TYPE_STRING,
                COLUMN_EVENT_TIME, TYPE_STRING,
                COLUMN_LINE, TYPE_STRING,
                COLUMN_NEXT_ARRIVAL, TYPE_STRING,
                COLUMN_STATION, TYPE_STRING,
                COLUMN_WAITING_SECONDS, TYPE_INT,
                COLUMN_WAITING_TIME, TYPE_STRING,
                COLUMN_FAVORITED, TYPE_INT);

        public static String[] getColumns() {
            return new String[] {
                    _ID,
                    COLUMN_TRAIN_ID,
                    COLUMN_DESTINATION,
                    COLUMN_DIRECTION,
                    COLUMN_EVENT_TIME,
                    COLUMN_LINE,
                    COLUMN_NEXT_ARRIVAL,
                    COLUMN_STATION,
                    COLUMN_WAITING_SECONDS,
                    COLUMN_WAITING_TIME,
                    COLUMN_FAVORITED
            };
        }
    }
}
