package com.andrewvora.apps.rideatlanta.data.repos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.local.notifications.NotificationsLocalSource;
import com.andrewvora.apps.rideatlanta.data.remote.notifications.NotificationsRemoteSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class NotificationsRepo implements NotificationsDataSource {

    private static NotificationsRepo mInstance;

    private Map<String, Notification> mCachedNotifications;
    private NotificationsDataSource mLocalSource;
    private NotificationsDataSource mRemoteSource;
    private boolean mCacheIsDirty;

    private NotificationsRepo(@NonNull NotificationsDataSource localSource,
                              @NonNull NotificationsDataSource remoteSource)
    {
        mLocalSource = localSource;
        mRemoteSource = remoteSource;
    }

    public static NotificationsRepo getInstance(@NonNull Context context) {
        if(mInstance == null) {
            NotificationsDataSource localSource = NotificationsLocalSource.getInstance(context);
            NotificationsDataSource remoteSource = NotificationsRemoteSource.getInstance();

            mInstance = new NotificationsRepo(localSource, remoteSource);
        }

        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    @Override
    public void getNotifications(@NonNull final GetNotificationsCallback callback) {
        if(mCachedNotifications != null && !mCacheIsDirty) {
            callback.onFinished(new ArrayList<>(mCachedNotifications.values()));
        }
        else if(mCacheIsDirty) {
            getNotificationsFromRemote(callback);
        }
        else {
            mLocalSource.getNotifications(new GetNotificationsCallback() {
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
    }

    @Override
    public void deleteAllNotifications() {
        mLocalSource.deleteAllNotifications();
        mRemoteSource.deleteAllNotifications();

        mCachedNotifications = checkNotNull(mCachedNotifications);
        mCachedNotifications.clear();
    }

    @Override
    public void saveNotification(@NonNull Notification notification) {
        try {
            mLocalSource.saveNotification(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadNotifications() {
        mCacheIsDirty = true;
    }

    private void getNotificationsFromRemote(@NonNull final GetNotificationsCallback callback) {
        mRemoteSource.getNotifications(new GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
                reloadCachedNotifications(notifications);
                reloadLocalNotifications(notifications);

                callback.onFinished(notifications);
            }

            @Override
            public void onError(Object error) {
                callback.onError(error);
            }
        });
    }

    private void reloadCachedNotifications(@NonNull List<Notification> notifications) {

        mCachedNotifications = checkNotNull(mCachedNotifications);
        mCachedNotifications.clear();

        for(Notification notification : notifications) {
            cacheNotification(notification);
        }

        mCacheIsDirty = false;
    }

    private void reloadLocalNotifications(@NonNull List<Notification> notifications) {

        mLocalSource.deleteAllNotifications();

        for(Notification notification : notifications) {
            mLocalSource.saveNotification(notification);
        }
    }

    private void cacheNotification(Notification notification) {
        mCachedNotifications = checkNotNull(mCachedNotifications);
        mCachedNotifications.put(notification.getNotificationId(), notification);
    }

    private Map<String, Notification> checkNotNull(@Nullable Map<String, Notification> map)
    {
        if(map == null) {
            map = new LinkedHashMap<>();
        }

        return map;
    }
}
