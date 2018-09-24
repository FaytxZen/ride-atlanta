package com.andrewvora.apps.rideatlanta.trains;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.andrewvora.apps.rideatlanta.MainActivity;
import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.testing.FreshDatabaseHelper;
import com.andrewvora.apps.rideatlanta.testing.rules.MockDataTestRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.andrewvora.apps.rideatlanta.testing.TestHelper.waitForId;
import static com.andrewvora.apps.rideatlanta.testing.assertions.RecyclerViewAssertions.hasHolderItemAtPosition;
import static com.andrewvora.apps.rideatlanta.testing.assertions.RecyclerViewAssertions.hasItemsCount;
import static com.andrewvora.apps.rideatlanta.testing.matchers.RecyclerViewMatchers.hasVisibleViewWithId;
import static org.junit.Assert.assertTrue;

/**
 * Created on 2/19/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4.class)
public class TrainRoutesTest {

	@ClassRule
	public static MockDataTestRule mockDataTestRule = new MockDataTestRule();

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

	@Before
	public void beforeEachTest() {
		activityTestRule.launchActivity(new Intent(Intent.ACTION_MAIN));
		onView(withId(R.id.tab_trains)).perform(click());
	}

	@AfterClass
	public static void cleanUp() {
		new FreshDatabaseHelper().cleanDatabase();
	}

	@Test
	public void listIsScrollable() {
		onView(isRoot()).perform(waitForId(R.id.train_destination_text_view));
		onView(withId(R.id.trains_list)).perform(swipeUp(), swipeUp());

		final RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.trains_list);
		final LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
		assertTrue(llm.findFirstVisibleItemPosition() != 0);
	}

	@Test
	public void openDetails() {
		onView(isRoot()).perform(waitForId(R.id.train_destination_text_view));
		onView(withId(R.id.trains_list))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

		onView(isRoot()).perform(waitForId(R.id.route_name));
		onView(withId(R.id.train_details_recycler_view))
				.check(hasItemsCount(1))
				.check(hasHolderItemAtPosition(0, hasVisibleViewWithId(R.id.route_destination)))
				.check(hasHolderItemAtPosition(0, hasVisibleViewWithId(R.id.arrival_time_text_view)))
				.check(hasHolderItemAtPosition(0, hasVisibleViewWithId(R.id.time_til_text_view)))
				.check(hasHolderItemAtPosition(0, hasVisibleViewWithId(R.id.direction_text_view)));
	}
}