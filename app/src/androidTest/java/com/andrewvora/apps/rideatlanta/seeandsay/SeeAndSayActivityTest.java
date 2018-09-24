package com.andrewvora.apps.rideatlanta.seeandsay;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.andrewvora.apps.rideatlanta.MainActivity;
import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.testing.rules.MockDataTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.andrewvora.apps.rideatlanta.testing.TestHelper.waitForId;
import static com.andrewvora.apps.rideatlanta.testing.actions.RecyclerViewActions.clickViewWithId;

/**
 * Created on 2/19/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4.class)
public class SeeAndSayActivityTest {
	@ClassRule
	public static MockDataTestRule mockDataTestRule = new MockDataTestRule();
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

	@Before
	public void beforeEveryTest() {
		activityTestRule.launchActivity(new Intent(Intent.ACTION_MAIN));
	}

	@Test
	public void getToSeeAndSayFromOverflowMenu() {
		openActionBarOverflowOrOptionsMenu(activityTestRule.getActivity());
		onView(withText(R.string.menu_report_incident)).perform(click());
		assertSeeAndSayIsDisplayed();
	}

	@Test
	public void getToSeeAndSayFromHome() {
		onView(withId(R.id.tab_home)).perform(click());
		onView(isRoot()).perform(waitForId(R.id.info_action_button));
		onView(withId(R.id.home_recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(2, clickViewWithId(R.id.info_action_button)));

		assertSeeAndSayIsDisplayed();
	}

	private void assertSeeAndSayIsDisplayed() {
		onView(withId(R.id.see_say_text_police)).check(matches(isDisplayed()));
		onView(withId(R.id.see_say_call_police)).check(matches(isDisplayed()));
	}
}