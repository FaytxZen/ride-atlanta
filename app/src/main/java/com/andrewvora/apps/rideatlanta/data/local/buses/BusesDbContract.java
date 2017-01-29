package com.andrewvora.apps.rideatlanta.data.local.buses;

import com.andrewvora.apps.rideatlanta.data.local.common.BaseDbContract;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface BusesDbContract {

    final class BusesTable implements BaseDbContract {
        public static final String TABLE_NAME = "buses";

        public static final String COLUMN_ROUTEID = "route_id";
        public static final String COLUMN_ADHERENCE = "adherence";
        public static final String COLUMN_BLOCKID = "block_id";
        public static final String COLUMN_BLOCK_ABBR = "block_abbr";
        public static final String COLUMN_DIRECTION = "direction";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_MSGTIME = "msgtime";
        public static final String COLUMN_STOPID = "stop_id";
        public static final String COLUMN_TIMEPOINT = "timepoint";
        public static final String COLUMN_TRIPID = "trip_id";
        public static final String COLUMN_VEHICLE = "vehicle";
        public static final String COLUMN_FAVORITED = "favorited";

        public static final String CREATE_STATEMENT = String.format(
                "CREATE TABLE %s (%s %s PRIMARY KEY AUTOINCREMENT, %s %s, %s %s, %s %s,"+
                        " %s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s)",
                TABLE_NAME,
                _ID, TYPE_INT,
                COLUMN_ROUTEID, TYPE_STRING,
                COLUMN_ADHERENCE, TYPE_INT,
                COLUMN_BLOCKID, TYPE_INT,
                COLUMN_BLOCK_ABBR, TYPE_STRING,
                COLUMN_DIRECTION, TYPE_STRING,
                COLUMN_LATITUDE, TYPE_STRING,
                COLUMN_LONGITUDE, TYPE_STRING,
                COLUMN_MSGTIME, TYPE_STRING,
                COLUMN_STOPID, TYPE_INT,
                COLUMN_TIMEPOINT, TYPE_STRING,
                COLUMN_TRIPID, TYPE_INT,
                COLUMN_VEHICLE, TYPE_INT,
                COLUMN_FAVORITED, TYPE_INT
        );

        public static String[] getColumns() {
            return new String[] {
                    _ID,
                    COLUMN_ROUTEID,
                    COLUMN_ADHERENCE,
                    COLUMN_BLOCKID,
                    COLUMN_BLOCK_ABBR,
                    COLUMN_DIRECTION,
                    COLUMN_LATITUDE,
                    COLUMN_LONGITUDE,
                    COLUMN_MSGTIME,
                    COLUMN_STOPID,
                    COLUMN_TIMEPOINT,
                    COLUMN_TRIPID,
                    COLUMN_VEHICLE,
                    COLUMN_FAVORITED, TYPE_INT
            };
        }
    }
}
