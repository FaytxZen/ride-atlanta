package com.andrewvora.apps.rideatlanta.data.local.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;
import com.andrewvora.apps.rideatlanta.data.local.notifications.NotificationsDbContract.NotificationsTable;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsLocalSource implements NotificationsDataSource {

    private RideAtlantaDbHelper dbHelper;

    private NotificationsLocalSource(@NonNull Context context) {
        dbHelper = new RideAtlantaDbHelper(context);
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    @Override
    public Observable<List<Notification>> getFreshNotifications() {
        return getNotifications();
    }

    @Override
    public Observable<List<Notification>> getNotifications() {
        return Observable.defer(new Callable<ObservableSource<? extends List<Notification>>>() {
			@Override
			public ObservableSource<List<Notification>> call() throws Exception {
				return Observable.just(getNotificationsFromDatabase());
			}
		});
    }

    private List<Notification> getNotificationsFromDatabase() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String[] columns = NotificationsTable.getColumns();
		String selection = "1=1";

		try {
			Cursor notificationsCursor = db.query(NotificationsTable.TABLE_NAME,
					columns, selection, null, null, null, null);

			return getNotificationsFrom(notificationsCursor);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

    @Override
    public Observable<Long> deleteAllNotifications() {
       return Observable.defer(new Callable<ObservableSource<? extends Long>>() {
		   @Override
		   public ObservableSource<Long> call() throws Exception {
			   return Observable.just(deleteAllNotificationsFromDatabase());
		   }
	   });
    }

    private long deleteAllNotificationsFromDatabase() {
		try {
			return dbHelper.getWritableDatabase()
					.delete(NotificationsTable.TABLE_NAME, "1=1", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1L;
	}

	@Override
	public Observable<Long> saveNotification(@NonNull Notification notification) {
		return Observable.just(saveNotificationInDatabase(notification));
	}

    private long saveNotificationInDatabase(@NonNull Notification notification) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		final boolean isNewRecord = notification.getId() == null;

		ContentValues contentValues = new ContentValues();
		contentValues.put(NotificationsTable.COLUMN_NOTIFICATION_ID,
				notification.getNotificationId());
		contentValues.put(NotificationsTable.COLUMN_MESSAGE, notification.getMessage());
		contentValues.put(NotificationsTable.COLUMN_ALERT_DATE, notification.getPostedAt());

		if(isNewRecord) {
			return db.insertWithOnConflict(NotificationsTable.TABLE_NAME,
					null, contentValues, SQLiteDatabase.CONFLICT_ROLLBACK);
		}
		else {
			contentValues.put(NotificationsTable._ID, notification.getId());

			String whereClause = NotificationsTable._ID + "=? and " +
					NotificationsTable.COLUMN_NOTIFICATION_ID + "=?";
			String[] whereArgs = new String[] {
					notification.getId().toString(),
					notification.getNotificationId()
			};

			return db.updateWithOnConflict(NotificationsTable.TABLE_NAME,
					contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
		}
    }

    @Override
    public void reloadNotifications() {
        // handled in the NotificationsRepo class
    }

    private List<Notification> getNotificationsFrom(@NonNull Cursor cursor) {
        List<Notification> notifications = new ArrayList<>();
        boolean hasRecords = cursor.moveToFirst();

        while(hasRecords && !cursor.isAfterLast()) {
            Notification notification = getNotificationFrom(cursor);
            notifications.add(notification);

            cursor.moveToNext();
        }

        return notifications;
    }

    private Notification getNotificationFrom(@NonNull Cursor cursor) {
        Notification notification = new Notification();

        int idIndex = cursor.getColumnIndex(NotificationsTable._ID);
        int notificationIdIndex = cursor.getColumnIndex(NotificationsTable.COLUMN_NOTIFICATION_ID);
        int messageIndex = cursor.getColumnIndex(NotificationsTable.COLUMN_MESSAGE);
        int alertDateIndex = cursor.getColumnIndex(NotificationsTable.COLUMN_ALERT_DATE);

        notification.setId(cursor.getLong(idIndex));
        notification.setNotificationId(cursor.getString(notificationIdIndex));
        notification.setMessage(cursor.getString(messageIndex));
        notification.setPostedAt(cursor.getString(alertDateIndex));

        return notification;
    }
}
