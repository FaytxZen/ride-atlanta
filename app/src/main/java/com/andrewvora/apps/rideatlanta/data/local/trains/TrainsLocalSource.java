package com.andrewvora.apps.rideatlanta.data.local.trains;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.trains.TrainsDbContract.TrainsTable;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsLocalSource implements TrainsDataSource {

    private RideAtlantaDbHelper dbHelper;
    private Context context;

    public TrainsLocalSource(@NonNull Context context) {
        dbHelper = new RideAtlantaDbHelper(context);
        this.context = context;
    }

    @Override
    public Observable<List<Train>> getTrains() {
        return Observable.defer(() -> Observable.just(getTrainsFromDatabase()));
    }

    @Override
    public Observable<List<Train>> getFreshTrains() {
        return getTrains();
    }

    private List<Train> getTrainsFromDatabase() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Train> trainList = new ArrayList<>();

        try {
            String[] columns = TrainsTable.getColumns();
            String selection = "1=1";

            Cursor trainsCursor = db.query(TrainsTable.TABLE_NAME,
                    columns, selection, null, null, null, null);
            trainList = getTrainsFrom(trainsCursor);
            trainsCursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return trainList;
    }

	@Override
	public Observable<List<Train>> getTrains(@NonNull String station, @NonNull String line) {
		return Observable.empty();
	}

    @Override
    public Observable<List<Train>> getTrains(@NonNull final String station) {
        return Observable.defer(() -> Observable.just(getTrainsFromDatabase())
                .flatMap((Function<List<Train>, ObservableSource<Train>>) Observable::fromIterable)
                .filter(train -> station.equals(train.getStation()))
                .toList()
                .toObservable());
    }

    @Override
	public Observable<Train> getTrain(@NonNull final Train train) {
		return Observable.defer(() -> Observable.just(getTrainFromDatabase(train)));
	}

    private Train getTrainFromDatabase(@NonNull Train train) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = TrainsTable.getColumns();
            String selection = TrainsTable.COLUMN_TRAIN_ID + "=?";
            String[] selectionArgs = new String[] { train.getTrainId().toString() };
            String numResultsNeeded = "1";

            Cursor trainCursor = db.query(TrainsTable.TABLE_NAME,
                    columns, selection, selectionArgs, null, null, null, numResultsNeeded);

            if(trainCursor.getCount() > 0) {
                trainCursor.moveToFirst();

                return getTrainFrom(trainCursor);
            }
            else {
                train.setWaitingTime(context.getString(R.string.text_adherence_unknown));
                return train;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
			return new Train();
        }
    }

	@Override
	public Observable<Long> deleteAllTrains() {
		return Observable.defer(() -> Observable.just(deleteAllTrainsFromDatabase()));
	}

    private long deleteAllTrainsFromDatabase() {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.delete(TrainsTable.TABLE_NAME, "1=1", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

	@Override
	public Observable<Long> saveTrain(@NonNull final Train route) {
		return Observable.defer(() -> Observable.just(saveTrainInDatabase(route)));
	}

	private long saveTrainInDatabase(@NonNull Train route) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
        contentValues.put(TrainsTable.COLUMN_FAVORITED, route.isFavorited() ? 1 : 0);

        if(newRecord) {
            final long id = db.insertWithOnConflict(TrainsTable.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);

			route.setId(id);

			return id;
        }
        else {
            contentValues.put(TrainsTable._ID, route.getId());

            String whereClause = TrainsTable._ID + "=? and " + TrainsTable.COLUMN_TRAIN_ID + "=?";
            String[] whereArgs = new String[] {
                    route.getId().toString(),
                    route.getTrainId().toString()
            };

            return db.updateWithOnConflict(TrainsTable.TABLE_NAME,
                    contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
        }
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    @Override
    public void reloadTrains() {
        // refreshing handled in the TrainRepo
    }

    private List<Train> getTrainsFrom(Cursor cursor) {
        List<Train> trains = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && !cursor.isAfterLast()) {
            Train train = getTrainFrom(cursor);
            trains.add(train);

            cursor.moveToNext();
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
        int favoritedIndex = cursor.getColumnIndex(TrainsTable.COLUMN_FAVORITED);

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
        train.setFavorited(cursor.getInt(favoritedIndex) == 1);

        return train;
    }
}
