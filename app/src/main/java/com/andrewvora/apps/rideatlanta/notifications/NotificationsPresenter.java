package com.andrewvora.apps.rideatlanta.notifications;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsPresenter implements NotificationsContract.Presenter {

    private NotificationsContract.View view;
    private NotificationsDataSource notificationRepo;
    private CompositeDisposable disposables;

    public NotificationsPresenter(@NonNull NotificationsContract.View view,
                                  @NonNull NotificationsDataSource notificationsRepo)
    {
        this.view = view;
        this.notificationRepo = notificationsRepo;
		this.disposables = new CompositeDisposable();
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
		disposables.dispose();
		disposables.clear();
    }

    @Override
    public void refreshNotifications() {
        notificationRepo.reloadNotifications();

        loadNotifications();
    }

    @Override
    public void loadNotifications() {
        useCachedDataIfAvailable();

        notificationRepo.getNotifications(new NotificationsDataSource.GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
				view.onNotificationsLoaded(notifications);
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void useCachedDataIfAvailable() {
        if(!hasCachedData()) {
            notificationRepo.reloadNotifications();
        }
    }

    private boolean hasCachedData() {
        return notificationRepo.hasCachedData();
    }
}
