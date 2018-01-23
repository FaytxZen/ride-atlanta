package com.andrewvora.apps.rideatlanta.data.local.buses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.buses.BusesDbContract.BusesTable;
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BusesLocalSource implements BusesDataSource {

    private RideAtlantaDbHelper dbHelper;

    public BusesLocalSource(@NonNull Context context) {
        dbHelper = new RideAtlantaDbHelper(context);
    }

	@Override
	public Observable<List<Bus>> getFreshBuses() {
		return getBuses();
	}

	@Override
	public Observable<List<Bus>> getBuses() {
		return Observable.defer(() -> Observable.just(getBusesFromDatabase()));
	}

    private List<Bus> getBusesFromDatabase() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Bus> busList = new ArrayList<>();

        try {
            final String[] columns = BusesTable.getColumns();
            final String selection = "1=1";

            final Cursor busesCursor = db.query(BusesTable.TABLE_NAME,
                    columns, selection, null, null, null, null);
            busList = getBusesFrom(busesCursor);
            busesCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return busList;
    }

	@Override
	public Observable<List<Bus>> getBuses(@NonNull final String... routeIds) {
		return Observable.defer(() -> Observable.just(getBusesFromDatabase(routeIds)));
	}

	private List<Bus> getBusesFromDatabase(@NonNull String... routeIds) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Bus> busList = new ArrayList<>();

        try {
            final String[] columns = BusesTable.getColumns();
            final String selection = String.format("%s IN (%s)",
                    BusesTable.COLUMN_ROUTEID,
                    getIdsAsSqlString(routeIds));

            final Cursor busesCursor = db.query(BusesTable.TABLE_NAME,
                    columns, selection, routeIds, null, null, null);

            busList = getBusesFrom(busesCursor);
            busesCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return busList;
    }

	@Override
	public Observable<Bus> getBus(@NonNull final Bus bus) {
		return Observable.defer(() -> Observable.just(getBusFromDatabase(bus)));
	}

	private Bus getBusFromDatabase(@NonNull Bus bus) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = BusesTable.getColumns();
            String selection = BusesTable.COLUMN_ROUTEID + "=?";
            String[] selectionArgs = new String[] { bus.getRouteId() };
            String numResultsNeeded = "1";

            Cursor busCursor = db.query(BusesTable.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, null, numResultsNeeded);

            if(busCursor.getCount() > 0) {
                busCursor.moveToFirst();

                return getBusFrom(busCursor);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return bus;
    }

	@Override
	public Observable<Long> deleteAllBus() {
		return Observable.defer(() -> Observable.just(deleteAllBusesFromDatabase()));
	}

	private long deleteAllBusesFromDatabase() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		try {
			return db.delete(BusesTable.TABLE_NAME, "1=1", null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1L;
	}

	@Override
	public Observable<Long> saveBus(@NonNull final Bus route) {
		return Observable.defer(() -> Observable.just(saveBusInDatabase(route)));
	}

	private long saveBusInDatabase(@NonNull Bus route) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean newRecord = route.getId() == null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(BusesTable.COLUMN_ROUTEID, route.getRouteId());
        contentValues.put(BusesTable.COLUMN_ADHERENCE, route.getAdherence());
        contentValues.put(BusesTable.COLUMN_BLOCKID, route.getBlockId());
        contentValues.put(BusesTable.COLUMN_BLOCK_ABBR, route.getBlockAbbr());
        contentValues.put(BusesTable.COLUMN_DIRECTION, route.getDirection());
        contentValues.put(BusesTable.COLUMN_LATITUDE, route.getLatitude());
        contentValues.put(BusesTable.COLUMN_LONGITUDE, route.getLongitude());
        contentValues.put(BusesTable.COLUMN_MSGTIME, route.getMsgTime());
        contentValues.put(BusesTable.COLUMN_STOPID, route.getStopId());
        contentValues.put(BusesTable.COLUMN_TIMEPOINT, route.getTimePoint());
        contentValues.put(BusesTable.COLUMN_TRIPID, route.getTripId());
        contentValues.put(BusesTable.COLUMN_VEHICLE, route.getVehicleNumber());
        contentValues.put(BusesTable.COLUMN_FAVORITED, route.isFavorited() ? 1 : 0);

        if(newRecord) {
            final long id = db.insertWithOnConflict(BusesTable.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);

			route.setId(id);

			return id;
        }
        else {
            contentValues.put(BusesTable._ID, route.getId());

            String whereClause = BusesTable._ID + "=? and " + BusesTable.COLUMN_ROUTEID + "=?";
            String[] whereArgs = new String[] { route.getId().toString(), route.getRouteId() };

            return db.updateWithOnConflict(BusesTable.TABLE_NAME,
                    contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    @Override
    public void reloadBuses() {
        // reload handled in the BusesRepo
    }

    private List<Bus> getBusesFrom(Cursor cursor) {
        List<Bus> buses = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && !cursor.isAfterLast()) {
            Bus bus = getBusFrom(cursor);
            buses.add(bus);

            cursor.moveToNext();
        }

        return buses;
    }

    private Bus getBusFrom(Cursor cursor) {

        Bus bus = new Bus();
        int idIndex = cursor.getColumnIndex(BusesTable._ID);
        int routeIdIndex = cursor.getColumnIndex(BusesTable.COLUMN_ROUTEID);
        int adherenceIndex = cursor.getColumnIndex(BusesTable.COLUMN_ADHERENCE);
        int blockIdIndex = cursor.getColumnIndex(BusesTable.COLUMN_BLOCKID);
        int blockAbbrIndex = cursor.getColumnIndex(BusesTable.COLUMN_BLOCK_ABBR);
        int directionIndex = cursor.getColumnIndex(BusesTable.COLUMN_DIRECTION);
        int latitudeIndex = cursor.getColumnIndex(BusesTable.COLUMN_LATITUDE);
        int longitudeIndex = cursor.getColumnIndex(BusesTable.COLUMN_LONGITUDE);
        int msgTimeIndex = cursor.getColumnIndex(BusesTable.COLUMN_MSGTIME);
        int stopIdIndex = cursor.getColumnIndex(BusesTable.COLUMN_STOPID);
        int timePointIndex = cursor.getColumnIndex(BusesTable.COLUMN_TIMEPOINT);
        int tripIdIndex = cursor.getColumnIndex(BusesTable.COLUMN_TRIPID);
        int vehicleIndex = cursor.getColumnIndex(BusesTable.COLUMN_VEHICLE);
        int favoritedIndex = cursor.getColumnIndex(BusesTable.COLUMN_FAVORITED);

        bus.setId(cursor.getLong(idIndex));
        bus.setRouteId(cursor.getString(routeIdIndex));
        bus.setAdherence(cursor.getInt(adherenceIndex));
        bus.setBlockId(cursor.getInt(blockIdIndex));
        bus.setBlockAbbr(cursor.getString(blockAbbrIndex));
        bus.setDirection(cursor.getString(directionIndex));
        bus.setLatitude(cursor.getString(latitudeIndex));
        bus.setLongitude(cursor.getString(longitudeIndex));
        bus.setMsgTime(cursor.getString(msgTimeIndex));
        bus.setStopId(cursor.getLong(stopIdIndex));
        bus.setTimePoint(cursor.getString(timePointIndex));
        bus.setTripId(cursor.getLong(tripIdIndex));
        bus.setVehicleNumber(cursor.getLong(vehicleIndex));
        bus.setFavorited(cursor.getInt(favoritedIndex) == 1);

        return bus;
    }

    private String getIdsAsSqlString(@NonNull String... routeIds) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < routeIds.length; i++) {
            String toAppend = i != routeIds.length - 1 ?
                    routeIds[i] + "," :
                    routeIds[i];

            sb.append(toAppend);
        }

        return sb.toString();
    }
}
