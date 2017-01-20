package com.andrewvora.apps.rideatlanta.data.local.routes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

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

    private static FavoriteRoutesLocalSource mInstance;
    private RideAtlantaDbHelper mDbHelper;

    private FavoriteRoutesLocalSource(@NonNull Context context) {
        mDbHelper = new RideAtlantaDbHelper(context);
    }

    public static FavoriteRoutesLocalSource getInstance(@NonNull Context context) {
        if(mInstance == null) {
            mInstance = new FavoriteRoutesLocalSource(context);
        }

        return mInstance;
    }

    @Override
    public void getFavoriteRoutes(@NonNull GetFavoriteRoutesCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final boolean isNewRecord = route.getId() == null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteRoutesTable.COLUMN_ROUTE_ID, route.getRouteId());
        contentValues.put(FavoriteRoutesTable.COLUMN_TYPE, route.getType());

        if(isNewRecord) {

            db.insertWithOnConflict(FavoriteRoutesTable.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
        else {
            contentValues.put(FavoriteRoutesTable._ID, route.getId());

            String whereClause = FavoriteRoutesTable._ID + "=?";
            String[] whereArgs = new String[] { route.getId().toString() };

            db.updateWithOnConflict(FavoriteRoutesTable.TABLE_NAME,
                    contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
    }

    @Override
    public void deleteAllRoutes() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(FavoriteRoutesTable.TABLE_NAME, "1=1", null);
    }

    @Override
    public void reloadRoutes() {
        // reloading is handled in the FavoriteRoutesRepo.
    }

    private List<FavoriteRoute> getRoutesFrom(Cursor cursor) {
        List<FavoriteRoute> favoriteRoutes = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && cursor.moveToNext()) {
            FavoriteRoute route = getRouteFrom(cursor);
            favoriteRoutes.add(route);
        }

        return favoriteRoutes;
    }

    private FavoriteRoute getRouteFrom(Cursor cursor) {

        FavoriteRoute route = new FavoriteRoute();

        int idIndex = cursor.getColumnIndex(FavoriteRoutesTable._ID);
        int routeIdIndex = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_ROUTE_ID);
        int typeIndex = cursor.getColumnIndex(FavoriteRoutesTable.COLUMN_TYPE);

        route.setId(cursor.getLong(idIndex));
        route.setRouteId(cursor.getString(routeIdIndex));
        route.setType(cursor.getString(typeIndex));

        return route;
    }
}
