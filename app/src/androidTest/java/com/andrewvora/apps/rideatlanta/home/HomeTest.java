package com.andrewvora.apps.rideatlanta.home;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.andrewvora.apps.rideatlanta.testing.TestHelper.waitForId;
import static com.andrewvora.apps.rideatlanta.testing.actions.RecyclerViewActions.clickViewWithId;
import static com.andrewvora.apps.rideatlanta.testing.assertions.RecyclerViewAssertions.hasHolderItemAtPosition;
import static com.andrewvora.apps.rideatlanta.testing.assertions.RecyclerViewAssertions.hasItemsCount;
import static com.andrewvora.apps.rideatlanta.testing.matchers.RecyclerViewMatchers.hasViewWithText;
import static com.andrewvora.apps.rideatlanta.testing.matchers.RecyclerViewMatchers.hasVisibleViewWithId;

/**
 * {@link HomeFragment}
 *
 * Created on 2/10/2018.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4.class)
public class HomeTest {

	@ClassRule public static MockDataTestRule mockDataTestRule = new MockDataTestRule();
	@Rule public ActivityTestRule<MainActivity> activityTestRule =
			new ActivityTestRule<>(MainActivity.class, false, false);

	@Before
	public void beforeEveryTest() {
		activityTestRule.launchActivity(new Intent(Intent.ACTION_MAIN));

		// switch between tabs to reload data
		onView(withId(R.id.tab_home)).perform(click());
	}

	@AfterClass
	public static void cleanUp() {
		new FreshDatabaseHelper().cleanDatabase();
	}

	@Test
	public void displaysNotifications() {
		onView(isRoot()).perform(waitForId(R.id.alert_message));
		onView(withId(R.id.home_recycler_view))
				.check(hasHolderItemAtPosition(0, hasVisibleViewWithId(R.id.alert_message)))
				.check(hasHolderItemAtPosition(1, hasVisibleViewWithId(R.id.alert_message)));
	}

	@Test
	public void displaysSeeAndSayMessage() {
		final String seeAndSayMsg = InstrumentationRegistry.getTargetContext().getString(R.string.text_see_and_say);
		onView(isRoot()).perform(waitForId(R.id.info_message));
		final int itemCount = ((RecyclerView) activityTestRule.getActivity().findViewById(R.id.home_recycler_view))
				.getAdapter()
				.getItemCount();

		onView(withId(R.id.home_recycler_view))
				.check(hasHolderItemAtPosition(itemCount - 1, hasViewWithText(seeAndSayMsg)));
	}

	@Test
	public void displaysFavoritedRoutes() {
		// go to train tab
		onView(withId(R.id.tab_buses)).perform(click());
		onView(isRoot()).perform(waitForId(R.id.bus_favorite_button));
		onView(withId(R.id.buses_list)).perform(RecyclerViewActions.actionOnItemAtPosition(
				0,
				clickViewWithId(R.id.bus_favorite_button)));

		// go to home tab
		onView(withId(R.id.tab_home)).perform(click());
		onView(isRoot()).perform(waitForId(R.id.route_destination));

		// check for favorited route
		onView(withId(R.id.home_recycler_view))
				.check(hasItemsCount(4))
				.check(hasHolderItemAtPosition(2, hasVisibleViewWithId(R.id.route_destination)));
	}
}
