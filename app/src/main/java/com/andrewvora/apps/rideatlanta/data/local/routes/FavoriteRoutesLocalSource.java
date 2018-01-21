package com.andrewvora.apps.rideatlanta.data.local.routes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.routes.FavoriteRoutesDbContract.FavoriteRoutesTable;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

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
	public Observable<List<FavoriteRoute>> getFavoriteRoutes() {
		return Observable.defer(() -> Observable.just(getFavoriteRoutesFromDatabase()));
	}

    private List<FavoriteRoute> getFavoriteRoutesFromDatabase() {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			final String[] columns = FavoriteRoutesTable.getColumns();
			final String selection = "1=1";

			final Cursor routesCursor = db.query(FavoriteRoutesTable.TABLE_NAME,
					columns, selection, null, null, null, null);
			final List<FavoriteRoute> favoriteRoutes = getRoutesFrom(routesCursor);
			routesCursor.close();

			return favoriteRoutes;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	@Override
	public Observable<FavoriteRoute> getFavoriteRoute(@NonNull String routeId) {
		final FavoriteRoute route = getFavoriteRouteFromDatabase(routeId);

		return route != null ?
				Observable.just(route) :
				Observable.empty();
	}

	private FavoriteRoute getFavoriteRouteFromDatabase(@NonNull String routeId) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			final String[] columns = FavoriteRoutesTable.getColumns();
			final String selection = FavoriteRoutesTable._ID + "=?";
			final String[] selectionArgs = new String[] { routeId };
			final String numResultsNeeded = "1";

			final Cursor routeCursor = db.query(FavoriteRoutesTable.TABLE_NAME,
					columns, selection, selectionArgs, null, null, null, numResultsNeeded);
			final FavoriteRoute favoriteRoute = getRouteFrom(routeCursor);
			routeCursor.close();

			return favoriteRoute;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Observable<Long> saveRoute(@NonNull FavoriteRoute route) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final boolean isNewRecord = route.getId() == null;

		final ContentValues contentValues = new ContentValues();
		contentValues.put(FavoriteRoutesTable.COLUMN_ROUTE_ID, route.getRouteId());
		contentValues.put(FavoriteRoutesTable.COLUMN_TYPE, route.getType());
		contentValues.put(FavoriteRoutesTable.COLUMN_NAME, route.getName());
		contentValues.put(FavoriteRoutesTable.COLUMN_DESTINATION, route.getDestination());
		contentValues.put(FavoriteRoutesTable.COLUMN_TIME_TIL_ARRIVAL, String.valueOf(Integer.MIN_VALUE));

		if(isNewRecord) {
			final long id = db.insertWithOnConflict(FavoriteRoutesTable.TABLE_NAME,
					null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);

			route.setId(id);

			return Observable.just(id);
		}
		else {
			final String whereClause = FavoriteRoutesTable.COLUMN_ROUTE_ID + "=?";
			final String[] whereArgs = new String[] { route.getRouteId() };

			final long recordsUpdated = db.updateWithOnConflict(FavoriteRoutesTable.TABLE_NAME,
					contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);

			return Observable.just(recordsUpdated);
		}
	}

	@Override
	public Observable<Long> deleteAllRoutes() {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final long recordsDeleted = db.delete(FavoriteRoutesTable.TABLE_NAME, "1=1", null);

		return Observable.just(recordsDeleted);
	}

	@Override
	public Observable<Long> deleteRoute(@NonNull FavoriteRouteDataObject route) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		final String whereClause = FavoriteRoutesTable.COLUMN_ROUTE_ID + "=?";
		final String[] whereArgs = { route.getRouteId() };
		final long recordsDeleted = db.delete(FavoriteRoutesTable.TABLE_NAME, whereClause, whereArgs);

		return Observable.just(recordsDeleted);
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
