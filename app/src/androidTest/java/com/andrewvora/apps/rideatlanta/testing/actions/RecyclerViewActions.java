package com.andrewvora.apps.rideatlanta.testing.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

/**
 * Created on 2/11/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
public class RecyclerViewActions {
	public static ViewAction clickViewWithId(final int id) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return null;
			}

			@Override
			public String getDescription() {
				return "Click on a child view with specified id, " + id;
			}

			@Override
			public void perform(UiController uiController, View view) {
				View v = view.findViewById(id);
				v.performClick();
			}
		};
	}

}
