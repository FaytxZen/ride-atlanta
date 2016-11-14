package com.andrewvora.apps.rideatlanta.data.local.notifications;

import android.provider.BaseColumns;

import com.andrewvora.apps.rideatlanta.data.local.common.BaseDbContract;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface NotificationsDbContract {

    final class NotificationsTable implements BaseDbContract {
        public static final String TABLE_NAME = "notifications";

        public static final String COLUMN_NOTIFICATION_ID = "notification_id";
        public static final String COLUMN_ALERT_DATE = "alert_date";
        public static final String COLUMN_MESSAGE = "message";

        public static final String CREATE_STATEMENT = String.format(
                "CREATE TABLE %s (%s %s PRIMARY KEY, %s %s, %s %s, %s %s)",
                TABLE_NAME,
                _ID, TYPE_STRING,
                COLUMN_NOTIFICATION_ID, TYPE_STRING,
                COLUMN_ALERT_DATE, TYPE_STRING,
                COLUMN_MESSAGE, TYPE_STRING
        );

        public static String[] getColumns() {
            return new String[] {
                    _ID,
                    COLUMN_NOTIFICATION_ID,
                    COLUMN_ALERT_DATE,
                    COLUMN_MESSAGE
            };
        }
    }
}
