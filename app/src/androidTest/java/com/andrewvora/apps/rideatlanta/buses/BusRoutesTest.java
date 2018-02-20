package com.andrewvora.apps.rideatlanta.buses;

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
public class BusRoutesTest {
	@ClassRule
	public static MockDataTestRule mockDataTestRule = new MockDataTestRule();

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

	@Before
	public void beforeEachTest() {
		activityTestRule.launchActivity(new Intent(Intent.ACTION_MAIN));
		onView(withId(R.id.tab_buses)).perform(click());
	}

	@AfterClass
	public static void cleanUp() {
		new FreshDatabaseHelper().cleanDatabase();
	}

	@Test
	public void listIsScrollable() {
		onView(isRoot()).perform(waitForId(R.id.bus_destination_text_view));
		onView(withId(R.id.buses_list)).perform(swipeUp(), swipeUp());

		final RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.buses_list);
		final LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
		assertTrue(llm.findFirstVisibleItemPosition() != 0);
	}

	@Test
	public void openDetails() {
		onView(isRoot()).perform(waitForId(R.id.bus_destination_text_view));
		onView(withId(R.id.buses_list))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

		onView(isRoot()).perform(waitForId(R.id.route_name));
		onView(withId(R.id.bus_details_recycler_view))
				.check(hasItemsCount(2));

		assertBusDetailViewsAreDisplayed(0);
		assertBusDetailViewsAreDisplayed(1);
	}

	private void assertBusDetailViewsAreDisplayed(int position) {
		onView(withId(R.id.bus_details_recycler_view))
				.check(hasHolderItemAtPosition(position, hasVisibleViewWithId(R.id.route_destination)))
				.check(hasHolderItemAtPosition(position, hasVisibleViewWithId(R.id.route_time_until_arrival)))
				.check(hasHolderItemAtPosition(position, hasVisibleViewWithId(R.id.route_direction)))
				.check(hasHolderItemAtPosition(position, hasVisibleViewWithId(R.id.route_name)));
	}
}