package com.andrewvora.apps.rideatlanta.notifications;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface NotificationsContract {

    interface Presenter extends BasePresenter {
        void loadNotifications();
    }

    interface View extends BaseView<NotificationsContract.Presenter> {
        void onNotificationsLoaded(List<Notification> notificationList);
    }
}
