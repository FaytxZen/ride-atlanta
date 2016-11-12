package com.andrewvora.apps.rideatlanta.data.local.notifications;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.models.Notification;
import com.andrewvora.apps.rideatlanta.data.NotificationsDataSource;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class NotificationsLocalSource implements NotificationsDataSource {

    private static NotificationsLocalSource mInstance;

    private NotificationsLocalSource(@NonNull Context context) {

    }

    public static NotificationsLocalSource getInstance(@NonNull Context context) {
        if(mInstance == null) {
            mInstance = new NotificationsLocalSource(context);
        }

        return mInstance;
    }

    @Override
    public void getNotifications(@NonNull GetNotificationsCallback callback) {

    }

    @Override
    public void deleteAllNotifications() {

    }

    @Override
    public void saveNotification(@NonNull Notification notification) {

    }

    @Override
    public void reloadNotifications() {

    }
}
