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

import io.reactivex.Observable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsRepo implements NotificationsDataSource {

    @NonNull private Map<String, Notification> cachedNotifications;
    @NonNull private NotificationsDataSource remoteSource;

    private boolean cacheIsDirty;

    public NotificationsRepo(@NonNull NotificationsDataSource remoteSource) {
        this.remoteSource = remoteSource;

        cachedNotifications = new ConcurrentHashMap<>();
    }

    @Override
    public boolean hasCachedData() {
        return !cachedNotifications.isEmpty();
    }

	@Override
	public Observable<List<Notification>> getNotifications() {
    	return Observable.defer(() -> {
		    if(cachedNotifications.isEmpty() || cacheIsDirty) {
			    return getNotificationsFromRemote();
		    } else {
			    return Observable.just(getCachedNotifications());
		    }
	    });
	}

    private List<Notification> getCachedNotifications() {
		List<Notification> notifications = new ArrayList<>(cachedNotifications.values());
		Collections.sort(notifications, new NotificationComparator());

		return notifications;
	}

	@Override
	public Observable<List<Notification>> getFreshNotifications() {
		return Observable.defer(this::getNotificationsFromRemote);
	}

	@Override
    public Observable<Long> deleteAllNotifications() {
        remoteSource.deleteAllNotifications();
        cachedNotifications.clear();

		return Observable.just(0L);
    }

    @Override
    public Observable<Long> saveNotification(@NonNull Notification notification) {
		return Observable.just(0L);
    }

    @Override
    public void reloadNotifications() {
        cacheIsDirty = true;
    }

    private Observable<List<Notification>> getNotificationsFromRemote() {
        return remoteSource.getNotifications()
		        .map(notifications -> {
			        reloadCachedNotifications(notifications);
			        return notifications;
		        });
    }

    private void reloadCachedNotifications(@NonNull List<Notification> notifications) {

        cachedNotifications.clear();

        for(Notification notification : notifications) {
            cacheNotification(notification);
        }

        cacheIsDirty = false;
    }

    private void cacheNotification(Notification notification) {
        cachedNotifications.put(notification.getNotificationId(), notification);
    }

	private static class NotificationComparator implements Comparator<Notification> {
		@Override
		public int compare(Notification n1, Notification n2) {
			long n1Time = DateHelper.getInstance()
					.getTimeAsMilliseconds(n1.getTimeStamp(), DateHelper.TWITTER_TIME_STAMP_FORMAT);
			long n2Time = DateHelper.getInstance()
					.getTimeAsMilliseconds(n2.getTimeStamp(), DateHelper.TWITTER_TIME_STAMP_FORMAT);

			return Long.compare(n2Time, n1Time);
		}
	}
}
