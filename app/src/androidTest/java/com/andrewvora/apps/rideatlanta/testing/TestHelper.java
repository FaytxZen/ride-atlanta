package com.andrewvora.apps.rideatlanta.testing;

import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnegative;

import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created on 2/11/2018.
 * @author Andrew Vorakrajangthiti
 */
public abstract class TestHelper {
	private static final long DEFAULT_WAIT_TIME_MS = TimeUnit.SECONDS.toMillis(5);

	public static void sleep(@Nonnegative long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ViewAction waitForId(@IdRes final int viewId) {
		return waitForId(viewId, DEFAULT_WAIT_TIME_MS);
	}

	public static ViewAction waitForId(@IdRes final int viewId, @IntRange(from=1) final long millis) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isRoot();
			}

			@Override
			public String getDescription() {
				return "waiting for a view with id <" + viewId + "> for " + millis + " millis.";
			}

			@Override
			public void perform(final UiController uiController, final View view) {
				uiController.loopMainThreadUntilIdle();
				final long startTime = System.currentTimeMillis();
				final long endTime = startTime + millis;
				final Matcher<View> viewMatcher = withId(viewId);

				do {
					for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
						// found view with required ID
						if (viewMatcher.matches(child)) {
							return;
						}
					}

					uiController.loopMainThreadForAtLeast(50);
				}
				while (System.currentTimeMillis() < endTime);

				// timeout happens
				throw new PerformException.Builder()
						.withActionDescription(this.getDescription())
						.withViewDescription(HumanReadables.describe(view))
						.withCause(new TimeoutException())
						.build();
			}
		};
	}
}
