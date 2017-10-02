package com.andrewvora.apps.rideatlanta.data.contracts;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface NotificationsDataSource {

    Observable<List<Notification>> getFreshNotifications();
    Observable<List<Notification>> getNotifications();
    Observable<Long> deleteAllNotifications();
    Observable<Long> saveNotification(@NonNull Notification notification);

    void reloadNotifications();
    boolean hasCachedData();
}
