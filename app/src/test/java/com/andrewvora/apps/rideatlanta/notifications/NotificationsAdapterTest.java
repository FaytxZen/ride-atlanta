package com.andrewvora.apps.rideatlanta.notifications;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.models.Notification;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * {@link NotificationsAdapter}.
 *
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class NotificationsAdapterTest extends BaseUnitTest {

    private NotificationsAdapter mAdapter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        List<Notification> notifications = new ArrayList<>();
        mAdapter = new NotificationsAdapter(notifications);
    }

    @Test
    public void getItemCount() throws Exception {
        mAdapter.getNotifications().add(new Notification());

        assertEquals(1, mAdapter.getItemCount());
    }

    @Test
    public void getItemCount_empty() throws Exception {
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void setNotifications() throws Exception {
        List<Notification> notifications = new ArrayList<>();

        mAdapter.setNotifications(notifications);

        assertEquals(notifications, mAdapter.getNotifications());
    }

}