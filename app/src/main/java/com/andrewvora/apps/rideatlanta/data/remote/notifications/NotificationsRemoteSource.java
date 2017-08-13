package com.andrewvora.apps.rideatlanta.data.remote.notifications;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
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

    private static final long TWITTER_USER_ID = 28942750L;
    private static final int MAX_TWEETS = 50;

    private TwitterApiClient twitterClient;

    public NotificationsRemoteSource(TwitterApiClient twitterClient) {
        this.twitterClient = twitterClient;
    }

    @Override
    public void getNotifications(@NonNull final GetNotificationsCallback callback) {
        StatusesService statusesService = twitterClient.getStatusesService();
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
                    notification.setNotificationId(String.valueOf(tweet.getId()));
                    notification.setPostedAt(tweet.createdAt);
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
    public void getFreshNotifications(@NonNull GetNotificationsCallback callback) {
        getNotifications(callback);
    }

    @Override
    public boolean hasCachedData() {
        return false;
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
