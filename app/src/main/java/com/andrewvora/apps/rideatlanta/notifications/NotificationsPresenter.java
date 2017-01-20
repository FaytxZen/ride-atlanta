package com.andrewvora.apps.rideatlanta.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.repos.NotificationsRepo;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class NotificationsPresenter implements NotificationsContract.Presenter {

    private Context mContext;
    private NotificationsContract.View mView;

    public NotificationsPresenter(@NonNull Context context,
                                  @NonNull NotificationsContract.View view)
    {
        mContext = context;
        mView = view;
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {

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
    public void loadNotifications() {
        NotificationsRepo repo = NotificationsRepo.getInstance(mContext);

        useCachedDataIfAvailable();

        repo.getNotifications(new NotificationsDataSource.GetNotificationsCallback() {
            @Override
            public void onFinished(List<Notification> notifications) {
                mView.onNotificationsLoaded(notifications);
                makeCachedDataAvailable();
            }

            @Override
            public void onError(Object error) {

            }
        });
    }

    private void useCachedDataIfAvailable() {
        if(!hasCachedData()) {
            NotificationsRepo.getInstance(mContext).reloadNotifications();
        }
    }

    private boolean hasCachedData() {
        return CachedDataMap.getInstance().hasCachedData(getCachedDataTag());
    }

    private void makeCachedDataAvailable() {
        CachedDataMap.getInstance().put(getCachedDataTag(), true);
    }

    private String getCachedDataTag() {
        return NotificationsPresenter.class.getSimpleName();
    }
}
