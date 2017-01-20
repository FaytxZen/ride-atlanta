package com.andrewvora.apps.rideatlanta.data.local.buses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.buses.BusesDbContract.BusesTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusesLocalSource implements BusesDataSource {

    private static BusesLocalSource mInstance;
    private RideAtlantaDbHelper mDbHelper;

    private BusesLocalSource(@NonNull Context context) {
        mDbHelper = new RideAtlantaDbHelper(context);
    }

    public static BusesLocalSource getInstance(@NonNull Context context) {
        if(mInstance == null) {
            mInstance = new BusesLocalSource(context);
        }

        return mInstance;
    }

    @Override
    public void getBuses(@NonNull GetBusesCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        try {
            String[] columns = BusesTable.getColumns();
            String selection = "1=1";

            Cursor busesCursor = db.query(BusesTable.TABLE_NAME,
                    columns, selection, null, null, null, null);
            List<Bus> busList = getBusesFrom(busesCursor);
            callback.onFinished(busList);

            busesCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void getBuses(@NonNull GetBusesCallback callback, @NonNull String... routeIds) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        try {
            String[] columns = BusesTable.getColumns();
            String selection = String.format("%s IN (%s)",
                    BusesTable.COLUMN_ROUTEID,
                    getIdsAsSqlString(routeIds));

            Cursor busesCursor = db.query(BusesTable.TABLE_NAME,
                    columns, selection, routeIds, null, null, null);
            List<Bus> busList = getBusesFrom(busesCursor);
            callback.onFinished(busList);

            busesCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void getBus(@NonNull String routeId, @NonNull GetBusCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        try {
            String[] columns = BusesTable.getColumns();
            String selection = BusesTable.COLUMN_ROUTEID + "=?";
            String[] selectionArgs = new String[] { routeId };
            String numResultsNeeded = "1";

            Cursor busCursor = db.query(BusesTable.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, null, numResultsNeeded);
            Bus bus = getBusFrom(busCursor);
            callback.onFinished(bus);
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void deleteAllBus(@Nullable DeleteBusesCallback callback) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            db.delete(BusesTable.TABLE_NAME, "1=1", null);

            if(callback != null) {
                callback.onDeleted();
            }

        } catch (Exception e) {
            e.printStackTrace();

            if(callback != null) {
                callback.onError(e);
            }
        }
    }

    @Override
    public void saveBus(@NonNull Bus route) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
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


        if(newRecord) {
            db.insertWithOnConflict(BusesTable.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
        else {
            contentValues.put(BusesTable._ID, route.getId());

            String whereClause = BusesTable._ID + "=? and " + BusesTable.COLUMN_ROUTEID + "=?";
            String[] whereArgs = new String[] { route.getId().toString(), route.getRouteId() };

            db.updateWithOnConflict(BusesTable.TABLE_NAME,
                    contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
    }

    @Override
    public void reloadBuses() {
        // reload handled in the BusesRepo
    }

    private List<Bus> getBusesFrom(Cursor cursor) {
        List<Bus> buses = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && cursor.moveToNext()) {
            Bus bus = getBusFrom(cursor);
            buses.add(bus);
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
