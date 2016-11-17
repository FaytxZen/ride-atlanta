package com.andrewvora.apps.rideatlanta.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrewvora.apps.rideatlanta.data.local.buses.BusesDbContract;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesDbContract;
import com.andrewvora.apps.rideatlanta.data.local.notifications.NotificationsDbContract;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsDbContract;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class RideAtlantaDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "rideatlanta.db";
    public static final int DB_VERSION = 1;

    public RideAtlantaDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static final String CREATE_TRAINS_TABLE = TrainsDbContract.TrainsTable.CREATE_STATEMENT;
    public static final String CREATE_BUSES_TABLE = BusesDbContract.BusesTable.CREATE_STATEMENT;
    public static final String CREATE_FAV_ROUTES_TABLE =
            FavoriteRoutesDbContract.FavoriteRoutesTable.CREATE_STATEMENT;
    public static final String CREATE_NOTIFICATIONS_TABLE =
            NotificationsDbContract.NotificationsTable.CREATE_STATEMENT;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TRAINS_TABLE);
        sqLiteDatabase.execSQL(CREATE_BUSES_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTIFICATIONS_TABLE);

        sqLiteDatabase.execSQL(CREATE_FAV_ROUTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
