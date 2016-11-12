package com.andrewvora.apps.rideatlanta.data.remote.notifications;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.models.Notification;
import com.andrewvora.apps.rideatlanta.data.NotificationsDataSource;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class NotificationsRemoteSource implements NotificationsDataSource {

    private static final long TWITTER_USER_ID = 14944459L;
    private static final int MAX_TWEETS = 50;

    private TwitterApiClient mTwitterApiClient;

    private NotificationsRemoteSource() {
        mTwitterApiClient = TwitterCore.getInstance().getApiClient();
    }

    public static synchronized NotificationsRemoteSource getInstance() {
        return new NotificationsRemoteSource();
    }

    @Override
    public void getNotifications(@NonNull final GetNotificationsCallback callback) {
        StatusesService statusesService = mTwitterApiClient.getStatusesService();
        final Call<List<Tweet>> call = statusesService.userTimeline(TWITTER_USER_ID,
                null,
                MAX_TWEETS,
                null,
                null,
                null,
                false,
                null,
                false);

        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {

                List<Notification> notifications = new ArrayList<>();
                List<Tweet> tweets = result.data;

                for(Tweet tweet : tweets) {
                    Notification notification = new Notification();
                    notification.setId(String.valueOf(tweet.getId()));
                    notification.setDate(tweet.createdAt);
                    notification.setMessage(tweet.text);

                    notifications.add(notification);
                }

                callback.onFinished(notifications);
            }

            @Override
            public void failure(TwitterException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void deleteAllNotifications() {
        // remote notifications are read-only
    }

    @Override
    public void saveNotification(@NonNull Notification notification) {
        // remote notifications are read-only
    }

    @Override
    public void reloadNotifications() {
        // reloading is handled in the NotificationsRepo
    }
}
