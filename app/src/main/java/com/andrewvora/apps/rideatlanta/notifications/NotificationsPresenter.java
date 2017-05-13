package com.andrewvora.apps.rideatlanta.notifications;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsPresenter implements NotificationsContract.Presenter {

    private NotificationsContract.View mView;
    private NotificationsDataSource mNotificationRepo;

    public NotificationsPresenter(@NonNull NotificationsContract.View view,
                                  @NonNull NotificationsDataSource notificationsRepo)
    {
        mView = view;
        mNotificationRepo = notificationsRepo;
    }

    @Override
    public void onSaveState(Bundle outState) { }

    @Override
    public void onRestoreState(Bundle savedState) { }

    @Override
    public void start() {
        loadNotifications();
    }

    @Override
    public void stop() {

    }

    @Override
    public void refreshNotifications() {
        mNotificationRepo.reloadNotifications();

        loadNotifications();
    }

    @Override
    public void loadNotifications() {
        useCachedDataIfAvailable();

        mNotificationRepo.getNotifications(new NotificationsDataSource.GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
                mView.onNotificationsLoaded(notifications);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void useCachedDataIfAvailable() {
        if(!hasCachedData()) {
            mNotificationRepo.reloadNotifications();
        }
    }

    private boolean hasCachedData() {
        return mNotificationRepo.hasCachedData();
    }
}
