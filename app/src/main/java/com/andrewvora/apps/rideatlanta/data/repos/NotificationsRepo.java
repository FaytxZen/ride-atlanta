package com.andrewvora.apps.rideatlanta.data.repos;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.DateHelper;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsRepo implements NotificationsDataSource {

    @NonNull private Map<String, Notification> cachedNotifications;
    @NonNull private NotificationsDataSource remoteSource;

    private boolean mCacheIsDirty;

    public NotificationsRepo(@NonNull NotificationsDataSource remoteSource) {
        this.remoteSource = remoteSource;

        cachedNotifications = new ConcurrentHashMap<>();
    }

    @Override
    public boolean hasCachedData() {
        return !cachedNotifications.isEmpty();
    }

    @Override
    public void getNotifications(@NonNull final GetNotificationsCallback callback) {
        if(!cachedNotifications.isEmpty() && !mCacheIsDirty) {
            callback.onFinished(getCachedNotifications());
        }
        else {
            getNotificationsFromRemote(callback);
        }
    }

    private List<Notification> getCachedNotifications() {
		List<Notification> notifications = new ArrayList<>(cachedNotifications.values());
		Collections.sort(notifications, new NotificationComparator());

		return notifications;
	}

	@Override
	public void getFreshNotifications(@NonNull GetNotificationsCallback callback) {
		getNotificationsFromRemote(callback);
	}

	@Override
    public void deleteAllNotifications() {
        remoteSource.deleteAllNotifications();

        cachedNotifications.clear();
    }

    @Override
    public void saveNotification(@NonNull Notification notification) {

    }

    @Override
    public void reloadNotifications() {
        mCacheIsDirty = true;
    }

    private void getNotificationsFromRemote(@NonNull final GetNotificationsCallback callback) {
        remoteSource.getNotifications(new GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
                reloadCachedNotifications(notifications);

                callback.onFinished(notifications);
            }

            @Override
            public void onError(Object error) {
                callback.onError(error);
            }
        });
    }

    private void reloadCachedNotifications(@NonNull List<Notification> notifications) {

        cachedNotifications.clear();

        for(Notification notification : notifications) {
            cacheNotification(notification);
        }

        mCacheIsDirty = false;
    }

    private void cacheNotification(Notification notification) {
        cachedNotifications.put(notification.getNotificationId(), notification);
    }

	static class NotificationComparator implements Comparator<Notification> {
		@Override
		public int compare(Notification n1, Notification n2) {
			long n1Time = DateHelper.getInstance()
					.getTimeAsMilliseconds(n1.getTimeStamp(), DateHelper.TWITTER_TIME_STAMP_FORMAT);
			long n2Time = DateHelper.getInstance()
					.getTimeAsMilliseconds(n2.getTimeStamp(), DateHelper.TWITTER_TIME_STAMP_FORMAT);

			if(n1Time < n2Time) {
				return 1;
			}
			else if(n1Time > n2Time) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
}
