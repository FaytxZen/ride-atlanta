package com.andrewvora.apps.rideatlanta.testing.assertions;

import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;

import org.hamcrest.Matcher;
import org.junit.Assert;

/**
 * Created on 2/11/2018.
 * @author Andrew Vorakrajangthiti
 */
public class RecyclerViewAssertions {
	public static ViewAssertion hasItemsCount(final int count) {
		return (view, e) -> {
			if (!(view instanceof RecyclerView)) {
				throw e;
			}
			RecyclerView rv = (RecyclerView) view;
			String errorMsg = String.format("Expected count: %d, found %d", count, rv.getAdapter().getItemCount());
			Assert.assertTrue(errorMsg, rv.getAdapter().getItemCount() == count);
		};
	}

	public static ViewAssertion hasHolderItemAtPosition(final int index,
	                                                    final Matcher<RecyclerView.ViewHolder> viewHolderMatcher) {
		return (view, e) -> {
			if (!(view instanceof RecyclerView)) {
				throw e;
			}
			RecyclerView rv = (RecyclerView) view;
			Assert.assertThat(rv.findViewHolderForAdapterPosition(index), viewHolderMatcher);
		};
	}
}
