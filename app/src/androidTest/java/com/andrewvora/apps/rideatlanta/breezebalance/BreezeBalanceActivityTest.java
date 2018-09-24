package com.andrewvora.apps.rideatlanta.breezebalance;

import android.content.Intent;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.andrewvora.apps.rideatlanta.MainActivity;
import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.testing.TestHelper;
import com.andrewvora.apps.rideatlanta.testing.rules.MockDataTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;

/**
 * Created on 2/18/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4.class)
public class BreezeBalanceActivityTest {
	@ClassRule
	public static MockDataTestRule mockDataTestRule = new MockDataTestRule();
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

	@Test
	public void loadsBreezeBalancePage() {
		// when
		activityTestRule.launchActivity(new Intent(Intent.ACTION_MAIN));
		openActionBarOverflowOrOptionsMenu(activityTestRule.getActivity());
		onView(withText(R.string.menu_breeze_balance)).perform(click());
		// wait for page to load
		TestHelper.sleep(3000);

		// check that the input field is there
		onWebView().withElement(findElement(Locator.ID, "cardnumber"));
	}
}