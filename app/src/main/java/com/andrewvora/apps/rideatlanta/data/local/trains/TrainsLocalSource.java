package com.andrewvora.apps.rideatlanta.data.local.trains;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.common.models.Train;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsDbContract.TrainsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsLocalSource implements TrainsDataSource {

    private static TrainsLocalSource mInstance;
    private RideAtlantaDbHelper mDbHelper;

    private TrainsLocalSource(@NonNull Context context) {
        mDbHelper = new RideAtlantaDbHelper(context);
    }

    public static TrainsLocalSource getInstance(@NonNull Context context) {

        if(mInstance == null) {
            mInstance = new TrainsLocalSource(context);
        }

        return mInstance;
    }

    @Override
    public void getTrains(@NonNull GetTrainRoutesCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        try {
            String[] columns = TrainsTable.getColumns();
            String selection = "1=1";

            Cursor trainsCursor = db.query(TrainsTable.TABLE_NAME,
                    columns, selection, null, null, null, null);
            List<Train> trainList = getTrainsFrom(trainsCursor);
            callback.onFinished(trainList);

            trainsCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void getTrains(@NonNull GetTrainRoutesCallback callback, @NonNull Long... trainIds) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        try {
            String[] columns = TrainsTable.getColumns();
            String[] selectionArgs = new String[trainIds.length];
            String selection = String.format("%s IN (%s)",
                    TrainsTable.COLUMN_TRAIN_ID,
                    getIdsAsSqlString(selectionArgs));

            for(int i = 0; i < selectionArgs.length; i++) {
                selectionArgs[i] = trainIds[i].toString();
            }

            Cursor trainsCursor = db.query(TrainsTable.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, null);
            List<Train> trainList = getTrainsFrom(trainsCursor);
            callback.onFinished(trainList);

            trainsCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private String getIdsAsSqlString(@NonNull String... trainIds) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < trainIds.length; i++) {
            String toAppend = i != trainIds.length - 1 ?
                    trainIds[i] + "," :
                    trainIds[i];

            sb.append(toAppend);
        }

        return sb.toString();
    }

    @Override
    public void getTrain(@NonNull Long trainId, @NonNull GetTrainRouteCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        try {
            String[] columns = TrainsTable.getColumns();
            String selection = TrainsTable.COLUMN_TRAIN_ID+ "=?";
            String[] selectionArgs = new String[] { trainId.toString() };
            String numResultsNeeded = "1";

            Cursor trainCursor = db.query(TrainsTable.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, null, numResultsNeeded);
            Train train = getTrainFrom(trainCursor);
            callback.onFinished(train);
        }
        catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    @Override
    public void deleteAllTrains(@Nullable DeleteTrainRoutesCallback callback) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            db.delete(TrainsTable.TABLE_NAME, "1=1", null);

            if(callback != null) {
                callback.onDeleted();
            }
        }
        catch (Exception e) {
            e.printStackTrace();

            if(callback != null) {
                callback.onError(e);
            }
        }
    }

    @Override
    public void saveTrain(@NonNull Train route) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final boolean newRecord = route.getId() == null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(TrainsTable.COLUMN_TRAIN_ID, route.getTrainId());
        contentValues.put(TrainsTable.COLUMN_DESTINATION, route.getDestination());
        contentValues.put(TrainsTable.COLUMN_DIRECTION, route.getDirection());
        contentValues.put(TrainsTable.COLUMN_EVENT_TIME, route.getEventTime());
        contentValues.put(TrainsTable.COLUMN_LINE, route.getLine());
        contentValues.put(TrainsTable.COLUMN_NEXT_ARRIVAL, route.getNextArrival());
        contentValues.put(TrainsTable.COLUMN_STATION, route.getStation());
        contentValues.put(TrainsTable.COLUMN_WAITING_SECONDS, route.getWaitingSeconds());
        contentValues.put(TrainsTable.COLUMN_WAITING_TIME, route.getWaitingTime());

        if(newRecord) {
            db.insertWithOnConflict(TrainsTable.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
        else {
            contentValues.put(TrainsTable._ID, route.getId());

            String whereClause = TrainsTable._ID + "=? and " + TrainsTable.COLUMN_TRAIN_ID + "=?";
            String[] whereArgs = new String[] {
                    route.getId().toString(),
                    route.getTrainId().toString()
            };

            db.updateWithOnConflict(TrainsTable.TABLE_NAME,
                    contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
    }

    @Override
    public void reloadTrains() {
        // refreshing handled in the TrainRepo
    }

    private List<Train> getTrainsFrom(Cursor cursor) {
        List<Train> trains = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && cursor.moveToNext()) {
            Train train = getTrainFrom(cursor);
            trains.add(train);
        }

        return trains;
    }

    private Train getTrainFrom(Cursor cursor) {
        Train train = new Train();

        int idIndex = cursor.getColumnIndex(TrainsTable._ID);
        int trainIdIndex = cursor.getColumnIndex(TrainsTable.COLUMN_TRAIN_ID);
        int destinationIndex = cursor.getColumnIndex(TrainsTable.COLUMN_DESTINATION);
        int directionIndex = cursor.getColumnIndex(TrainsTable.COLUMN_DIRECTION);
        int eventTimeIndex = cursor.getColumnIndex(TrainsTable.COLUMN_EVENT_TIME);
        int lineIndex = cursor.getColumnIndex(TrainsTable.COLUMN_LINE);
        int nextArrivalIndex = cursor.getColumnIndex(TrainsTable.COLUMN_NEXT_ARRIVAL);
        int stationIndex = cursor.getColumnIndex(TrainsTable.COLUMN_STATION);
        int waitingSecondsIndex = cursor.getColumnIndex(TrainsTable.COLUMN_WAITING_SECONDS);
        int waitingTimeIndex = cursor.getColumnIndex(TrainsTable.COLUMN_WAITING_TIME);

        train.setId(cursor.getLong(idIndex));
        train.setTrainId(cursor.getLong(trainIdIndex));
        train.setDestination(cursor.getString(destinationIndex));
        train.setDirection(cursor.getString(directionIndex));
        train.setEventTime(cursor.getString(eventTimeIndex));
        train.setLine(cursor.getString(lineIndex));
        train.setNextArrival(cursor.getString(nextArrivalIndex));
        train.setStation(cursor.getString(stationIndex));
        train.setWaitingSeconds(cursor.getInt(waitingSecondsIndex));
        train.setWaitingTime(cursor.getString(waitingTimeIndex));

        return train;
    }
}
