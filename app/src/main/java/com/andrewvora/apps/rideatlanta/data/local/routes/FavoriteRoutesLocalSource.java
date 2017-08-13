package com.andrewvora.apps.rideatlanta.data.local.routes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesDbContract.FavoriteRoutesTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/24/2016.
 * @author Andrew Vorakrajangthiti
 */

public class FavoriteRoutesLocalSource implements FavoriteRoutesDataSource {
    private RideAtlantaDbHelper dbHelper;

    public FavoriteRoutesLocalSource(@NonNull Context context) {
        dbHelper = new RideAtlantaDbHelper(context);
    }

    @Override
    public void getFavoriteRoutes(@NonNull GetFavoriteRoutesCallback callback) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = FavoriteRoutesTable.getColumns();
            String selection = "1=1";

            Cursor routesCursor = db.query(FavoriteRoutesTable.TABLE_NAME,
                    columns, selection, null, null, null, null);
            List<FavoriteRoute> favoriteRoutes = getRoutesFrom(routesCursor);
            routesCursor.close();

            callback.onFinished(favoriteRoutes);
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void getFavoriteRoute(@NonNull String recordId, @NonNull GetFavoriteRouteCallback callback) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = FavoriteRoutesTable.getColumns();
            String selection = FavoriteRoutesTable._ID + "=?";
            String[] selectionArgs = new String[] { recordId };
            String numResultsNeeded = "1";

            Cursor routeCursor = db.query(FavoriteRoutesTable.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, null, numResultsNeeded);
            FavoriteRoute favoriteRoute = getRouteFrom(routeCursor);
            routeCursor.close();

            callback.onFinished(favoriteRoute);
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void saveRoute(@NonNull FavoriteRoute route) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean isNewRecord = route.getId() == null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteRoutesTable.COLUMN_ROUTE_ID, route.getRouteId());
        contentValues.put(FavoriteRoutesTable.COLUMN_TYPE, route.getType());
        contentValues.put(FavoriteRoutesTable.COLUMN_NAME, route.getName());
        contentValues.put(FavoriteRoutesTable.COLUMN_DESTINATION, route.getDestination());
        contentValues.put(FavoriteRoutesTable.COLUMN_TIME_TIL_ARRIVAL, String.valueOf(Integer.MIN_VALUE));

        if(isNewRecord) {
            long id = db.insertWithOnConflict(FavoriteRoutesTable.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);

            route.setId(id);
        }
        else {
            String whereClause = FavoriteRoutesTable.COLUMN_ROUTE_ID + "=?";
            String[] whereArgs = new String[] { route.getRouteId() };

            db.updateWithOnConflict(FavoriteRoutesTable.TABLE_NAME,
                    contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
    }

    @Override
    public void deleteAllRoutes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FavoriteRoutesTable.TABLE_NAME, "1=1", null);
    }

    @Override
    public void deleteRoute(@NonNull FavoriteRouteDataObject route) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = FavoriteRoutesTable.COLUMN_ROUTE_ID + "=?";
        String[] whereArgs = { route.getRouteId() };
        db.delete(FavoriteRoutesTable.TABLE_NAME, whereClause, whereArgs);
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    @Override
    public void reloadRoutes() {
        // reloading is handled in the FavoriteRoutesRepo.
    }

    private List<FavoriteRoute> getRoutesFrom(Cursor cursor) {
        List<FavoriteRoute> favoriteRoutes = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && !cursor.isAfterLast()) {
            FavoriteRoute route = getRouteFrom(cursor);
            favoriteRoutes.add(route);

            cursor.moveToNext();
        }

        return favoriteRoutes;
    }

    private FavoriteRoute getRouteFrom(Cursor cursor) {

        FavoriteRoute route = new FavoriteRoute();

        int idIndex = cursor.getColumnIndex(FavoriteRoutesTable._ID);
        int routeIdIndex = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_ROUTE_ID);
        int typeIndex = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_TYPE);
        int destinationIndex = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_DESTINATION);
        int nameIndex = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_NAME);
        int timeTilArrival = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_TIME_TIL_ARRIVAL);

        route.setId(cursor.getLong(idIndex));
        route.setRouteId(cursor.getString(routeIdIndex));
        route.setType(cursor.getString(typeIndex));
        route.setDestination(cursor.getString(destinationIndex));
        route.setTimeUntilArrival(cursor.getString(timeTilArrival));
        route.setName(cursor.getString(nameIndex));

        return route;
    }
}
