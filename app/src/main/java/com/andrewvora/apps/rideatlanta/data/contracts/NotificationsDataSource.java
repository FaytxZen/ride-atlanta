package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface NotificationsDataSource {

    interface GetNotificationsCallback {
        void onFinished(List<Notification> notifications);
        void onError(Object error);
    }

    void getNotifications(@NonNull GetNotificationsCallback callback);
    void deleteAllNotifications();
    void saveNotification(@NonNull Notification notification);
    void reloadNotifications();

    boolean hasCachedData();
}
