package com.andrewvora.apps.rideatlanta.data.remote.notifications;

import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.data.contracts.NotificationsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Notification;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

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
    public Observable<List<Notification>> getNotifications() {
        return Observable.defer(new Callable<ObservableSource<? extends List<Notification>>>() {
			@Override
			public ObservableSource<List<Notification>> call() throws Exception {
				return Observable.just(getNotificationsFromClient());
			}
		});
    }

    private List<Notification> getNotificationsFromClient() {
        final StatusesService statusesService = twitterClient.getStatusesService();

		try {
			final List<Tweet> tweets = statusesService.userTimeline(TWITTER_USER_ID,
					null,
					MAX_TWEETS,
					null,
					null,
					null,
					false,
					null,
					false)
					.execute()
					.body();

			final List<Notification> notifications = new ArrayList<>();

			if (tweets != null) {
				for(Tweet tweet : tweets) {
					Notification notification = new Notification();
					notification.setNotificationId(String.valueOf(tweet.getId()));
					notification.setPostedAt(tweet.createdAt);
					notification.setMessage(tweet.text);

					notifications.add(notification);
				}
			}

			return notifications;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return new ArrayList<>();
    }

    @Override
    public Observable<List<Notification>> getFreshNotifications() {
        return getNotifications();
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

	@Override
	public Observable<Long> deleteAllNotifications() {
		return Observable.just(1L);
	}

	@Override
	public Observable<Long> saveNotification(@NonNull Notification notification) {
		return Observable.just(1L);
	}

	@Override
    public void reloadNotifications() {
        // reloading is handled in the NotificationsRepo
    }
}
