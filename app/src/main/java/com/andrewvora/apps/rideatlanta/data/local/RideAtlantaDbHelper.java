package com.andrewvora.apps.rideatlanta.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrewvora.apps.rideatlanta.data.local.buses.BusesDbContract;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesDbContract;
import com.andrewvora.apps.rideatlanta.data.local.notifications.NotificationsDbContract;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsDbContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/23/2016.
 * @author Andrew Vorakrajangthiti
 */

public class RideAtlantaDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "rideatlanta.db";
    private static final int DB_VERSION = 2;

    public RideAtlantaDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String CREATE_TRAINS_TABLE = TrainsDbContract.TrainsTable.CREATE_STATEMENT;
    private static final String CREATE_BUSES_TABLE = BusesDbContract.BusesTable.CREATE_STATEMENT;
    private static final String CREATE_FAV_ROUTES_TABLE =
            FavoriteRoutesDbContract.FavoriteRoutesTable.CREATE_STATEMENT;
    private static final String CREATE_NOTIFICATIONS_TABLE =
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
	    // TODO: eventually move to proper SQL migrations
		deleteAllTables(sqLiteDatabase);
    }

    private void deleteAllTables(SQLiteDatabase db) {
	    // query to obtain the names of all tables in your database
	    Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
	    List<String> tables = new ArrayList<>();

		// iterate over the result set, adding every table name to a list
	    while (c.moveToNext()) {
		    tables.add(c.getString(0));
	    }

		// call DROP TABLE on every table name
	    for (String table : tables) {
		    String dropQuery = "DROP TABLE IF EXISTS " + table;
		    db.execSQL(dropQuery);
	    }

	    c.close();
    }
}
