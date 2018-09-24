package com.andrewvora.apps.rideatlanta.testing.matchers;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;

/**
 * Created on 2/11/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
public class RecyclerViewMatchers {

	public static Matcher<RecyclerView.ViewHolder> hasViewWithText(CharSequence text) {
		return new TypeSafeMatcher<RecyclerView.ViewHolder>() {
			@Override
			protected boolean matchesSafely(RecyclerView.ViewHolder item) {
				final ArrayList<View> matchedViews = new ArrayList<>();
				item.itemView.findViewsWithText(matchedViews, text, View.FIND_VIEWS_WITH_TEXT);

				return !matchedViews.isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("View holder does not have view with text '");
				description.appendText(text.toString());
				description.appendText("'");
			}
		};
	}

	public static Matcher<RecyclerView.ViewHolder> hasVisibleViewWithId(@IdRes int id) {
		return new TypeSafeMatcher<RecyclerView.ViewHolder>() {
			@Override
			protected boolean matchesSafely(RecyclerView.ViewHolder item) {
				final View view = item.itemView.findViewById(id);
				return view != null && view.getVisibility() == View.VISIBLE;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Could not find view with ID " + id);
			}
		};
	}

	public static Matcher<RecyclerView.ViewHolder> hasViewWithTextWithId(@IdRes int id) {
		return new TypeSafeMatcher<RecyclerView.ViewHolder>() {
			@Override
			protected boolean matchesSafely(RecyclerView.ViewHolder item) {
				final View view = item.itemView.findViewById(id);
				return view != null && !((TextView) view).getText().toString().isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Could not find text view with ID and non-empty string: " + id);
			}
		};
	}
}
