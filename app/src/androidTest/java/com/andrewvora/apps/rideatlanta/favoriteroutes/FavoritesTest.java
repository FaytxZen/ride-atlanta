package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.andrewvora.apps.rideatlanta.testing.TestHelper.waitForId;
import static com.andrewvora.apps.rideatlanta.testing.actions.RecyclerViewActions.clickViewWithId;
import static com.andrewvora.apps.rideatlanta.testing.assertions.RecyclerViewAssertions.hasHolderItemAtPosition;
import static com.andrewvora.apps.rideatlanta.testing.assertions.RecyclerViewAssertions.hasItemsCount;
import static com.andrewvora.apps.rideatlanta.testing.matchers.RecyclerViewMatchers.hasViewWithTextWithId;

/**
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4.class)
public class FavoritesTest {

	@ClassRule
	public static MockDataTestRule mockDataTestRule = new MockDataTestRule();
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void beforeEveryTest() {
    	activityTestRule.launchActivity(new Intent(Intent.ACTION_MAIN));
    	onView(withId(R.id.tab_fav_routes)).perform(click());
    }

    @AfterClass
    public static void cleanUp() {
	    new FreshDatabaseHelper().cleanDatabase();
    }

    @Test
	public void favoritesHaveEmptyStateByDefault() {
    	onView(withId(R.id.no_favorited_routes_view)).check(matches(isDisplayed()));
    }

    @Test
	public void addingOrRemovingFavorites() {
    	// favorite a bus
		onView(withId(R.id.tab_buses)).perform(click());
	    onView(isRoot()).perform(waitForId(R.id.bus_favorite_button));
	    onView(withId(R.id.buses_list)).perform(
	    		actionOnItemAtPosition(0, clickViewWithId(R.id.bus_favorite_button)));

		// favorite a train
	    onView(withId(R.id.tab_trains)).perform(click());
	    onView(isRoot()).perform(waitForId(R.id.train_favorite_button));
	    onView(withId(R.id.trains_list))
			    .perform(actionOnItemAtPosition(0, clickViewWithId(R.id.train_favorite_button)));

	    // go back to favorites
	    onView(withId(R.id.tab_fav_routes)).perform(click());
	    onView(isRoot()).perform(waitForId(R.id.favorite_route_row_item));
	    onView(withId(R.id.favorite_routes_recycler_view)).check(hasItemsCount(2));

	    // unfavorite items
	    onView(withId(R.id.favorite_routes_recycler_view))
			    .perform(actionOnItemAtPosition(1, clickViewWithId(R.id.favorite_button)))
	            .check(hasItemsCount(1))
			    .perform(actionOnItemAtPosition(0, clickViewWithId(R.id.favorite_button)))
	            .check(hasItemsCount(0));
	    onView(withId(R.id.no_favorited_routes_view)).check(matches(isDisplayed()));
    }

    @Test
	public void allInformationShouldBeSetOnRoute() {
	    // favorite a bus
	    onView(withId(R.id.tab_buses)).perform(click());
	    onView(isRoot()).perform(waitForId(R.id.bus_favorite_button));
	    onView(withId(R.id.buses_list)).perform(
			    actionOnItemAtPosition(0, clickViewWithId(R.id.bus_favorite_button)));

	    // favorite a train
	    onView(withId(R.id.tab_trains)).perform(click());
	    onView(isRoot()).perform(waitForId(R.id.train_favorite_button));
	    onView(withId(R.id.trains_list))
			    .perform(actionOnItemAtPosition(0, clickViewWithId(R.id.train_favorite_button)));

	    // go back to favorites
	    onView(withId(R.id.tab_fav_routes)).perform(click());
	    onView(isRoot()).perform(waitForId(R.id.route_destination));
	    verifyViewsAreSetFor(0);
	    verifyViewsAreSetFor(1);
    }

    private void verifyViewsAreSetFor(final int position) {
	    onView(withId(R.id.favorite_routes_recycler_view))
			    .check(hasHolderItemAtPosition(position, hasViewWithTextWithId(R.id.route_direction)))
			    .check(hasHolderItemAtPosition(position, hasViewWithTextWithId(R.id.route_destination)))
			    .check(hasHolderItemAtPosition(position, hasViewWithTextWithId(R.id.route_time_until_arrival)));
    }
}
