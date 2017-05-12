package com.andrewvora.apps.rideatlanta.notifications;

import android.os.Bundle;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link NotificationsPresenter}.
 *
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsPresenterTest extends BaseUnitTest {

    @Mock private NotificationsDataSource mNotificationRepo;
    @Mock private CachedDataMap mCachedDataMap;
    @Mock private NotificationsContract.View mView;
    private NotificationsContract.Presenter mPresenter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mPresenter = new NotificationsPresenter(mView, mNotificationRepo, mCachedDataMap);
    }

    @Test
    public void onSaveState() throws Exception {
        mPresenter.onSaveState(mock(Bundle.class));
    }

    @Test
    public void onRestoreState() throws Exception {
        mPresenter.onRestoreState(mock(Bundle.class));
    }

    @Test
    public void start() throws Exception {
        mPresenter.start();

        verify(mNotificationRepo).reloadNotifications();
        verify(mNotificationRepo).getNotifications(any(NotificationsDataSource.GetNotificationsCallback.class));
    }

    @Test
    public void stop() throws Exception {
        mPresenter.stop();
    }

    @Test
    public void loadNotifications_hasCachedData() throws Exception {
        when(mCachedDataMap.hasCachedData(NotificationsPresenter.getCachedDataTag()))
                .thenReturn(true);

        mPresenter.loadNotifications();

        verify(mNotificationRepo, never()).reloadNotifications();
        verify(mNotificationRepo).getNotifications(any(NotificationsDataSource.GetNotificationsCallback.class));
    }

    @Test
    public void loadNotifications() throws Exception {
        mPresenter.loadNotifications();

        verify(mNotificationRepo).reloadNotifications();
        verify(mNotificationRepo).getNotifications(any(NotificationsDataSource.GetNotificationsCallback.class));
    }
}