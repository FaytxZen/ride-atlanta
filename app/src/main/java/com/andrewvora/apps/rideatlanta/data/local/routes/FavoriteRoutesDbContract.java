package com.andrewvora.apps.rideatlanta.data.local.routes;

import com.andrewvora.apps.rideatlanta.data.local.common.BaseDbContract;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface FavoriteRoutesDbContract {

    final class FavoriteRoutesTable implements BaseDbContract {
        public static final String TABLE_NAME = "favorite_routes";

        public static final String COLUMN_ROUTE_ID = "route_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESTINATION = "destination";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIME_TIL_ARRIVAL = "arrival_time";
        public static final String COLUMN_DIRECTION = "direction";

        public static final String CREATE_STATEMENT = String.format(
                "CREATE TABLE %s (%s %s PRIMARY KEY, %s %s, %s %s, %s %s, %s %s, %s %s, %s, %s)",
                TABLE_NAME,
                _ID, TYPE_STRING,
                COLUMN_ROUTE_ID, TYPE_STRING,
                COLUMN_TYPE, TYPE_STRING,
                COLUMN_DESTINATION, TYPE_STRING,
                COLUMN_NAME, TYPE_STRING,
                COLUMN_TIME_TIL_ARRIVAL, TYPE_STRING,
		        COLUMN_DIRECTION, TYPE_STRING);

        public static String[] getColumns() {
            return new String[] {
                    _ID,
                    COLUMN_ROUTE_ID,
                    COLUMN_TYPE,
                    COLUMN_DESTINATION,
                    COLUMN_NAME,
                    COLUMN_TIME_TIL_ARRIVAL,
		            COLUMN_DIRECTION
            };
        }
    }
}
